package com.thizthizzydizzy.vrmanager.config.module;
import java.util.ArrayList;
public class ProcessManagerConfiguration{
    public ArrayList<ProcessConfiguration> processes = new ArrayList<>();
    public static class ProcessConfiguration{
        public String title;
        public String target;
        public String startLocation;
        public boolean startIndirect;
        public boolean forceShutdown;
        public ArrayList<String> arguments = new ArrayList<>();

        public boolean isWindowsApp;
        public String exeName;
        @Override
        public String toString(){
            if(title!=null)return title;
            if(isWindowsApp)return exeName+" (Windows App)";
            if(target==null)return "UNSET";
            return target+" "+String.join(" ", arguments);
        }
    }
}
