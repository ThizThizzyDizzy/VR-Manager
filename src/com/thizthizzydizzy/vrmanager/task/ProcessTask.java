package com.thizthizzydizzy.vrmanager.task;
import com.thizthizzydizzy.vrmanager.Logger;
public class ProcessTask extends Task{
    public final Process process;
    private final boolean forceShutdown;
    public ProcessTask(String name, Process process, boolean forceShutdown){
        super(name==null?process.pid()+"":name);
        this.process = process;
        this.forceShutdown = forceShutdown;
    }
    @Override
    public boolean isActive(){
        return process.isAlive();
    }
    @Override
    public void shutdown(){
        Logger.info("Destroying process "+name+" with PID "+process.pid());
        if(forceShutdown)process.destroyForcibly();
        else
            process.destroy();
    }
}
