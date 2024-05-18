package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.pimax.PiRpc;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcType.piSvcResult;
import com.thizthizzydizzy.vrmanager.task.Task;
import java.io.File;
import java.io.IOException;
public class Pimax extends Task{
    public static File deviceSetting = new File("C:\\Program Files\\Pimax\\Runtime\\DeviceSetting.exe");
    public static File pimaxClient = new File("C:\\Program Files\\Pimax\\PimaxClient\\pimaxui\\PimaxClient.exe");
    public static boolean usePimaxClient = false;
    public static boolean forceReboot = false;
    public static boolean startSteamVR = true;
    public static boolean usePimaxClientForShutdown = true;
    public static void init(){
        VRManager.startTask(new Pimax());
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
        if(usePimaxClient&&!isClientRunning){
            Logger.info("Starting Pimax Client ("+pimaxClient.getName()+")");
            VRManager.startIndirect(pimaxClient);
        }else if(!isPimaxRunning){
            Logger.info("Starting Pitool (DeviceSetting.exe)");
            service = VRManager.start(deviceSetting);
        }
        Logger.info("Starting PiSvc...");
        PiSvc.start();
        Logger.info("Starting PiRpc...");
        PiRpc.start();
        waitForConnection();
        if(forceReboot){
            Logger.info("Rebooting HMD");
            PiRpc.Event_rebootHmdAuto();
            try{
                Thread.sleep(5000);
            }catch(InterruptedException ex){
            }
            waitForConnection();
        }
        if(startSteamVR)PiRpc.Click_SteamVR();
        Logger.pop();
    }
    private void waitForConnection(){
        int consecutiveA = 0;
        int consecutiveB = 0;
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
        if(usePimaxClientForShutdown){
            VRManager.startIndirect(pimaxClient);
            try{
                Thread.sleep(3000);
            }catch(InterruptedException ex){
            }
        }
        Windows.runCommand("taskkill /IM PimaxClient.exe");
        if(service!=null){
            Logger.info("Shutting down Pitool");
            service.destroy();
        }
        Windows.runCommand("taskkill /IM DeviceSetting.exe");
        running = false;
        Logger.pop();
    }
}
