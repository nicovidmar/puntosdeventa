package com.costservice.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.costservice.dto.ShortestPathResponse;
import com.costservice.webclient.WebClientPosService;

@Service
public class CostService {

    private static final String COSTS_HASH_KEY = "costs";

    private final WebClientPosService posService;
    private final RedisTemplate<String, Integer> redisTemplate;

    public CostService(RedisTemplate<String, Integer> redisTemplate, WebClientPosService posService) {
        this.redisTemplate = redisTemplate;
        this.posService = posService;
    }

    private String buildKey(int idA, int idB) {
        return idA + "_" + idB;
    }

    public boolean save(int idA, int idB, int cost) {
        checkPosExists(idA);
        checkPosExists(idB);

        if (cost < 0)
            throw new IllegalArgumentException("El costo no puede ser negativo");
        if (idA == idB && cost != 0)
            throw new IllegalArgumentException("Costo de un punto al mismo punto no puede ser distinto de 0");

        String key = buildKey(idA, idB);
        String reverseKey = buildKey(idB, idA);

        boolean alreadyExists = redisTemplate.opsForHash().hasKey(COSTS_HASH_KEY, key);

        redisTemplate.opsForHash().put(COSTS_HASH_KEY, key, cost);
        redisTemplate.opsForHash().put(COSTS_HASH_KEY, reverseKey, cost);

        return !alreadyExists; // true si es nuevo, false si es update
    }

    public void deleteCost(int idA, int idB) {
        checkPosExists(idA);
        checkPosExists(idB);

        redisTemplate.opsForHash().delete(COSTS_HASH_KEY, buildKey(idA, idB));
        redisTemplate.opsForHash().delete(COSTS_HASH_KEY, buildKey(idB, idA));
    }

    public Map<Integer, Integer> getDirectConnectionsFrom(int id) {
        checkPosExists(id);

        Map<Object, Object> allCosts = redisTemplate.opsForHash().entries(COSTS_HASH_KEY);
        Map<Integer, Integer> result = new HashMap<>();

        for (Map.Entry<Object, Object> entry : allCosts.entrySet()) {
            String key = (String) entry.getKey();
            if (key.startsWith(id + "_")) {
                int idB = Integer.parseInt(key.split("_")[1]);
                result.put(idB, (Integer) entry.getValue());
            }
        }

        return result;
    }

    public ShortestPathResponse getShortestPath(int idA, int idB) {
        checkPosExists(idA);
        checkPosExists(idB);

        // Mapa para guardar la distancia mínima conocida desde idA hacia cada nodo
        Map<Integer, Integer> distances = new HashMap<>();
        // Mapa para guardar el nodo anterior en el camino óptimo hacia cada nodo
        Map<Integer, Integer> previous = new HashMap<>();
        // Conjunto para marcar los nodos ya visitados
        Set<Integer> visited = new HashSet<>();
        // Cola de prioridad para seleccionar el nodo con menor costo acumulado
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        // Inicializar el nodo de origen con costo 0
        distances.put(idA, 0);
        queue.add(new int[] { idA, 0 });

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentId = current[0];

            // Si ya fue visitado, lo salteamos
            if (!visited.add(currentId))
                continue;

            // Si llegamos al destino, terminamos
            if (currentId == idB)
                break;

            // Obtener conexiones directas del nodo actual
            Map<Integer, Integer> neighbors = getDirectConnectionsFrom(currentId);
            for (var entry : neighbors.entrySet()) {
                int neighbor = entry.getKey();
                int cost = entry.getValue();
                if (visited.contains(neighbor))
                    continue;

                int newDist = distances.get(currentId) + cost;

                // Si hay un camino más corto hacia el vecino
                if (newDist < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    // guarda nuevo costo mínimo para llegar hasta neighbor
                    distances.put(neighbor, newDist);
                    // registra que para llegar a neighbor desde el origen, el paso anterior óptimo
                    // fue currentId
                    previous.put(neighbor, currentId);
                    // agrega este vecino a la cola para seguir procesando su camino desde ese punto
                    // con el costo acumulado
                    queue.add(new int[] { neighbor, newDist });
                }
            }
        }

        // Si no hay ruta al destino
        if (!distances.containsKey(idB)) {
            throw new IllegalArgumentException("No hay camino entre los puntos de venta");
        }

        // reconstrucción del camino
        List<String> path = new LinkedList<>();
        for (Integer at = idB; at != null; at = previous.get(at)) {
            String name = posService.findById(at).getName();
            path.add(0, name);
        }

        String fromName = posService.findById(idA).getName();
        String toName = posService.findById(idB).getName();

        return new ShortestPathResponse(fromName, toName, distances.get(idB), path);
    }

    private void checkPosExists(int id) {
        posService.findById(id);
    }

}
