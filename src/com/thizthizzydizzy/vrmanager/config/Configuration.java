package com.thizthizzydizzy.vrmanager.config;
import com.thizthizzydizzy.vrmanager.config.module.PimaxConfiguration;
import com.thizthizzydizzy.vrmanager.config.module.ProcessManagerConfiguration;
import com.thizthizzydizzy.vrmanager.config.module.WebhookManagerConfiguration;
import java.util.ArrayList;
public class Configuration{
    public ArrayList<String> modules = new ArrayList<>();
    public PimaxConfiguration pimax = new PimaxConfiguration();
    public ProcessManagerConfiguration processManager = new ProcessManagerConfiguration();
    public WebhookManagerConfiguration webhookManager = new WebhookManagerConfiguration();
}
