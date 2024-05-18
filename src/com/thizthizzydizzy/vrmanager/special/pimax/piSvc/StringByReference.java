package com.thizthizzydizzy.vrmanager.special.pimax.piSvc;
import com.sun.jna.Memory;
public class StringByReference extends Memory{
    public StringByReference(){
        super(1);
    }
    public String getVal(){
        return getString(0);
    }
}
