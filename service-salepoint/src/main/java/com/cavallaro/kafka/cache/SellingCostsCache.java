package com.cavallaro.kafka.cache;


import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.model.SellingCostId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("SellingCostsCache")
@AllArgsConstructor
@Slf4j
public class SellingCostsCache implements CacheableService<SellingCostDTO, SellingCostId> {
    private final String SELLING_COSTS_CACHE = "sellingCosts";
    private final CacheManager cacheManager;

    @Override
    public List<SellingCostDTO> getAllCached() {
        return cacheManager.getCache(SELLING_COSTS_CACHE).get("all", List.class);
    }

    @Override
    public void save(SellingCostDTO entity) {

        List<SellingCostDTO> cachedList = getAllCached();

        if (cachedList != null) {
            cachedList.add(entity);
            Objects.requireNonNull(cacheManager.getCache(SELLING_COSTS_CACHE)).put("all", cachedList);
        }


    }

    @Override
    public void remove(SellingCostId sellingCostId) {

        List<SellingCostDTO> cachedList = getAllCached();
        if (cachedList != null) {
            cachedList.removeIf(sp -> Objects.equals(sp.getId() ,sellingCostId)      );
            Objects.requireNonNull(cacheManager.getCache(SELLING_COSTS_CACHE)).put("all", cachedList);
        }
    }

    @Override
    public void update(SellingCostId sellingCostId, SellingCostDTO entity) {
        List<SellingCostDTO> cachedList = getAllCached();
        if (cachedList != null) {
            cachedList.forEach(sp -> {
                if (Objects.equals(sp.getId(), sellingCostId)) {
                    sp.setCost(entity.getCost());
                }
            });

            Objects.requireNonNull(cacheManager.getCache(SELLING_COSTS_CACHE)).put("all", cachedList);
        }
    }
}