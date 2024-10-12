package com.thizthizzydizzy.vrmanager;
import com.thizthizzydizzy.vrmanager.gui.ManagerGUI;
import com.thizthizzydizzy.vrmanager.task.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thizthizzydizzy.vrmanager.config.Configuration;
import com.thizthizzydizzy.vrmanager.command.CommandUtil;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.config.module.ProcessManagerConfiguration;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piVector3fAdapter;
import com.thizthizzydizzy.vrmanager.module.VRModule;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
public class VRManager{
    public static HashSet<StartupFlags> flags = new HashSet<>();
    public static Gson gson = new GsonBuilder().registerTypeAdapter(piVector3f.class, new piVector3fAdapter()).setPrettyPrinting().create();
    public static Configuration configuration;
    public static final ArrayList<Task> tasks = new ArrayList<>();
    public static boolean running = true;
    public static void main(String[] sysArgs){
        Logger.info("VR Manager starting up");
        for(String s : sysArgs){
            flags.add(StartupFlags.valueOf(s.toUpperCase(Locale.ROOT)));
        }
        Logger.info("Flags: "+flags.toString());
        Logger.info("Loading configuration");
        File configFile = new File("config.json");
        try{
            configuration = gson.fromJson(Files.readString(configFile.toPath()), Configuration.class);
        }catch(NoSuchFileException _ignored){
            configuration = new Configuration();
        }catch(IOException ex){
            configuration = null;
        }
        if(configuration==null){
            boolean reset = false;
            if(!flags.contains(StartupFlags.NOGUI)){
                int option = JOptionPane.showOptionDialog(null, "Failed to load settings!\nReset configuration?", "VR Manager", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Exit", "Reset Configuration"}, "Reset Configuration");
                reset = option==1;
            }
            if(!reset){
                Logger.error("Failed to load configuration! Exiting...");
                return;
            }
            configuration = new Configuration();
            configFile.delete();
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
                for(VRModule module : VRModule.modules){
                    if(parts[0].equals(module.getName())){
                        CommandUtil.chooseSubcommand(module.getName(), CommandUtil.trimArgument(parts), null, module.getCommands());
                        continue CLI;
                    }
                }
                CommandUtil.chooseCommand(parts[0], CommandUtil.trimArgument(parts), (base) -> "Unknown command: "+base,
                    new NamedCommand("init", (base, args) -> {
                        init();//TODO prevent multiple inits
                    }),
                    new NamedCommand("autoconfig", (base, args) -> {
                        configuration = autoConfig(null);
                        saveConfig();
                    }),
                    new NamedCommand("save", (base, args) -> saveConfig()),
                    new NamedCommand("exit", (base, arguments) -> {
                        HashSet<Character> flags = CommandUtil.getFlags(arguments, 's', 'f', 'r');
                        shutdown(flags.contains('s'), flags.contains('f'), flags.contains('r'), true);
                    })
                );
            }
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public static void init(){
        Logger.push(VRManager.class);
        Logger.info("Initializing...");
        for(var module : configuration.modules){
            VRModule m = VRModule.get(module);
            if(m!=null){
                Logger.info("Initializing Module: "+module);
                m.init();
            }else{
                Logger.error("Could not find module: "+module);
            }
        }
        Logger.pop();
    }
    public static void startIndirect(File target, String... args){
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("/C");
        arguments.add("start");
        arguments.add("\""+target.getName()+"\"");
        arguments.add("/D");
        arguments.add(target.getAbsoluteFile().getParent());
        arguments.add(target.getAbsolutePath());
        arguments.addAll(Arrays.asList(args));
        start("cmd.exe", arguments.toArray(String[]::new)); // this process has no reason to exist
    }
    public static Process start(File target, String... args) throws IOException{
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(target.getAbsolutePath());
        arguments.addAll(Arrays.asList(args));
        Process p = null;
        Logger.info("Starting "+target.getAbsolutePath()+" with arguments "+Arrays.toString(args)+"...");
        p = new ProcessBuilder(arguments).directory(target.getAbsoluteFile().getParentFile()).start();
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
        return p;
    }
    public static <T extends Task> T startTask(T task){
        tasks.add(task);
        task.start();
        return task;
    }
    public static Configuration autoConfig(Consumer<String> logCallback){
        var configuration = new Configuration();
        configuration.enableTelemetry = VRManager.configuration.enableTelemetry;//remember telemetry setting
        Consumer<String> log = (str) -> {
            Logger.info(str);
            if(logCallback!=null)logCallback.accept(str);
        };
        boolean usb = false;
        log.accept("Generating automatic configuration...");
        if(new File(System.getenv("PROGRAMFILES"), "Pimax\\Runtime\\DeviceSetting.exe").exists()){
            configuration.modules.add("pimax");
            usb = true;
            log.accept("Detected Pimax");
        }
        if(new File(System.getenv("PROGRAMFILES"), "vor\\bin\\vor.exe").exists()){
            ProcessManagerConfiguration.ProcessConfiguration vor = new ProcessManagerConfiguration.ProcessConfiguration();
            vor.title = "VRChat OSC Router";
            vor.target = System.getenv("PROGRAMFILES")+"\\vor\\bin\\vor.exe";
            vor.arguments.add("-e");
            configuration.processManager.processes.add(vor);
            log.accept("Detected VRChat OSC Router");
        }
        if(new File(System.getenv("LOCALAPPDATA"), "bHapticsPlayer\\BhapticsPlayer.exe").exists()){
            ProcessManagerConfiguration.ProcessConfiguration bHapticsPlayer = new ProcessManagerConfiguration.ProcessConfiguration();
            bHapticsPlayer.title = "bHaptics Player";
            bHapticsPlayer.target = System.getenv("LOCALAPPDATA")+"\\bHapticsPlayer\\BhapticsPlayer.exe";
            bHapticsPlayer.startIndirect = true;
            bHapticsPlayer.forceShutdown = true;
            configuration.processManager.processes.add(bHapticsPlayer);
            log.accept("Detected bHaptics Player");
        }
        if(new File(System.getenv("LOCALAPPDATA"), "Packages\\96ba052f-0948-44d8-86c4-a0212e4ae047_d7rcq4vxghz0r").exists()){
            ProcessManagerConfiguration.ProcessConfiguration vrcft = new ProcessManagerConfiguration.ProcessConfiguration();
            vrcft.isWindowsApp = true;
            vrcft.title = "VRCFaceTracking";
            vrcft.target = "96ba052f-0948-44d8-86c4-a0212e4ae047_d7rcq4vxghz0r";
            vrcft.exeName = "VRCFaceTracking.exe";
            configuration.processManager.processes.add(vrcft);
            log.accept("Detected VRCFaceTracking");
        }
        if(new File(System.getenv("LOCALAPPDATA"), "Packages\\OWO-Desktop_kn5h6p6y0g1fc").exists()){
            ProcessManagerConfiguration.ProcessConfiguration owo = new ProcessManagerConfiguration.ProcessConfiguration();
            owo.isWindowsApp = true;
            owo.title = "OWO";
            owo.target = "OWO-Desktop_kn5h6p6y0g1fc";
            owo.exeName = "OWO_Desktop.exe";
            owo.startIndirect = true;
            owo.forceShutdown = true;
            configuration.processManager.processes.add(owo);
            log.accept("Detected OWO");
        }
        if(new File(System.getenv("PROGRAMFILES(x86)"), "KAT Gateway\\KAT Gateway.exe").exists()){
            ProcessManagerConfiguration.ProcessConfiguration katVR = new ProcessManagerConfiguration.ProcessConfiguration();
            katVR.title = "KatVR Gateway";
            katVR.target = System.getenv("PROGRAMFILES(x86)")+"\\KAT Gateway\\KAT Gateway.exe";
            katVR.startIndirect = false;
            katVR.forceShutdown = false;
            configuration.processManager.processes.add(katVR);
            log.accept("Detected Kat Gateway");
        }
        if(usb){
            log.accept("At least one detected service supports USB tracking, adding USB Module");
            configuration.modules.add(0, "usb");//add at the very beginning, so it can monitor startup & shutdown
        }
        if(configuration.processManager.processes.size()>0)configuration.modules.add("process");
        return configuration;
    }
    public static void saveConfig(){
        try{
            Files.writeString(new File("config.json").toPath(), gson.toJson(configuration));
            Logger.info("Saved configuration");
        }catch(IOException ex){
            Logger.error("Unable to save configuration!", ex);
        }
    }
    public static boolean hasActiveTasks(){
        for(var task : tasks){
            if(task.isActive())return true;
        }
        return false;
    }
    public static boolean shutdown(boolean stopTasks, boolean force, boolean recursive, boolean closeGUI){
        Runnable exitLoop = () -> {
            if(stopTasks){
                for(int i = 0; i<tasks.size(); i++){
                    Task task = tasks.get(i);
                    if(task.isActive())task.shutdown();
                }
            }
            if(closeGUI)ManagerGUI.stop();
            if(force&&!recursive){
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
            if(!running)return true;
            if(!recursive){
                return false;
            }
            int nowTasks = 0;
            for(int i = 0; i<tasks.size(); i++){
                Task task = tasks.get(i);
                if(task.isActive())nowTasks++;
            }
            if(nowTasks==numTasks&&attempts>2){
                if(force){
                    Logger.error("Warning: "+nowTasks+" tasks have not stopped after "+attempts+" attempts!");
                    System.exit(0);
                    return true;
                }else{
                    Logger.error("Recursive shutdown cancelled! "+nowTasks+" tasks have not stopped after "+attempts+" attempts.");
                }
                return false;
            }
        }
    }
    public enum StartupFlags{
        NOGUI, INIT
    }
}
