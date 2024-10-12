package com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability;
public class ToneState{
    public static final int TONE_WARM = 0x00;
    public static final int TONE_COOL = 0x01;
//    public static final int TONE_UNKNOW;
    public static String getName(int state){
        return switch(state){
            case 0 -> "TONE_WARM";
            case 1 -> "TONE_COOL";

            default -> "TONE_UNKNOW";
        };
    }
}
