package com.thizthizzydizzy.vrmanager.special.pimax.piSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piSvcHmdInfo;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
/**
 * This library reflects the piSvcCAPI header file defined in the PiTool GitHub:
 * https://github.com/OpenMAR/PiTool/blob/master/PiTool/piSvc/piSvcCAPI.h
 */
public interface piSvcCAPI extends Library{
    public static piSvcCAPI INSTANCE = Native.load("PiSvc", piSvcCAPI.class);

    //svc_start must be called first.
    //No other functions can call before svc_start succeeds or after svc_shutdown.
    public int svc_start(PointerByReference handle, StartCallback callback);
    public int svc_shutdown(Pointer handle);
    public int svc_getSvcHmdDesc(Pointer handle, piSvcHmdInfo hmdInfo);
    //caps represent all the capabilitys of connected hmd, each capacity value corresponds to the piHmdCapabilityMask
    public int svc_getHmdCaps(Pointer handle, IntByReference caps);
    public int svc_getResolutionCaps(Pointer handle, IntByReference resolutionCaps);
    public int svc_getCurrentResolution(Pointer handle, IntByReference resolution);
    public int svc_setResolution(Pointer handle, int resolution);
    public int svc_getUsbState(Pointer handle);
    public int svc_getHdmiState(Pointer handle);
    //devices represent all connected TrackedDeviceType devices
    public int svc_getConnectedDevices(Pointer handle, IntByReference devices);
    public int svc_getTrackerInfo(Pointer handle, int trackerType, StringByReference info, int size);
    public int svc_getDeviceBatteryLevel(Pointer handle, int device, IntByReference level);
    public int svc_getDeviceBatteryPercent(Pointer handle, int device, IntByReference percent/*percent is 0 to 100*/);
    public int svc_getServiceConnection(Pointer handle, IntByReference mode);
    public int svc_startFirmwareUpgrade(Pointer handle, String dfuPath);
    public int svc_changeMode(Pointer handle, int mode);
    public int svc_getModeCaps(Pointer handle, IntByReference caps);
    //restart current server
    public int svc_reconnectServer(Pointer handle);
    public int svc_rebootHmd(Pointer handle);
    public int svc_getToneState(Pointer handle, IntByReference tone);
    public int svc_setToneState(Pointer handle, int tone);
    public int svc_getBrightState(Pointer handle, IntByReference bright);
    public int svc_setBrightState(Pointer handle, int bright);
    public int svc_getBlueLightState(Pointer handle, BooleanByReference enable);
    public int svc_enableBlueLight(Pointer handle, boolean enable);
    public int svc_getBackLightState(Pointer handle, BooleanByReference value);
    public int svc_enableBackLight(Pointer handle, boolean enable);
    public int svc_getProximityState(Pointer handle, BooleanByReference enable);
    public int svc_enableProximity(Pointer handle, boolean enable);
    public int svc_getGyroState(Pointer handle, BooleanByReference enable);
    public int svc_enableGyro(Pointer handle, boolean enable);
    public int svc_getIpdValue(Pointer handle, FloatByReference value);
    public int svc_setIpdValue(Pointer handle, float value);
    public int svc_getLensValue(Pointer handle, FloatByReference value);
    public int svc_setLensValue(Pointer handle, float value);
    public int svc_getDisplayQualityValue(Pointer handle, FloatByReference value);
    public int svc_setDisplayQualityValue(Pointer handle, float value);
    public int svc_resetEyePose(Pointer handle);
    public int svc_getPimaxDefaultAudioConfig(Pointer handle, BooleanByReference enable);
    public int svc_setPimaxDefaultAudioDevice(Pointer handle, boolean enable);
    public int svc_resetFactorySettings(Pointer handle);
    public int svc_slopeCalibrationHmd(Pointer handle);
    public int svc_setIntConfig(Pointer handle, String key, int value);
    public int svc_getIntConfig(Pointer handle, String key, IntByReference value);
    public int svc_setFloatConfig(Pointer handle, String key, float value);
    public int svc_getFloatConfig(Pointer handle, String key, FloatByReference value);
    public int svc_setStringConfig(Pointer handle, String key, String value);
    public int svc_getStringConfig(Pointer handle, String key, StringByReference value, int size);
    public int svc_getVector3fConfig(Pointer handle, String key, piVector3f value);
    public int svc_setVector3fConfig(Pointer handle, String key, piVector3f value);
    public int svc_getHmdPosition(Pointer handle, FloatByReference x, FloatByReference y, FloatByReference z);
    /**********************************************
    start calibrate magnetism.
    then call svc_magnetismCalibrationed() 
    function to check succussful
    **********************************************/
    public int svc_magnetismCalibrationHmd(Pointer handle);
    public int svc_magnetismCalibrationed(Pointer handle);

    public int svc_getInterfaceVersion(Pointer handle, IntByReference major, IntByReference minor);
    public int svc_getServiceVersion(Pointer handle, IntByReference major, IntByReference minor);
    public int svc_getServiceLogLevel(Pointer handle, IntByReference level);
    public int svc_setServiceLogLevel(Pointer handle, int level);
    
    // New methods in PiSvc.dll that are not documented: (Signatures may not be accurate)
    public int svc_setStringDeviceConfig(Pointer handle, String key, String value);
    public int svc_getStringDeviceConfig(Pointer handle, String key, StringByReference value, int size);
    /*
    
    svc_getPanelMode
    svc_setKeyReportState
    svc_setLightColorState
    svc_setPanelMode
    svc_startWithMode

    */
    
    public static interface StartCallback extends Callback{
        public void run();
    }
}
