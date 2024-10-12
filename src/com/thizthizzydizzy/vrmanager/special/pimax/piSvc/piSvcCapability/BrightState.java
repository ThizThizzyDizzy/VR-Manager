package com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability;
public class BrightState{
    public static final int LEVEL_0 = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;
    public static final int LEVEL_4 = 4;
    public static final int LEVEL_5 = 5;
    public static final int LEVEL_6 = 6;
    public static final int LEVEL_7 = 7;
    public static final int LEVEL_8 = 8;
    public static final int LEVEL_9 = 9;
    public static final int LEVEL_10 = 10;
    public static final int LEVEL_11 = 11;
    public static final int LEVEL_12 = 12;
    public static final int LEVEL_INVALID = -1;
    public static String getName(int state){
        return switch(state){
            case 0 -> "LEVEL_0";
            case 1 -> "LEVEL_1";
            case 2 -> "LEVEL_2";
            case 3 -> "LEVEL_3";
            case 4 -> "LEVEL_4";
            case 5 -> "LEVEL_5";
            case 6 -> "LEVEL_6";
            case 7 -> "LEVEL_7";
            case 8 -> "LEVEL_8";
            case 9 -> "LEVEL_9";
            case 10 -> "LEVEL_10";
            case 11 -> "LEVEL_11";
            case 12 -> "LEVEL_12";
            case -1 -> "LEVEL_INVALID";

            default -> "UNKNOWN";
        };
    }
}
