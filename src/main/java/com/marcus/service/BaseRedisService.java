package com.marcus.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BaseRedisService {
    void set(String key, String value); // save key and value into redis 

    Object get(String key);// get field
    
    void delete(String key);
    
    void delete(String key, String field);
    
    void delete(String key, List<String> fields);
    
}
