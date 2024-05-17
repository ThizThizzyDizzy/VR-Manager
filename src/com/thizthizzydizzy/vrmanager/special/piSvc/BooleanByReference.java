package com.thizthizzydizzy.vrmanager.special.piSvc;
import com.sun.jna.ptr.IntByReference;
public class BooleanByReference extends IntByReference{
    public boolean getVal(){
        return super.getValue()>0;
    }
}
