package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.config.module.WebhookManagerConfiguration;
import com.thizthizzydizzy.vrmanager.task.Task;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
public class WebhookManager{
    public static void run(WebhookManagerConfiguration.WebhookConfiguration webhook){
        if(webhook.shutdownTarget!=null){
            VRManager.startTask(new Task("Webhook: "+webhook.title){
                boolean active = false;
                @Override
                public boolean isActive(){
                    return active;
                }
                @Override
                public void start(){
                    webhook(webhook.target);
                    active = true;
                }
                @Override
                public void shutdown(){
                    webhook(webhook.shutdownTarget);
                    active = false;
                }
            });
        }else
            webhook(webhook.target);
    }
    public static void webhook(String hook){
        Logger.push(WebhookManager.class);
        Logger.info("Sending webhook: "+hook);
        try{
            var conn = (HttpURLConnection)new URL(hook).openConnection();
            conn.setRequestMethod("POST");
            int code = conn.getResponseCode();
            if(code!=200)Logger.warn("Webhook returned HTTP "+code+"!");
            conn.disconnect();
        }catch(IOException ex){
            Logger.error("Failed to send webhook!", ex);
        }
        Logger.pop();
    }
}
