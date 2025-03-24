package com.marcus.config.whilelisting;

public class WhileList {
    public static final String[] API_WHITE_LIST = {"/api/**","/auth/**","/user/**"};
    public static String[] WEB_WHITE_LIST = {"/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**"};
    public static String[] CLIENT_WHITE_LIST = {"http://localhost:3000"};
}
