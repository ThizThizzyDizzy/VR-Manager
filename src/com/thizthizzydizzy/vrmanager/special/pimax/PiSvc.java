package com.thizthizzydizzy.vrmanager.special.pimax;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.Telemetry;
import com.thizthizzydizzy.vrmanager.task.Task;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.BooleanByReference;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.StringByReference;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCAPI;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piSvcHmdInfo;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcType.piSvcResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
public class PiSvc{
    private static Task task;
    public static boolean active = false;
    private static Pointer handle;
    private static int error = 0;
    public static boolean debug = false;
    public static final ArrayList<PiSvcConfig> knownConfigKeys = new ArrayList<>();
    static{
        //TODO more helpful descriptions
        knownConfigKeys.add(new PiSvcConfig("enable_lighthouse_tracking", PiSvcConfig.Type.INT, "This doesn't seem to do anything, it's always 1"));
        knownConfigKeys.add(new PiSvcConfig("display_timing_selection", PiSvcConfig.Type.INT, "Refresh Rate (0 = 120 Hz, 1 = 90 Hz, 2 = 72 Hz)", 0, 2));
        knownConfigKeys.add(new PiSvcConfig("ipd_auto_adjust", PiSvcConfig.Type.INT, "Auto Ipd Adjustment", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("hmd_mount_adjust", PiSvcConfig.Type.INT, "Wearing Location Reminder", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("ipd", PiSvcConfig.Type.FLOAT, false, "IPD Adjustment"));
        knownConfigKeys.add(new PiSvcConfig("enable_leapmotion_controller", PiSvcConfig.Type.INT, "Enable Hand Tracking (Leap Motion)", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("use_controller_type_knuckles", PiSvcConfig.Type.INT, "Simulate Valve Index Controllers", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("pixels_per_display_pixel_rate", PiSvcConfig.Type.FLOAT, "Render scale multiplier", 0.5f, 2));
        knownConfigKeys.add(new PiSvcConfig("enable_foveated_rendering", PiSvcConfig.Type.INT, "Enable Dynamic Foveated Rendering", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("foveated_rendering_level", PiSvcConfig.Type.INT, "Dynamic Foveated Rendering (0 = Aggressive, 1 = Balanced, 2 = Conservative)", 0, 2));
        knownConfigKeys.add(new PiSvcConfig("dbg_asw_enable", PiSvcConfig.Type.INT, "Smart Smoothing", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("dbg_force_framerate_divide_by", PiSvcConfig.Type.INT, "Lock to half framerate (1-3; 1 = off, 2 = on)", 1, 3));
        knownConfigKeys.add(new PiSvcConfig("dbg_hidden_area_enable", PiSvcConfig.Type.INT, "Hidden Area Mask", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("support_vive_only_games", PiSvcConfig.Type.INT, "Vive Compatibility", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("color_contrast_0_rgb", PiSvcConfig.Type.VECTOR3F, "Color Contrast - Left Eye (R/G/B)", -0.5f, 0.5f));
        knownConfigKeys.add(new PiSvcConfig("color_contrast_1_rgb", PiSvcConfig.Type.VECTOR3F, "Color Contrast - Right Eye (R/G/B)", -.5f, 0.5f));
        knownConfigKeys.add(new PiSvcConfig("color_brightness_0_rgb", PiSvcConfig.Type.VECTOR3F, "Color Brightness - Left Eye (R/G/B)", -.1f, .1f));
        knownConfigKeys.add(new PiSvcConfig("color_brightness_1_rgb", PiSvcConfig.Type.VECTOR3F, "Color Brightness - Right Eye (R/G/B)", -.1f, .1f));
        knownConfigKeys.add(new PiSvcConfig("auto_switch_default_audio", PiSvcConfig.Type.INT, "Headset as default audio device", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("enable_pvr_home", PiSvcConfig.Type.INT, "Home Environment (0 = Off, 1 = Pimax Home or Experience Home)", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("lens_auto_detect", PiSvcConfig.Type.INT, "Auto-detect Lens Settings", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("lens_selection", PiSvcConfig.Type.INT, "Lens Setting (0 = 35 PPD Glass, 1 = 42 PPD Poly, 2 = 35 PPD Poly, 3 = Big FOV)", 0, 3));
        knownConfigKeys.add(new PiSvcConfig("lens_horizontal_offset_0", PiSvcConfig.Type.FLOAT, "Horizontal IPD Offset - Left Eye", -.02f, .02f));
        knownConfigKeys.add(new PiSvcConfig("lens_horizontal_offset_1", PiSvcConfig.Type.FLOAT, "Horizontal IPD Offset - Right Eye", -.02f, .02f));
        knownConfigKeys.add(new PiSvcConfig("lens_vertical_offset_0", PiSvcConfig.Type.FLOAT, "Screen Vertical Offset - Left Eye", -.01f, .01f));
        knownConfigKeys.add(new PiSvcConfig("lens_vertical_offset_1", PiSvcConfig.Type.FLOAT, "Screen Vertical Offset - Right Eye", -.01f, .01f));
        knownConfigKeys.add(new PiSvcConfig("local_dimming_black_level", PiSvcConfig.Type.FLOAT, "Local Dimming Level (1 = off, 0.6 = Balanced, 0.3 = Highlight, 0 = Extreme)", 0f, 1f));
        knownConfigKeys.add(new PiSvcConfig("enable_vst", PiSvcConfig.Type.INT, "Enable Passthrough", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("double_tap_for_vst", PiSvcConfig.Type.INT, "Double-tap the HMD to toggle Passthrough", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("disable_room_boundary_global", PiSvcConfig.Type.INT, "Disable Chaperone (Play area)", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("motion_cancellation_device", PiSvcConfig.Type.STRING, "Motion Compensation (Device Name, ex. LHR-etc)"));
        knownConfigKeys.add(new PiSvcConfig("steamvr_use_native_fov", PiSvcConfig.Type.INT, "Always 0"));
        knownConfigKeys.add(new PiSvcConfig("fov_level", PiSvcConfig.Type.INT, "Reduce FOV (10 degrees per setting)", 0, 3));//this just sets fov_outer_adjust_degree to 10x the supplied value
        knownConfigKeys.add(new PiSvcConfig("fov_outer_adjust_degree", PiSvcConfig.Type.FLOAT, "Reduce FOV (degrees)", 0, 180));
        knownConfigKeys.add(new PiSvcConfig("support_hmd_volume_adjust", PiSvcConfig.Type.INT, "Switches between HMD and windows volume control (0 = Windows volume, 1 = HMD volume)", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("enable_screen_saver", PiSvcConfig.Type.INT, "Enable screen saver", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("standby_timeout_min", PiSvcConfig.Type.INT, "Standby timeout (Set to 0 to disable)", 0, 60));
        knownConfigKeys.add(new PiSvcConfig("headphone_state", PiSvcConfig.Type.INT, false, ""));
        knownConfigKeys.add(new PiSvcConfig("hmd_debug_status", PiSvcConfig.Type.INT, false, ""));
        knownConfigKeys.add(new PiSvcConfig("expire_ts", PiSvcConfig.Type.INT, false, ""));
        knownConfigKeys.add(new PiSvcConfig("notify_feature_update", PiSvcConfig.Type.INT, "This was set to 1 at some point"));
        knownConfigKeys.add(new PiSvcConfig("vr_mode", PiSvcConfig.Type.INT, "Recline Mode", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("controller_haptic_scale", PiSvcConfig.Type.FLOAT, "Controller haptics intensity", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("camera_mode_auto_detect", PiSvcConfig.Type.INT, "Auto-detect Room Lighting Frequency", 0, 1));
        knownConfigKeys.add(new PiSvcConfig("camera_mode_idx", PiSvcConfig.Type.INT, "Room Lighting Frequency (0 = 60 Hz, 1 = 50 Hz)", 0, 1));
    }
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
                task = new Task("PiSvc"){
                    @Override
                    public boolean isActive(){
                        return active;
                    }
                    @Override
                    public void shutdown(){
                        stop();
                    }
                };
                VRManager.startTask(task);
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
        run(piSvcCAPI.INSTANCE.svc_getSvcHmdDesc(handle, info));
        return info;
    }
    public static int svc_getHmdCaps(){
        IntByReference caps = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getHmdCaps(handle, caps));
        return caps.getValue();
    }
    public static int svc_getResolutionCaps(){
        IntByReference caps = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getResolutionCaps(handle, caps));
        return caps.getValue();
    }
    public static int svc_getCurrentResolution(){
        IntByReference resolution = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getCurrentResolution(handle, resolution));
        return resolution.getValue();
    }
    public static void svc_setResolution(int resolution){
        run(piSvcCAPI.INSTANCE.svc_setResolution(handle, resolution));
    }
    public static void svc_getUsbState(){
        run(piSvcCAPI.INSTANCE.svc_getUsbState(handle));
    }
    public static void svc_getHdmiState(){
        run(piSvcCAPI.INSTANCE.svc_getHdmiState(handle));
    }
    public static int svc_getConnectedDevices(){
        IntByReference devices = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getConnectedDevices(handle, devices));
        return devices.getValue();
    }
    public static String svc_getTrackerInfo(int trackerType, int size){
        StringByReference info = new StringByReference();
        run(piSvcCAPI.INSTANCE.svc_getTrackerInfo(handle, trackerType, info, size));
        return info.getVal();
    }
    public static int svc_getDeviceBatteryLevel(int device){
        IntByReference level = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getDeviceBatteryLevel(handle, device, level));
        return level.getValue();
    }
    public static int svc_getDeviceBatteryPercent(int device){
        IntByReference percent = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getDeviceBatteryPercent(handle, device, percent));
        return percent.getValue();
    }
    public static int svc_getServiceConnection(){
        IntByReference mode = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getServiceConnection(handle, mode));
        return mode.getValue();
    }
    public static void svc_startFirmwareUpgrade(String dfuPath){
        run(piSvcCAPI.INSTANCE.svc_startFirmwareUpgrade(handle, dfuPath));
    }
    public static void svc_changeMode(int mode){
        run(piSvcCAPI.INSTANCE.svc_changeMode(handle, mode));
    }
    public static int svc_getModeCaps(){
        IntByReference caps = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getModeCaps(handle, caps));
        return caps.getValue();
    }
    public static void svc_reconnectServer(){
        run(piSvcCAPI.INSTANCE.svc_reconnectServer(handle));
    }
    /**
     * @deprecated This is unreliable; Use `PiRpc.Event_rebootHmdAuto` if
     * possible.
     */
    @Deprecated
    public static void svc_rebootHmd(){
        run(piSvcCAPI.INSTANCE.svc_rebootHmd(handle));
    }
    public static int svc_getToneState(){
        IntByReference val = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getToneState(handle, val));
        return val.getValue();
    }
    public static void svc_setToneState(int tone){
        run(piSvcCAPI.INSTANCE.svc_setToneState(handle, tone));
    }
    public static int svc_getBrightState(){
        IntByReference val = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getBrightState(handle, val));
        return val.getValue();
    }
    public static void svc_setBrightState(int bright){
        run(piSvcCAPI.INSTANCE.svc_setBrightState(handle, bright));
    }
    public static boolean svc_getBlueLightState(){
        BooleanByReference val = new BooleanByReference();
        run(piSvcCAPI.INSTANCE.svc_getBlueLightState(handle, val));
        return val.getVal();
    }
    public static void svc_enableBlueLight(boolean enable){
        run(piSvcCAPI.INSTANCE.svc_enableBlueLight(handle, enable));
    }
    public static boolean svc_getBackLightState(){
        BooleanByReference val = new BooleanByReference();
        run(piSvcCAPI.INSTANCE.svc_getBackLightState(handle, val));
        return val.getVal();
    }
    public static void svc_enableBackLight(boolean enable){
        run(piSvcCAPI.INSTANCE.svc_enableBackLight(handle, enable));
    }
    public static boolean svc_getProximityState(){
        BooleanByReference val = new BooleanByReference();
        run(piSvcCAPI.INSTANCE.svc_getProximityState(handle, val));
        return val.getVal();
    }
    public static void svc_enableProximity(boolean enable){
        run(piSvcCAPI.INSTANCE.svc_enableProximity(handle, enable));
    }
    public static boolean svc_getGyroState(){
        BooleanByReference val = new BooleanByReference();
        run(piSvcCAPI.INSTANCE.svc_getGyroState(handle, val));
        return val.getVal();
    }
    public static void svc_enableGyro(boolean enable){
        run(piSvcCAPI.INSTANCE.svc_enableGyro(handle, enable));
    }
    public static float svc_getIpdValue(){
        FloatByReference val = new FloatByReference();
        run(piSvcCAPI.INSTANCE.svc_getIpdValue(handle, val));
        return val.getValue();
    }
    public static void svc_setIpdValue(float value){
        run(piSvcCAPI.INSTANCE.svc_setIpdValue(handle, value));
    }
    public static float svc_getLensValue(){
        FloatByReference val = new FloatByReference();
        run(piSvcCAPI.INSTANCE.svc_getLensValue(handle, val));
        return val.getValue();
    }
    public static void svc_setLensValue(float value){
        run(piSvcCAPI.INSTANCE.svc_setLensValue(handle, value));
    }
    public static float svc_getDisplayQualityValue(){
        FloatByReference val = new FloatByReference();
        run(piSvcCAPI.INSTANCE.svc_getDisplayQualityValue(handle, val));
        return val.getValue();
    }
    public static void svc_setDisplayQualityValue(float value){
        run(piSvcCAPI.INSTANCE.svc_setDisplayQualityValue(handle, value));
    }
    public static void svc_resetEyePose(){
        run(piSvcCAPI.INSTANCE.svc_resetEyePose(handle));
    }
    public static boolean svc_getPimaxDefaultAudioConfig(){
        BooleanByReference val = new BooleanByReference();
        run(piSvcCAPI.INSTANCE.svc_getPimaxDefaultAudioConfig(handle, val));
        return val.getVal();
    }
    public static void svc_setPimaxDefaultAudioDevice(boolean enable){
        run(piSvcCAPI.INSTANCE.svc_setPimaxDefaultAudioDevice(handle, enable));
    }
    public static void svc_resetFactorySettings(){
        run(piSvcCAPI.INSTANCE.svc_resetFactorySettings(handle));
    }
    public static void svc_slopeCalibrationHmd(){
        run(piSvcCAPI.INSTANCE.svc_slopeCalibrationHmd(handle));
    }
    public static void svc_setIntConfig(String key, int value){
        run(piSvcCAPI.INSTANCE.svc_setIntConfig(handle, key, value));
    }
    public static int svc_getIntConfig(String key){
        IntByReference val = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getIntConfig(handle, key, val));
        return val.getValue();
    }
    public static void svc_setFloatConfig(String key, float value){
        run(piSvcCAPI.INSTANCE.svc_setFloatConfig(handle, key, value));
    }
    public static float svc_getFloatConfig(String key){
        FloatByReference val = new FloatByReference();
        run(piSvcCAPI.INSTANCE.svc_getFloatConfig(handle, key, val));
        return val.getValue();
    }
    public static void svc_setStringConfig(String key, String value){
        run(piSvcCAPI.INSTANCE.svc_setStringConfig(handle, key, value));
    }
    public static String svc_getStringConfig(String key, int size){
        StringByReference val = new StringByReference();
        run(piSvcCAPI.INSTANCE.svc_getStringConfig(handle, key, val, size));
        return val.getVal();
    }
    public static void svc_setStringDeviceConfig(String key, String value){
        run(piSvcCAPI.INSTANCE.svc_setStringDeviceConfig(handle, key, value));
    }
    public static String svc_getStringDeviceConfig(String key, int size){
        StringByReference val = new StringByReference();
        run(piSvcCAPI.INSTANCE.svc_getStringDeviceConfig(handle, key, val, size));
        return val.getVal();
    }
    public static piVector3f svc_getVector3fConfig(String key){
        piVector3f vector = new piVector3f();
        run(piSvcCAPI.INSTANCE.svc_getVector3fConfig(handle, key, vector));
        return vector;
    }
    public static void svc_setVector3fConfig(String key, piVector3f value){
        run(piSvcCAPI.INSTANCE.svc_setVector3fConfig(handle, key, value));
    }
    public static piVector3f svc_getHmdPosition(){
        FloatByReference x = new FloatByReference();
        FloatByReference y = new FloatByReference();
        FloatByReference z = new FloatByReference();
        run(piSvcCAPI.INSTANCE.svc_getHmdPosition(handle, x, y, z));
        piVector3f vector = new piVector3f();
        vector.x = x.getValue();
        vector.y = y.getValue();
        vector.z = z.getValue();
        return vector;
    }
    public static void svc_magnetismCalibrationHmd(){
        run(piSvcCAPI.INSTANCE.svc_magnetismCalibrationHmd(handle));
    }
    public static void svc_magnetismCalibrationed(){
        run(piSvcCAPI.INSTANCE.svc_magnetismCalibrationed(handle));
    }
    public static int[] svc_getInterfaceVersion(){
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getInterfaceVersion(handle, major, minor));
        return new int[]{major.getValue(), minor.getValue()};
    }
    public static int[] svc_getServiceVersion(){
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getInterfaceVersion(handle, major, minor));
        return new int[]{major.getValue(), minor.getValue()};
    }
    public static int svc_getServiceLogLevel(){
        IntByReference val = new IntByReference();
        run(piSvcCAPI.INSTANCE.svc_getServiceLogLevel(handle, val));
        return val.getValue();
    }
    public static void svc_setServiceLogLevel(int level){
        run(piSvcCAPI.INSTANCE.svc_setServiceLogLevel(handle, level));
    }
    private static void run(int result){
        Logger.push(PiSvc.class);
        error = result;
        if(debug&&error!=0)Logger.error("Error: "+piSvcResult.getName(error));
        Logger.pop();
    }
    public static int checkError(){
        int err = error;
        error = 0;
        return err;
    }
}
