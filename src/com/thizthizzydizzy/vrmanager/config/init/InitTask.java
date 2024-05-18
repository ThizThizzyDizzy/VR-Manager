package com.thizthizzydizzy.vrmanager.config.init;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.Windows;
import com.thizthizzydizzy.vrmanager.task.WatcherTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class InitTask{
    public String title;
    public Type type = Type.RUN;
    public String target;
    public ArrayList<String> arguments = new ArrayList<>();
    public void run(){
        switch(type){
            case RUN -> {
                try{
                    for(var task : Windows.getTasks()){
                        if(task.imageName.equals(new File(target).getName())){
                            Logger.warn("WARNING: Found task "+task.imageName+" (PID "+task.pid+") already running! This task may not be managed properly by VR Manager!");
                        }
                    }
                }catch(IOException ex){}
                VRManager.start(new File(target), arguments.toArray(String[]::new));
            }
            case WATCH -> {
                VRManager.startTask(new WatcherTask(target));
            }
            default -> Logger.error("Skipping invalid init task "+title+"! (Unrecognized type: "+type.toString()+")");
        }
    }
    public static enum Type{
        RUN, WATCH, RUN_JAVA, PIMAX;
    }
}
