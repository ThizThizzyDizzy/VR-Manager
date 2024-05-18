package com.thizthizzydizzy.vrmanager.special;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.Task;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.piSvc.BooleanByReference;
import com.thizthizzydizzy.vrmanager.special.piSvc.StringByReference;
import com.thizthizzydizzy.vrmanager.special.piSvc.piSvcCAPI;
import com.thizthizzydizzy.vrmanager.special.piSvc.piSvcDesc.piSvcHmdInfo;
import com.thizthizzydizzy.vrmanager.special.piSvc.piSvcDesc.piVector3f;
import com.thizthizzydizzy.vrmanager.special.piSvc.piSvcType.piSvcResult;
public class PiSvc{
    private static Task task;
    public static boolean active = false;
    private static Pointer handle;
    public static void start(){
        Logger.push(PiSvc.class);
        Logger.info("Initializing PiSvc");
        PointerByReference handleReference = new PointerByReference();
        int result = piSvcCAPI.INSTANCE.svc_start(handleReference, () -> {
            //I dunno what this callback is for, it seems to get called a bunch of times around startup/shutdown?
        });
        if(result!=0){
            Logger.error("Initialization failed: "+piSvcResult.getName(result));
        }else{
            Logger.info("Initialization successful!");
            handle = handleReference.getPointer();
            active = true;
            if(task==null){
                task = new Task(){
                    @Override
                    public boolean isActive(){
                        return active;
                    }
                    @Override
                    public void shutdown(){
                        stop();
                    }
                };
                VRManager.addTask(task);
            }
            var serviceVersion = svc_getServiceVersion();
            var interfaceVersion = svc_getInterfaceVersion();
            Logger.info("Service version: "+serviceVersion[0]+"."+serviceVersion[1]);
            Logger.info("Interface version: "+interfaceVersion[0]+"."+interfaceVersion[1]);
        }
        Logger.pop();
    }
    public static void stop(){
        Logger.push(PiSvc.class);
        int result = piSvcCAPI.INSTANCE.svc_shutdown(handle);
        if(result!=0){
            Logger.error("Shutdown failed: "+piSvcResult.getName(result));
        }else{
            Logger.info("PiSvc stopped");
            handle = null;
            active = false;
        }
        Logger.pop();
    }

    public static piSvcHmdInfo svc_getSvcHmdDesc(){
        var info = new piSvcHmdInfo();
        checkError(piSvcCAPI.INSTANCE.svc_getSvcHmdDesc(handle, info));
        return info;
    }
    public static int svc_getHmdCaps(){
        IntByReference caps = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getHmdCaps(handle, caps));
        return caps.getValue();
    }
    public static int svc_getResolutionCaps(){
        IntByReference caps = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getResolutionCaps(handle, caps));
        return caps.getValue();
    }
    public static int svc_getCurrentResolution(){
        IntByReference resolution = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getCurrentResolution(handle, resolution));
        return resolution.getValue();
    }
    public static void svc_setResolution(int resolution){
        checkError(piSvcCAPI.INSTANCE.svc_setResolution(handle, resolution));
    }
    public static void svc_getUsbState(){
        checkError(piSvcCAPI.INSTANCE.svc_getUsbState(handle));
    }
    public static void svc_getHdmiState(){
        checkError(piSvcCAPI.INSTANCE.svc_getHdmiState(handle));
    }
    public static int svc_getConnectedDevices(){
        IntByReference devices = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getConnectedDevices(handle, devices));
        return devices.getValue();
    }
    public static String svc_getTrackerInfo(int trackerType, int size){
        StringByReference info = new StringByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getTrackerInfo(handle, trackerType, info, size));
        return info.getVal();
    }
    public static int svc_getDeviceBatteryLevel(int device){
        IntByReference level = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getDeviceBatteryLevel(handle, device, level));
        return level.getValue();
    }
    public static int svc_getDeviceBatteryPercent(int device){
        IntByReference percent = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getDeviceBatteryPercent(handle, device, percent));
        return percent.getValue();
    }
    public static int svc_getServiceConnection(){
        IntByReference mode = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getServiceConnection(handle, mode));
        return mode.getValue();
    }
    public static void svc_startFirmwareUpgrade(String dfuPath){
        checkError(piSvcCAPI.INSTANCE.svc_startFirmwareUpgrade(handle, dfuPath));
    }
    public static void svc_changeMode(int mode){
        checkError(piSvcCAPI.INSTANCE.svc_changeMode(handle, mode));
    }
    public static int svc_getModeCaps(){
        IntByReference caps = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getModeCaps(handle, caps));
        return caps.getValue();
    }
    public static void svc_reconnectServer(){
        checkError(piSvcCAPI.INSTANCE.svc_reconnectServer(handle));
    }
    public static void svc_rebootHmd(){
        checkError(piSvcCAPI.INSTANCE.svc_rebootHmd(handle));
    }
    public static int svc_getToneState(){
        IntByReference val = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getToneState(handle, val));
        return val.getValue();
    }
    public static void svc_setToneState(int tone){
        checkError(piSvcCAPI.INSTANCE.svc_setToneState(handle, tone));
    }
    public static int svc_getBrightState(){
        IntByReference val = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getBrightState(handle, val));
        return val.getValue();
    }
    public static void svc_setBrightState(int bright){
        checkError(piSvcCAPI.INSTANCE.svc_setBrightState(handle, bright));
    }
    public static boolean svc_getBlueLightState(){
        BooleanByReference val = new BooleanByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getBlueLightState(handle, val));
        return val.getVal();
    }
    public static void svc_enableBlueLight(boolean enable){
        checkError(piSvcCAPI.INSTANCE.svc_enableBlueLight(handle, enable));
    }
    public static boolean svc_getBackLightState(){
        BooleanByReference val = new BooleanByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getBackLightState(handle, val));
        return val.getVal();
    }
    public static void svc_enableBackLight(boolean enable){
        checkError(piSvcCAPI.INSTANCE.svc_enableBackLight(handle, enable));
    }
    public static boolean svc_getProximityState(){
        BooleanByReference val = new BooleanByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getProximityState(handle, val));
        return val.getVal();
    }
    public static void svc_enableProximity(boolean enable){
        checkError(piSvcCAPI.INSTANCE.svc_enableProximity(handle, enable));
    }
    public static boolean svc_getGyroState(){
        BooleanByReference val = new BooleanByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getGyroState(handle, val));
        return val.getVal();
    }
    public static void svc_enableGyro(boolean enable){
        checkError(piSvcCAPI.INSTANCE.svc_enableGyro(handle, enable));
    }
    public static float svc_getIpdValue(){
        FloatByReference val = new FloatByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getIpdValue(handle, val));
        return val.getValue();
    }
    public static void svc_setIpdValue(float value){
        checkError(piSvcCAPI.INSTANCE.svc_setIpdValue(handle, value));
    }
    public static float svc_getLensValue(){
        FloatByReference val = new FloatByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getLensValue(handle, val));
        return val.getValue();
    }
    public static void svc_setLensValue(float value){
        checkError(piSvcCAPI.INSTANCE.svc_setLensValue(handle, value));
    }
    public static float svc_getDisplayQualityValue(){
        FloatByReference val = new FloatByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getDisplayQualityValue(handle, val));
        return val.getValue();
    }
    public static void svc_setDisplayQualityValue(float value){
        checkError(piSvcCAPI.INSTANCE.svc_setDisplayQualityValue(handle, value));
    }
    public static void svc_resetEyePose(){
        checkError(piSvcCAPI.INSTANCE.svc_resetEyePose(handle));
    }
    public static boolean svc_getPimaxDefaultAudioConfig(){
        BooleanByReference val = new BooleanByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getPimaxDefaultAudioConfig(handle, val));
        return val.getVal();
    }
    public static void svc_setPimaxDefaultAudioDevice(boolean enable){
        checkError(piSvcCAPI.INSTANCE.svc_setPimaxDefaultAudioDevice(handle, enable));
    }
    public static void svc_resetFactorySettings(){
        checkError(piSvcCAPI.INSTANCE.svc_resetFactorySettings(handle));
    }
    public static void svc_slopeCalibrationHmd(){
        checkError(piSvcCAPI.INSTANCE.svc_slopeCalibrationHmd(handle));
    }
    public static void svc_setIntConfig(String key, int value){
        checkError(piSvcCAPI.INSTANCE.svc_setIntConfig(handle, key, value));
    }
    public static int svc_getIntConfig(String key){
        IntByReference val = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getIntConfig(handle, key, val));
        return val.getValue();
    }
    public static void svc_setFloatConfig(String key, float value){
        checkError(piSvcCAPI.INSTANCE.svc_setFloatConfig(handle, key, value));
    }
    public static float svc_getFloatConfig(String key){
        FloatByReference val = new FloatByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getFloatConfig(handle, key, val));
        return val.getValue();
    }
    public static void svc_setStringConfig(String key, String value){
        checkError(piSvcCAPI.INSTANCE.svc_setStringConfig(handle, key, value));
    }
    public static String svc_getStringConfig(String key, int size){
        StringByReference val = new StringByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getStringConfig(handle, key, val, size));
        return val.getVal();
    }
    public static piVector3f svc_getVector3fConfig(String key){
        piVector3f vector = new piVector3f();
        checkError(piSvcCAPI.INSTANCE.svc_getVector3fConfig(handle, key, vector));
        return vector;
    }
    public static void svc_setVector3fConfig(String key, piVector3f value){
        checkError(piSvcCAPI.INSTANCE.svc_setVector3fConfig(handle, key, value));
    }
    public static piVector3f svc_getHmdPosition(){
        FloatByReference x = new FloatByReference();
        FloatByReference y = new FloatByReference();
        FloatByReference z = new FloatByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getHmdPosition(handle, x, y, z));
        piVector3f vector = new piVector3f();
        vector.x = x.getValue();
        vector.y = y.getValue();
        vector.z = z.getValue();
        return vector;
    }
    public static void svc_magnetismCalibrationHmd(){
        checkError(piSvcCAPI.INSTANCE.svc_magnetismCalibrationHmd(handle));
    }
    public static void svc_magnetismCalibrationed(){
        checkError(piSvcCAPI.INSTANCE.svc_magnetismCalibrationed(handle));
    }
    public static int[] svc_getInterfaceVersion(){
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getInterfaceVersion(handle, major, minor));
        return new int[]{major.getValue(), minor.getValue()};
    }
    public static int[] svc_getServiceVersion(){
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getInterfaceVersion(handle, major, minor));
        return new int[]{major.getValue(), minor.getValue()};
    }
    public static int svc_getServiceLogLevel(){
        IntByReference val = new IntByReference();
        checkError(piSvcCAPI.INSTANCE.svc_getServiceLogLevel(handle, val));
        return val.getValue();
    }
    public static void svc_setServiceLogLevel(int level){
        checkError(piSvcCAPI.INSTANCE.svc_setServiceLogLevel(handle, level));
    }
    private static void checkError(int result){
        Logger.push(PiSvc.class);
        if(result!=0)Logger.error("Error: "+piSvcResult.getName(result));
        Logger.pop();
    }
}
