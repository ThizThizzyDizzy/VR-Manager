package com.thizthizzydizzy.vrmanager.task;
public abstract class Task{
    public final String name;
    public Task(String name){
        this.name = name;
    }
    public abstract boolean isActive();
    public void start(){
    }
    public abstract void shutdown();
}
