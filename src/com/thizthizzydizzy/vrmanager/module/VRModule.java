package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
public abstract class VRModule{
    public static final VRModule[] modules = new VRModule[]{
        new ProcessManagerModule(),
        new WebhookManagerModule(),
        new PimaxModule(),
        new UsbModule()
    };
    public static VRModule get(String name){
        for(VRModule module : modules)if(module.getName().equals(name))return module;
        Logger.error("Invalid module: "+name);
        return null;
    }
    public abstract String getName();
    public abstract NamedCommand[] getCommands();
    public static String[] getCommandNames(VRModule module){
        var commands = module.getCommands();
        String[] names = new String[commands.length];
        for(int i = 0; i<names.length; i++)names[i] = commands[i].name;
        return names;
    }
    public abstract void init();
    public JDialog getConfigurationGUI(JFrame parent){
        return null;
    }
    public boolean hasConfiguration(){
        return false;
    }
}
