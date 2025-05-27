package com.cavallaro.kafka.cache;
import java.util.List;


public interface CacheableService <T, ID>{


    List<T> getAllCached();

    void save(T entity);

    void remove(ID id);

    void update(ID id, T entity);
}
