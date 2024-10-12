package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.pimax.PiRpc;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.PiRpcAPI;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcType.piSvcResult;
import com.thizthizzydizzy.vrmanager.task.Task;
import java.io.File;
import java.io.IOException;
public class Pimax extends Task{
    public static File deviceSetting = new File("C:\\Program Files\\Pimax\\Runtime\\DeviceSetting.exe");
    public static File pimaxClient = new File("C:\\Program Files\\Pimax\\PimaxClient\\pimaxui\\PimaxClient.exe");
    public static Pimax pimaxTask;
    public static void init(){
        pimaxTask = VRManager.startTask(new Pimax());
    }
    private boolean running = false;
    private Process service;
    public Pimax(){
        super("Pimax");
    }
    @Override
    public void start(){
        Logger.push(this);
        boolean isPimaxRunning = false;
        boolean isClientRunning = false;
        try{
            for(var task : Windows.getTasks()){
                if(task.imageName.equals(deviceSetting.getName())){
                    isPimaxRunning = true;
                }
                if(task.imageName.equals(pimaxClient.getName())){
                    isClientRunning = true;
                }
            }
        }catch(IOException ex){
            Logger.warn("Could not list running tasks! Assuming DeviceSetting is not running...");
        }
        running = true;
        if(VRManager.configuration.pimax.usePimaxClient&&!isClientRunning){
            Logger.info("Starting Pimax Client ("+pimaxClient.getName()+")");
            VRManager.startIndirect(pimaxClient);
        }else if(!isPimaxRunning){
            Logger.info("Starting Pitool (DeviceSetting.exe)");
            try{
                service = VRManager.start(deviceSetting);
            }catch(IOException ex){
                Logger.error("Could not start Pitool! (DeviceSetting.exe)");
                Logger.pop();
                return;
            }
        }
        if(!PiSvc.active){
            Logger.info("Starting PiSvc...");
            PiSvc.start();
        }
        if(!PiRpcAPI.active){
            Logger.info("Starting PiRpc...");
            PiRpc.start();
        }
        if(VRManager.configuration.pimax.watchUSBDevices){
            Usb.watch("Tobii Eye Tracking", 0x2104, 0x0220);//Tobii AB WinUsb Device
            Usb.watch("SteamVR HMD Tracking", 0x28DE, 0x2300);//Lighthouse Faceplate (HMD tracking)
            Usb.watch("Watchman Dongle", 0x28DE, 0x2101);//Watchman Dongle (HMD or standalone)
            Usb.watch("Pimax Crystal", 0x34A4, 0x0012);//Pimax Crystal
            Usb.watch("Pimax", 0x34A4);//Pimax
            Usb.watch("Valve", 0x28DE);//Valve
        }
        waitForConnection(0);
        if(VRManager.configuration.pimax.forceReboot){
            Logger.info("Rebooting HMD");
            PiRpc.Event_rebootHmdAuto();
            waitForConnection(5000);
        }
        //Check settings, adjust and wait for reboots as neccessary
        for(var entry : VRManager.configuration.pimax.intSettings.entrySet()){
            int value;
            while((value = PiSvc.svc_getIntConfig(entry.getKey()))!=entry.getValue()){
                Logger.info("Changing Setting "+entry.getKey()+" ("+value+" -> "+entry.getValue()+")");
                PiSvc.svc_setIntConfig(entry.getKey(), entry.getValue());
                waitForConnection(500);
            }
        }
        for(var entry : VRManager.configuration.pimax.floatSettings.entrySet()){
            float value;
            while((value = PiSvc.svc_getFloatConfig(entry.getKey()))!=entry.getValue()){
                Logger.info("Changing Setting "+entry.getKey()+" ("+value+" -> "+entry.getValue()+")");
                PiSvc.svc_setFloatConfig(entry.getKey(), entry.getValue());
                waitForConnection(500);
            }
        }
        for(var entry : VRManager.configuration.pimax.stringSettings.entrySet()){
            String value;
            while(!(value = PiSvc.svc_getStringConfig(entry.getKey(), entry.getValue().length())).equals(entry.getValue())){
                Logger.info("Changing Setting "+entry.getKey()+" ("+value+" -> "+entry.getValue()+")");
                PiSvc.svc_setStringConfig(entry.getKey(), entry.getValue());
                waitForConnection(500);
            }
        }
        for(var entry : VRManager.configuration.pimax.vectorSettings.entrySet()){
            piVector3f value;
            while(!(value = PiSvc.svc_getVector3fConfig(entry.getKey())).equals(entry.getValue())){
                Logger.info("Changing Setting "+entry.getKey()+" ("+value.toString()+" -> "+entry.getValue().toString()+")");
                PiSvc.svc_setVector3fConfig(entry.getKey(), entry.getValue());
                waitForConnection(500);
            }
        }
        if(VRManager.configuration.pimax.startSteamVR)PiRpc.Click_SteamVR();
        Logger.pop();
    }
    private void waitForConnection(int minDelay){
        int consecutiveA = 0;
        int consecutiveB = 0;
        try{
            Thread.sleep(minDelay);
        }catch(InterruptedException ex){
            Logger.error("Pimax Initialization was interrupted!", ex);
        }
        while(true){
            PiSvc.svc_getUsbState();
            int usbState = PiSvc.checkError();
            PiSvc.svc_getHdmiState();
            int hdmiState = PiSvc.checkError();
            boolean rpc = PiRpc.Get_HMDStatus_hmd_connect();
            if(usbState==0&&hdmiState==0&&rpc)break;
            Logger.info("Waiting for Headset connection... USB: "+piSvcResult.getName(usbState)+", Video: "+piSvcResult.getName(hdmiState)+", RPC: "+rpc);
            if(usbState==114&&hdmiState==114&&!rpc)consecutiveA++;
            else
                consecutiveA = 0;
            if(usbState==0&&hdmiState==0&&!rpc)consecutiveB++;
            else
                consecutiveB = 0;
            if(consecutiveA>15){
                Logger.warn("RPCs unresponsive for 15 seconds! Is another VR Manager instance running?");
                Logger.info("Restarting Service...");
                PiRpc.Event_restartServiceAuto();
                consecutiveA = 0;
            }
            if(consecutiveB>30){
                Logger.warn("Headset is unresponsive for 30 seconds! Attempting to restart it...");
                PiRpc.Event_rebootHmdAuto();
                consecutiveB = 0;
            }

            try{
                Thread.sleep(1000);
            }catch(InterruptedException ex){
                Logger.error("Pimax Initialization was interrupted!", ex);
            }
        }
    }
    @Override
    public boolean isActive(){
        return running;
    }
    @Override
    public void shutdown(){
        Logger.push(this);
        //no RPC to shut down the HMD
        PiRpc.stop();
        PiSvc.stop();
        if(VRManager.configuration.pimax.usePimaxClientForShutdown){
            VRManager.startIndirect(pimaxClient);
            try{
                Thread.sleep(3000);
            }catch(InterruptedException ex){
            }
        }
        Windows.taskkill("PimaxClient.exe");
        if(service!=null){
            Logger.info("Shutting down Pitool");
            service.destroy();
        }
        Windows.taskkill("DeviceSetting.exe");
        running = false;
        pimaxTask = null;
        Logger.pop();
    }
}
