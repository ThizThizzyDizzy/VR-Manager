package com.thizthizzydizzy.vrmanager.config.module;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import java.util.HashMap;
public class PimaxConfiguration{
    public boolean usePimaxClient = true;
    public boolean forceReboot = false;
    public boolean startSteamVR = false;
    public boolean usePimaxClientForShutdown = false;
    public boolean watchUSBDevices = true;
    public HashMap<String, Integer> intSettings = new HashMap<>();
    public HashMap<String, Float> floatSettings = new HashMap<>();
    public HashMap<String, String> stringSettings = new HashMap<>();
    public HashMap<String, piVector3f> vectorSettings = new HashMap<>();
}
