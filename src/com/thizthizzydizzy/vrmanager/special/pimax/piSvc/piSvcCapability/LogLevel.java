package com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability;
public class LogLevel{
    public static final int LOG_NONE = 0; //output nothing
    public static final int LOG_NORMAL = 1;
    public static final int LOG_DEBUG = 2;
    public static String getName(int state){
        return switch(state){
            case 0 -> "LOG_NONE";
            case 1 -> "LOG_NORMAL";
            case 2 -> "LOG_DEBUG";

            default -> "UNKNOWN";
        };
    }
}
