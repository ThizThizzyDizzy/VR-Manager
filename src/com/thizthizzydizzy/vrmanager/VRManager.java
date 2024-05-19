package com.thizthizzydizzy.vrmanager;
import com.thizthizzydizzy.vrmanager.task.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thizthizzydizzy.vrmanager.config.Configuration;
import com.thizthizzydizzy.vrmanager.config.init.InitTask;
import com.thizthizzydizzy.vrmanager.command.Command;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.module.VRModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
public class VRManager{
    public static HashSet<StartupFlags> flags = new HashSet<>();
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Configuration configuration;
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static boolean running = true;
    public static void main(String[] sysArgs){
        Logger.info("VR Manager starting up");
        for(String s : sysArgs){
            flags.add(StartupFlags.valueOf(s.toUpperCase(Locale.ROOT)));
        }
        Logger.info("Flags: "+flags.toString());
        Logger.info("Loading configuration");
        try{
            configuration = gson.fromJson(Files.readString(new File("config.json").toPath()), Configuration.class);
        }catch(NoSuchFileException _ignored){
            configuration = new Configuration();
        }catch(IOException ex){
            Logger.error("Failed to load configuration! Exiting...");
            return;
        }
        if(flags.contains(StartupFlags.GENCONFIG)){
            configuration = new Configuration();
            configuration.modules.add("pimax");
            {
                InitTask pimax = new InitTask();
                pimax.title = "Pimax";
                pimax.type = InitTask.Type.PIMAX;
                configuration.initialization.initTasks.add(pimax);
            }
            {
                InitTask vor = new InitTask();
                vor.title = "VRChat OSC Router";
                vor.target = "C:\\Program Files\\vor\\bin\\vor.exe";
                vor.arguments.add("-e");
                configuration.initialization.initTasks.add(vor);
            }
            {
                InitTask bHapticsOSC = new InitTask();
                bHapticsOSC.title = "bHapticsOSC";
                bHapticsOSC.target = "C:\\Users\\Thiz\\Desktop\\VR accessories\\OSC stuff\\bHapticsOSC.exe";
                configuration.initialization.initTasks.add(bHapticsOSC);
            }
            {
                InitTask bHapticsPlayer = new InitTask();
                bHapticsPlayer.title = "bHaptics Player";
                bHapticsPlayer.target = "C:\\Users\\Thiz\\AppData\\Local\\bHapticsPlayer\\BhapticsPlayer.exe";
                bHapticsPlayer.startIndirect = true;
                bHapticsPlayer.forceShutdown = true;
                configuration.initialization.initTasks.add(bHapticsPlayer);
            }
            {
                InitTask vrcFriendDatabase = new InitTask();
                vrcFriendDatabase.title = "VRC Friend Database";
                vrcFriendDatabase.type = InitTask.Type.RUN_JAVA;
                vrcFriendDatabase.target = "C:\\Users\\Thiz\\Desktop\\VR accessories\\VRC_Friend_Database.jar";
                configuration.initialization.initTasks.add(vrcFriendDatabase);
            }
            {
                InitTask vrcFaceTracking = new InitTask();
                vrcFaceTracking.title = "VRCFaceTracking";
                vrcFaceTracking.target = "C:\\Windows\\explorer.exe";
                vrcFaceTracking.arguments.add("\"shell:appsFolder\\96ba052f-0948-44d8-86c4-a0212e4ae047_d7rcq4vxghz0r!App\"");
                configuration.initialization.initTasks.add(vrcFaceTracking);

                InitTask vrcft = new InitTask();
                vrcft.type = InitTask.Type.WATCH;
                vrcft.target = "VRCFaceTracking.exe";
                configuration.initialization.initTasks.add(vrcft);
            }
            try{
                Files.writeString(new File("config.json").toPath(), gson.toJson(configuration));
            }catch(IOException ex){
                Logger.error("Unable to save configuration!", ex);
            }
        }
        if(!configuration.modules.isEmpty()){
            for(String key : configuration.modules){
                VRModule.setActive(key, true);
            }
            Logger.info("Loaded "+configuration.modules.size()+" modules");
        }
        if(!flags.contains(StartupFlags.NOGUI)){
            Logger.info("Starting GUI");
            ManagerGUI.start();
        }
        if(flags.contains(StartupFlags.INIT))init();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            CLI:
            while(running){
                System.out.print("> ");
                String line = reader.readLine();
                Logger.reset();
                if(line.isBlank())continue;
                String[] parts = line.split(" ");
                for(VRModule module : VRModule.activeModules){
                    if(parts[0].equals(module.getName())){
                        Command.chooseSubcommand(module.getName(), Command.trimArgument(parts), null, module.getCommands());
                        continue CLI;
                    }
                }
                Command.chooseCommand(parts[0], Command.trimArgument(parts), (base) -> "Unknown command: "+base,
                    new NamedCommand("exit", (base, arguments) -> {
                        HashSet<Character> flags = Command.getFlags(arguments, 's', 'f', 'r');
                        Runnable exitLoop = () -> {
                            if(flags.contains('s')){
                                for(int i = 0; i<tasks.size(); i++){
                                    Task task = tasks.get(i);
                                    if(task.isActive())task.shutdown();
                                }
                            }
                            if(flags.contains('f')&&!flags.contains('r')){
                                System.exit(0);
                            }else{
                                ArrayList<String> taskNames = new ArrayList<>();
                                for(int i = 0; i<tasks.size(); i++){
                                    Task task = tasks.get(i);
                                    if(task.isActive())taskNames.add(task.name);
                                }
                                if(!taskNames.isEmpty()){
                                    Logger.warn("There "+(taskNames.size()==1?"is":"are")+" "+taskNames.size()+" active "+(taskNames.size()==1?"task":"tasks")+":\n"
                                        +String.join(", ", taskNames)+(flags.isEmpty()?"\nUse with -s to shut down all tasks, -f to force shutdown, and -r to run recursively.":""));
                                }else{
                                    running = false;
                                }
                            }
                        };
                        int attempts = 0;
                        while(true){
                            int numTasks = 0;
                            for(int i = 0; i<tasks.size(); i++){
                                Task task = tasks.get(i);
                                if(task.isActive())numTasks++;
                            }
                            attempts++;
                            exitLoop.run();
                            if(!running)break;
                            if(!flags.contains('r')){
                                break;
                            }
                            int nowTasks = 0;
                            for(int i = 0; i<tasks.size(); i++){
                                Task task = tasks.get(i);
                                if(task.isActive())nowTasks++;
                            }
                            if(nowTasks==numTasks&&attempts>2){
                                if(flags.contains('f')){
                                    Logger.error("Warning: "+nowTasks+" tasks have not stopped after "+attempts+" attempts!");
                                    System.exit(0);
                                }else{
                                    Logger.error("Recursive shutdown cancelled! "+nowTasks+" tasks have not stopped after "+attempts+" attempts.");
                                }
                                break;
                            }
                        }
                    }),
                    new NamedCommand("module", Command.subcommand(null,
                        new NamedCommand("list", (base, args) -> {
                            ArrayList<String> active = new ArrayList<>();
                            ArrayList<String> inactive = new ArrayList<>();
                            for(var module : VRModule.activeModules)active.add(module.getName());
                            for(var module : VRModule.modules)inactive.add(module.getName());
                            inactive.removeAll(active);
                            Collections.sort(active);
                            Collections.sort(inactive);
                            Logger.info(active.size()+" active module"+(active.size()==1?"":"s")+": "+String.join(", ", active)+"\n"
                                +inactive.size()+" inactive module"+(inactive.size()==1?"":"s")+": "+String.join(", ", inactive));
                        }),
                        new NamedCommand("enable", (base, args) -> {
                            if(!Command.nArguments(base, args, 1))return;
                            for(var module : VRModule.modules){
                                if(module.getName().equals(args[0])){
                                    if(VRModule.activeModules.contains(module)){
                                        Logger.info("Module "+module.getName()+" is already enabled!");
                                        return;
                                    }
                                    VRModule.activeModules.add(module);
                                    Logger.info("Enabled module "+module.getName());
                                    return;
                                }
                            }
                            Logger.info("Invalid module: "+args[0]+"!");
                        }),
                        new NamedCommand("disable", (base, args) -> {
                            if(!Command.nArguments(base, args, 1))return;
                            for(var module : VRModule.modules){
                                if(module.getName().equals(args[0])){
                                    if(!VRModule.activeModules.contains(module)){
                                        Logger.info("Module "+module.getName()+" is not enabled!");
                                        return;
                                    }
                                    VRModule.activeModules.remove(module);
                                    Logger.info("Disabled module "+module.getName());
                                    return;
                                }
                            }
                            Logger.info("Invalid module: "+args[0]+"!");
                        })
                    ))
                );
            }
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    private static void init(){
        if(configuration.initialization.initTasks.isEmpty()){
            Logger.info("No init tasks have been specified! Skipping initialization.");
            return;
        }
        Logger.info("Initializing...");
        for(var task : configuration.initialization.initTasks)task.run();
    }
    public static Process startIndirect(File target, String... args){
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("/C");
        arguments.add("start");
        arguments.add("\""+target.getName()+"\"");
        arguments.add("/D");
        arguments.add(target.getAbsoluteFile().getParent());
        arguments.add(target.getAbsolutePath());
        arguments.addAll(Arrays.asList(args));
        return start("cmd.exe", arguments.toArray(String[]::new));
    }
    public static Process start(File target, String... args){
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(target.getAbsolutePath());
        arguments.addAll(Arrays.asList(args));
        Process p = null;
        try{
            Logger.info("Starting "+target.getAbsolutePath()+" with arguments "+Arrays.toString(args)+"...");
            p = new ProcessBuilder(arguments).directory(target.getAbsoluteFile().getParentFile()).start();
        }catch(IOException ex){
            Logger.error("Failed to start process "+target.getAbsolutePath()+"!", ex);
            return null;
        }
        watchTask(target.getName(), p);
        return p;
    }
    public static Process start(String target, String... args){
        return startAt(null, target, args);
    }
    public static Process startAt(File dir, String target, String... args){
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(target);
        arguments.addAll(Arrays.asList(args));
        Process p = null;
        try{
            Logger.info("Starting "+target+" with arguments "+Arrays.toString(args)+"...");
            var pb = new ProcessBuilder(arguments);
            if(dir!=null)pb.directory(dir);
            p = pb.start();
        }catch(IOException ex){
            Logger.error("Failed to start process "+target+" with arguments "+Arrays.toString(args)+"!", ex);
            return null;
        }
        watchTask(target, p);
        return p;
    }
    public static void watchTask(String name, Process p){
        startTask(new Task(name==null?p.pid()+"":name){
            @Override
            public boolean isActive(){
                return p.isAlive();
            }
            @Override
            public void shutdown(){
                Logger.info("Destroying process "+name+" with PID "+p.pid());
                p.destroy();
            }
        });
    }
    public static void startTask(Task task){
        tasks.add(task);
        task.start();
    }
    public enum StartupFlags{
        NOGUI, INIT, @Deprecated
        GENCONFIG
    }
}
