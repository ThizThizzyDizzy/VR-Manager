package com.thizthizzydizzy.vrmanager.special.pimax;
public class PiSvcConfig{
    public String key;
    public Type type;
    public boolean writable;
    public String description;
    public float min;
    public float max;
    public boolean hasDefinedRange;
    public PiSvcConfig(String key, Type type, String description){
        this(key, type, true, description);
    }
    public PiSvcConfig(String key, Type type, boolean writable, String description){
        this.key = key;
        this.type = type;
        this.writable = writable;
        this.description = description;
    }
    public PiSvcConfig(String key, Type type, String description, float min, float max){
        this(key, type, description);
        hasDefinedRange = true;
        this.min = min;
        this.max = max;
    }
    @Override
    public String toString(){
        return key+" ("+type.toString()+") "+description+(writable?"":"(Read only)");
    }
    public static enum Type{
        INT,
        FLOAT,
        STRING,
        VECTOR3F
    }
}
