package com.thizthizzydizzy.vrmanager.special.piSvc.piSvcDesc;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
public class piVector3f extends Structure{
    public float x, y, z;
    public piVector3f(){
        super();
    }
    public piVector3f(Pointer p){
        super(p);
        read();
    }
    @Override
    protected List<String> getFieldOrder(){
        return Arrays.asList("x", "y", "z");
    }
}
