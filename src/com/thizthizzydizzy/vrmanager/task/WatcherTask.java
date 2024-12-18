package com.thizthizzydizzy.vrmanager.task;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.Windows;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
public class WatcherTask extends Task{
    private Thread watcher;
    private final String target;
    private HashSet<Long> pids = new HashSet<>();
    private ArrayList<Task> subtasks = new ArrayList<>();
    private final boolean force;
    public WatcherTask(String title, String target, boolean force){
        super(title==null?"Watcher: "+target:title);
        this.target = target;
        this.force = force;
    }
    public WatcherTask(String target, boolean force){
        this(null, target, force);
    }
    @Override
    public boolean isActive(){
        return watcher!=null&&watcher.isAlive();
    }
    @Override
    public void start(){
        watcher = new Thread(() -> {
            Logger.push("Watcher");
            while(true){
                try{
                    for(var task : Windows.getTasks()){
                        if(task.imageName.equals(target)){
                            if(!pids.contains(task.pid)){
                                pids.add(task.pid);
                                var subtask = new Task(target){
                                    @Override
                                    public boolean isActive(){
                                        try{
                                            for(var t : Windows.getTasks()){
                                                if(t.pid==task.pid)return true;
                                            }
                                        }catch(IOException ex){
                                            Logger.error("Could not list tasks!", ex);
                                        }
                                        return false;
                                    }
                                    @Override
                                    public void shutdown(){
                                        Windows.runCommand("taskkill /PID "+task.pid+(force?" /F":""));
                                    }
                                };
                                VRManager.startTask(subtask);
                                subtasks.add(subtask);
                            }
                        }
                    }
                }catch(IOException ex){
                    Logger.error("Could not list tasks!", ex);
                }
                try{
                    Thread.sleep(10000);
                }catch(InterruptedException ex){
                    break; // task has stopped, shutting down
                }
            }
            Logger.pop();
        }, "Watcher Thread: "+target);
        watcher.start();
    }
    @Override
    public void shutdown(){
        for(var task : subtasks){
            task.shutdown();
        }
        watcher.interrupt();
    }
}
