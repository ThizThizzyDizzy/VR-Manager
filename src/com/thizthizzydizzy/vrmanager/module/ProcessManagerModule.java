package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.command.CommandUtil;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.command.NamedCommandWithFlags;
import com.thizthizzydizzy.vrmanager.gui.module.ConfigureProcessManagerGUI;
import com.thizthizzydizzy.vrmanager.special.ProcessManager;
import com.thizthizzydizzy.vrmanager.special.Windows;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JDialog;
import javax.swing.JFrame;
public class ProcessManagerModule extends VRModule{
    @Override
    public String getName(){
        return "process";
    }
    @Override
    public NamedCommand[] getCommands(){
        return CommandUtil.subcommands(
            new NamedCommandWithFlags("start", (base, args, flags) -> {
                if(!CommandUtil.nArguments(base, args, 1))return;
                String target = args[0];
                try{
                    for(var task : Windows.getTasks()){
                        if(task.imageName.equals(new File(target).getName())){
                            Logger.warn("WARNING: Found task "+task.imageName+" (PID "+task.pid+") already running! This task may not be managed properly by VR Manager!");
                        }
                    }
                }catch(IOException ex){
                    Logger.warn("WARNING: Could not check for running tasks!", ex);
                }
                ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
                arguments.remove(0);
                ProcessManager.start(new File(target).getName(), null, target, flags.contains('i'), flags.contains('f'), arguments.toArray(String[]::new));
            }).addFlag('i', "Start Indirect").addFlag('f', "Requires Forced Shutdown"),
            new NamedCommand("list", (base, args) -> {
                CommandUtil.noArguments(base, args);
                Logger.info(ProcessManager.processes.size()+" Directly Managed Process"+(ProcessManager.processes.size()==1?"":"es"));
                for(var process : ProcessManager.processes){
                    Logger.info("- "+process.name+" ("+process.process.pid()+")"+(process.process.isAlive()?"":" - STOPPED"));
                }
                Logger.info(ProcessManager.indirectProcesses.size()+" Indirectly Managed Process"+(ProcessManager.indirectProcesses.size()==1?"":"es"));
                for(var watcher : ProcessManager.indirectProcesses){
                    Logger.info("- "+watcher.name);
                }
            })
        );
    }
    @Override
    public void init(){
        for(var process : VRManager.configuration.processManager.processes){
            ProcessManager.start(process);
        }
    }
    @Override
    public boolean hasConfiguration(){
        return true;
    }
    @Override
    public JDialog getConfigurationGUI(JFrame parent){
        return new ConfigureProcessManagerGUI(parent);
    }

}
