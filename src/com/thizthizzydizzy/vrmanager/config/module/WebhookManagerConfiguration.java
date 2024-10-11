package com.thizthizzydizzy.vrmanager.config.module;
import java.util.ArrayList;
public class WebhookManagerConfiguration{
    public ArrayList<WebhookConfiguration> webhooks = new ArrayList<>();
    public static class WebhookConfiguration{
        public String title;
        public String target;
        public String shutdownTarget;
        @Override
        public String toString(){
            if(title!=null)return title;
            if(shutdownTarget==null)return target==null?"UNSET":target;
            return target+" | "+shutdownTarget;
        }
    }
}
