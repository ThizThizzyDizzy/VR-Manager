package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.config.module.ProcessManagerConfiguration;
import com.thizthizzydizzy.vrmanager.task.ProcessTask;
import com.thizthizzydizzy.vrmanager.task.WatcherTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class ProcessManager{
    public static final ArrayList<ProcessTask> processes = new ArrayList<>();
    public static final ArrayList<WatcherTask> indirectProcesses = new ArrayList<>();
    public static void start(ProcessManagerConfiguration.ProcessConfiguration process){
        if(process.isWindowsApp){
            Logger.push(ProcessManager.class);
            VRManager.startIndirect(new File("C:\\Windows\\explorer.exe"), "\"shell:appsFolder\\"+process.target+"!App\"");
            indirectProcesses.add(VRManager.startTask(new WatcherTask(process.exeName, process.forceShutdown)));
            Logger.pop();
        }else{
            start(process.startLocation, process.target, process.startIndirect, process.forceShutdown, process.arguments.toArray(String[]::new));
        }
    }
    public static void start(String startLocation, String target, boolean indirect, boolean forceShutdown, String... args){
        Logger.push(ProcessManager.class);
        File f = new File(target);
        if(indirect){
            VRManager.startIndirect(f, args);
            Logger.info("Started process "+f.getName()+" (INDIRECT)");
            indirectProcesses.add(VRManager.startTask(new WatcherTask(f.getName(), forceShutdown)));
        }else{
            try{
                var process = startLocation==null?VRManager.start(f, args):VRManager.startAt(new File(startLocation), target, args);
                Logger.info("Started process "+f.getName()+" (PID "+process.pid()+")");
                processes.add(VRManager.startTask(new ProcessTask(f.getName(), process, forceShutdown)));
            }catch(IOException ex){
                Logger.error("Could not start process "+f.getName()+"!", ex);
            }
        }
        Logger.pop();
    }
}
