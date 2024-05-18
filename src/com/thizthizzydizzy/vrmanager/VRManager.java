package com.thizthizzydizzy.vrmanager;
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
    private static Configuration configuration;
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
                pimax.title = "Pimax Client";
                pimax.target = "C:\\Program Files\\Pimax\\PimaxClient\\pimaxui\\PimaxClient.exe";
                configuration.initialization.initTasks.add(pimax);
                //TODO wait for startup & connection, restart headset, verify passthrough and whatnot
                InitTask deviceSetting = new InitTask();
                deviceSetting.type = InitTask.Type.WATCH;
                deviceSetting.target = "DeviceSetting.exe";
                configuration.initialization.initTasks.add(deviceSetting);

                InitTask piVST = new InitTask();
                piVST.type = InitTask.Type.WATCH;
                piVST.target = "pi_vst.exe";
                configuration.initialization.initTasks.add(piVST);

                InitTask piServer = new InitTask();
                piServer.type = InitTask.Type.WATCH;
                piServer.target = "pi_server.exe";
                configuration.initialization.initTasks.add(piServer);

                InitTask piOverlay = new InitTask();
                piOverlay.type = InitTask.Type.WATCH;
                piOverlay.target = "pi_overlay.exe";
                configuration.initialization.initTasks.add(piOverlay);

                InitTask piPlatformService = new InitTask();
                piPlatformService.type = InitTask.Type.WATCH;
                piPlatformService.target = "PiPltformService_64.exe";
                configuration.initialization.initTasks.add(piPlatformService);

                InitTask platformRuntime = new InitTask();
                platformRuntime.type = InitTask.Type.WATCH;
                platformRuntime.target = "platform_runtime_VR4PIMAXP3B_service.exe";
                configuration.initialization.initTasks.add(platformRuntime);
            }
            {
                InitTask steamVR = new InitTask();
                steamVR.title = "SteamVR";
                steamVR.target = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\SteamVR\\bin\\win64\\vrstartup.exe";
                configuration.initialization.initTasks.add(steamVR);

                InitTask vrServer = new InitTask();
                vrServer.type = InitTask.Type.WATCH;
                vrServer.target = "vrserver.exe";
                configuration.initialization.initTasks.add(vrServer);

                InitTask vrMonitor = new InitTask();
                vrMonitor.type = InitTask.Type.WATCH;
                vrMonitor.target = "vrmonitor.exe";
                configuration.initialization.initTasks.add(vrMonitor);

                InitTask vrDashboard = new InitTask();
                vrDashboard.type = InitTask.Type.WATCH;
                vrDashboard.target = "vrdashboard.exe";
                configuration.initialization.initTasks.add(vrDashboard);

                InitTask vrCompositor = new InitTask();
                vrCompositor.type = InitTask.Type.WATCH;
                vrCompositor.target = "vrcompositor.exe";
                configuration.initialization.initTasks.add(vrCompositor);
            }
            {
                InitTask vor = new InitTask();
                vor.title = "VRChat OSC Router";
                vor.target = "C:\\Program Files\\vor\\bin\\vor.exe";
                vor.arguments.add("-e");
                configuration.initialization.initTasks.add(vor);
            }
            {
                InitTask vrcFaceTracking = new InitTask();
                vrcFaceTracking.title = "VRCFaceTracking";
                vrcFaceTracking.target = "C:\\Program Files\\WindowsApps\\96ba052f-0948-44d8-86c4-a0212e4ae047_5.1.1.0_x64__d7rcq4vxghz0r\\VRCFaceTracking.exe";
                configuration.initialization.initTasks.add(vrcFaceTracking);

                InitTask sr = new InitTask();
                sr.type = InitTask.Type.WATCH;
                sr.target = "sr_runtime.exe";
                configuration.initialization.initTasks.add(sr);

                InitTask sRanipal = new InitTask();
                sRanipal.type = InitTask.Type.WATCH;
                sRanipal.target = "SRanipalService.exe";
                configuration.initialization.initTasks.add(sRanipal);
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
                configuration.initialization.initTasks.add(bHapticsPlayer);
            }
            {
                InitTask vrcFriendDatabase = new InitTask();
                vrcFriendDatabase.title = "VRC Friend Database";
                vrcFriendDatabase.type = InitTask.Type.RUN_JAVA;
                vrcFriendDatabase.target = "C:\\Users\\Thiz\\Desktop\\VR accessories\\VRC_Friend_Database.jar";
                configuration.initialization.initTasks.add(vrcFriendDatabase);
            }
            try{
                Files.writeString(new File("config.json").toPath(), gson.toJson(configuration));
            }catch(IOException ex){

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
                        HashSet<String> flags = Command.getFlags(arguments, "s", "f");
                        if(flags.contains("s")){
                            for(Task task : tasks)if(task.isActive())task.shutdown();
                        }
                        if(flags.contains("f")){
                            System.exit(0);
                        }else{
                            int active = 0;
                            for(Task task : tasks)if(task.isActive())active++;
                            if(active>0){
                                Logger.warn("There are "+active+" active "+(active==1?"task":"tasks")+"! Use with -s to shut down all tasks, or with -f to force shutdown");
                            }else{
                                running = false;
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
    public static Process start(File target, String... args){
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(target.getAbsolutePath());
        arguments.addAll(Arrays.asList(args));
        Process p;
        try{
            p = new ProcessBuilder(arguments).directory(target.getAbsoluteFile().getParentFile()).start();
        }catch(IOException ex){
            Logger.error("Failed to start process "+target.getAbsolutePath()+" with arguments "+Arrays.toString(args)+"!", ex);
            return null;
        }
        Thread t = new Thread(() -> {
//            while(p.isAlive()){
//            }
        }, "Task thread: "+target.getName());
        t.start();
        return p;
    }
    public static void addTask(Task task){
        tasks.add(task);
    }
    public enum StartupFlags{
        NOGUI, INIT, @Deprecated
        GENCONFIG
    }
}