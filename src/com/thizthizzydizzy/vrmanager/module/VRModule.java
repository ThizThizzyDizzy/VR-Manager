package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.Logger;
import java.util.HashSet;
public abstract class VRModule{
    public static final VRModule[] modules = new VRModule[]{
        new PimaxModule(),
        new UsbModule()
    };
    public static final HashSet<VRModule> activeModules = new HashSet<>();
    public static VRModule get(String name){
        for(VRModule module : modules)if(module.getName().equals(name))return module;
        Logger.error("Invalid module: "+name);
        return null;
    }
    public static void setActive(String key, boolean b){
        var module = get(key);
        if(module==null)return;
        if(b)activeModules.add(module);
        else{
            activeModules.remove(module);
        }
    }
    public static boolean isActive(String key){
        return activeModules.contains(get(key));
    }
    public abstract String getName();
    public abstract NamedCommand[] getCommands();
    public static String[] getCommandNames(VRModule module){
        var commands = module.getCommands();
        String[] names = new String[commands.length];
        for(int i = 0; i<names.length; i++)names[i] = commands[i].name;
        return names;
    }
}
