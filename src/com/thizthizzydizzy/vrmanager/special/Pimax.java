package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcType.piSvcResult;
import com.thizthizzydizzy.vrmanager.task.Task;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
public class Pimax extends Task{
    public static File deviceSetting = new File("C:\\Program Files\\Pimax\\Runtime\\DeviceSetting.exe");
    public static File pimaxClient = new File("C:\\Program Files\\Pimax\\PimaxClient\\pimaxui\\PimaxClient.exe");
    public static boolean usePimaxClient = true;
    public static boolean forceReboot = true;
    public static void init(){
        VRManager.startTask(new Pimax());
    }
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
        if(usePimaxClient&&!isClientRunning){
            Logger.info("Starting Pimax Client ("+pimaxClient.getName()+")");
            VRManager.startIndirect(pimaxClient);
        }else if(!isPimaxRunning){
            Logger.info("Starting Pimax (DeviceSetting.exe)");
            VRManager.start(deviceSetting);
        }
        Logger.info("Starting PiSvc...");
        PiSvc.start();
        waitForConnection();
        if(forceReboot){
            Logger.info("Rebooting HMD");
            PiSvc.svc_rebootHmd();
            waitForConnection();
        }
        Logger.pop();
    }
    private void waitForConnection(){
        while(true){
            PiSvc.svc_getUsbState();
            int usbState = PiSvc.checkError();
            PiSvc.svc_getHdmiState();
            int hdmiState = PiSvc.checkError();
            if(usbState==0&&hdmiState==0)break;
            Logger.info("Waiting for Headset connection... USB: "+piSvcResult.getName(usbState)+", Video: "+piSvcResult.getName(hdmiState));
            try{
                Thread.sleep(1000);
            }catch(InterruptedException ex){
                Logger.error("Pimax Initialization was interrupted!", ex);
            }
        }
    }
    @Override
    public boolean isActive(){
        return false;
    }
    @Override
    public void shutdown(){
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
