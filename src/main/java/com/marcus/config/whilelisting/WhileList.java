package com.marcus.config.whilelisting;

public class WhileList {
    // API white list
    public static final String[] API_WHITE_LIST = {"/api/**","/auth/**","/user/**"};
    
    // Web white list
    public static String[] WEB_WHITE_LIST = {"/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**"};
    
    // Client white list
    public static String[] CLIENT_WHITE_LIST = {"http://localhost:3000"};
    
}
