package com.thizthizzydizzy.vrmanager.config.module;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import java.util.HashMap;
public class PimaxConfiguration{
    public boolean usePimaxClient = false;
    public boolean forceReboot = true;
    public boolean startSteamVR = false;
    public boolean usePimaxClientForShutdown = true;
    public boolean watchUSBDevices = true;
    public HashMap<String, Integer> intSettings = new HashMap<>();
    public HashMap<String, Float> floatSettings = new HashMap<>();
    public HashMap<String, String> stringSettings = new HashMap<>();
    public HashMap<String, piVector3f> vectorSettings = new HashMap<>();
    {
        intSettings.put("enable_vst", 1);//enable passthrough
        intSettings.put("standby_timeout_min", 0);//disable standby
    }
}
