package com.thizthizzydizzy.vrmanager.config.init;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.Pimax;
import com.thizthizzydizzy.vrmanager.special.Windows;
import com.thizthizzydizzy.vrmanager.task.WatcherTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class InitTask{
    public String title;
    public Type type = Type.RUN;
    public String target;
    public boolean startIndirect;
    public boolean forceShutdown;
    public ArrayList<String> arguments = new ArrayList<>();
    public void run(){
        switch(type){
            case PIMAX ->
                Pimax.init();
            case RUN -> {
                try{
                    for(var task : Windows.getTasks()){
                        if(task.imageName.equals(new File(target).getName())){
                            Logger.warn("WARNING: Found task "+task.imageName+" (PID "+task.pid+") already running! This task may not be managed properly by VR Manager!");
                        }
                    }
                }catch(IOException ex){
                }
                if(startIndirect){
                    VRManager.startIndirect(new File(target), arguments.toArray(String[]::new));
                    VRManager.startTask(new WatcherTask(new File(target).getName(), forceShutdown));
                }else
                    VRManager.start(new File(target), arguments.toArray(String[]::new));
            }
            case RUN_JAVA -> {
                arguments.add(0, "-jar");
                arguments.add(1, "\""+target+"\"");
                VRManager.startAt(new File(target).getAbsoluteFile().getParentFile(), "java", arguments.toArray(String[]::new));
            }
            case WATCH -> {
                VRManager.startTask(new WatcherTask(target, forceShutdown));
            }
            default ->
                Logger.error("Skipping invalid init task "+title+"! (Unrecognized type: "+type.toString()+")");
        }
    }
    public static enum Type{
        RUN, WATCH, RUN_JAVA, PIMAX;
    }
}
