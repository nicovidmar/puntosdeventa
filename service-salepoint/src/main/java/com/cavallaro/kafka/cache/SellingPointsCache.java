package com.cavallaro.kafka.cache;


import com.cavallaro.kafka.dto.SellingPointDTO;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service("SellingPointsCache")
@AllArgsConstructor
public class SellingPointsCache implements CacheableService<SellingPointDTO, Integer> {

    private final String SELLING_POINTS_CACHE = "sellingPoints";

    private final CacheManager cacheManager;

    @Override
    public List<SellingPointDTO> getAllCached() {
        return cacheManager.getCache(SELLING_POINTS_CACHE).get("all", List.class);

    }

    @Override
    public void save(SellingPointDTO entity) {
        List<SellingPointDTO> cachedList = getAllCached();

        if (cachedList != null) {
            cachedList.add(entity);
            Objects.requireNonNull(cacheManager.getCache(SELLING_POINTS_CACHE)).put("all", cachedList);
        }
    }

    @Override
    public void remove(Integer id) {
        List<SellingPointDTO>  cachedList = getAllCached();
        if (cachedList != null) {
            cachedList.removeIf(sp -> sp.getId().equals(id));
            Objects.requireNonNull(cacheManager.getCache(SELLING_POINTS_CACHE)).put("all", cachedList);
        }
    }

    @Override
    public void update(Integer id, SellingPointDTO entity) {
        List<SellingPointDTO>  cachedList = getAllCached();
        if (cachedList != null) {
            cachedList.forEach( sp ->{
                if(Objects.equals(sp.getId(), id)){
                    sp.setName(entity.getName());
                }
            });

            Objects.requireNonNull(cacheManager.getCache(SELLING_POINTS_CACHE)).put("all", cachedList);
        }
    }
}