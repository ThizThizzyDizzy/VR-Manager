package com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
public class piVector3f extends Structure{
    public float x, y, z;
    public piVector3f(){
        super();
    }
    public piVector3f(float x, float y, float z){
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public piVector3f(Pointer p){
        super(p);
        read();
    }
    @Override
    protected List<String> getFieldOrder(){
        return Arrays.asList("x", "y", "z");
    }
    @Override
    public boolean equals(Object o){
        return o instanceof piVector3f vec&&vec.x==x&&vec.y==y&&vec.z==z;
    }
    @Override
    public String toString(){
        return "("+x+", "+y+", "+z+")";
    }
}
