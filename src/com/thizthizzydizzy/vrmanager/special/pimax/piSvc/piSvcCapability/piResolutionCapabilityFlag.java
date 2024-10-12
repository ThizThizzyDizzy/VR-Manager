package com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability;
public class piResolutionCapabilityFlag{
    public static final int e_1080_1920 = 0x01;
    public static final int e_1440_2560 = 0x02;
    public static final int e_1200_2160 = 0x04;
    public static final int e_2160_3840 = 0x08;
//    public static final int e_unknow;
    public static String getName(int resolution){
        return switch(resolution){
            case 1 -> "e_1080_1920";
            case 2 -> "e_1440_2560";
            case 4 -> "e_1200_2160";
            case 8 -> "e_2160_3840";

            default -> "e_unknow";
        };
    }
}
