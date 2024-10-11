package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.command.CommandUtil;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.command.NamedCommandWithFlags;
import com.thizthizzydizzy.vrmanager.gui.module.ConfigureWebhookManagerGUI;
import com.thizthizzydizzy.vrmanager.special.WebhookManager;
import javax.swing.JDialog;
import javax.swing.JFrame;
public class WebhookManagerModule extends VRModule{
    @Override
    public String getName(){
        return "webhook";
    }
    @Override
    public NamedCommand[] getCommands(){
        return CommandUtil.subcommands(
            new NamedCommandWithFlags("call", (base, args, flags) -> {
                if(!CommandUtil.nArguments(base, args, 1))return;
                WebhookManager.webhook(args[0]);
            })
        );
    }
    @Override
    public void init(){
        for(var webhook : VRManager.configuration.webhookManager.webhooks){
            WebhookManager.run(webhook);
        }
    }
    @Override
    public boolean hasConfiguration(){
        return true;
    }
    @Override
    public JDialog getConfigurationGUI(JFrame parent){
        return new ConfigureWebhookManagerGUI(parent);
    }
}
