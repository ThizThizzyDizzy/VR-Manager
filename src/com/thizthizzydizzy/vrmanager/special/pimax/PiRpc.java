package com.thizthizzydizzy.vrmanager.special.pimax;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.PiRpcAPI;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.ControllerStatus;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.ErrorInfo;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.Game;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.LocatorStatus;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.P3B_Charge;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.SerialNumber;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.SystemInfo;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.types.TrackerStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class PiRpc{
    private static HashMap<String, Object> rpcCallMessage(String type, int maxAttempts){
        return PiRpcAPI.rpcCallMessage(PiRpcAPI.getEnum("MsgType").findValueByName(type), maxAttempts);
    }
    public static boolean Get_HMDStatus_hmd_connect(){
        return (boolean)rpcCallMessage("Get_HMDStatus_hmd_connect", 1).get("hmd_connect");
    }
    public static boolean Get_HMDStatus_spaceCalibrated(){
        return (boolean)rpcCallMessage("Get_HMDStatus_spaceCalibrated", 1).get("spaceCalibrated");
    }
    public static boolean Get_HMDStatus_leapMotionPlugin(){
        return (boolean)rpcCallMessage("Get_HMDStatus_leapMotionPlugin", 1).get("leapMotionPlugin");
    }
    public static boolean Get_HMDStatus_headTracked(){
        return (boolean)rpcCallMessage("Get_HMDStatus_headTracked", 1).get("headTracked");
    }
    public static boolean Get_HMDStatus_leapMotionInstalled(){
        return (boolean)rpcCallMessage("Get_HMDStatus_leapMotionInstalled", 1).get("leapMotionInstalled");
    }
    public static boolean Get_HMDStatus_leapMotionServiceReady(){
        return (boolean)rpcCallMessage("Get_HMDStatus_leapMotionServiceReady", 1).get("leapMotionServiceReady");
    }
    public static boolean Get_HMDStatus_leapMotionDeviceReady(){
        return (boolean)rpcCallMessage("Get_HMDStatus_leapMotionDeviceReady", 1).get("leapMotionDeviceReady");
    }
    public static boolean Get_HMDStatus_eyeTrackInstalled(){
        return (boolean)rpcCallMessage("Get_HMDStatus_eyeTrackInstalled", 1).get("eyeTrackInstalled");
    }
    public static boolean Get_HMDStatus_eyeTrackPlugin(){
        return (boolean)rpcCallMessage("Get_HMDStatus_eyeTrackPlugin", 1).get("eyeTrackPlugin");
    }
    public static boolean Get_HMDStatus_eyeTrackServiceReady(){
        return (boolean)rpcCallMessage("Get_HMDStatus_eyeTrackServiceReady", 1).get("eyeTrackServiceReady");
    }
    public static int Get_HMDStatus_errorCode(){
        return (int)rpcCallMessage("Get_HMDStatus_errorCode", 1).get("errorCode");
    }
    public static int Get_HMDStatus_modeType(){
        return (int)rpcCallMessage("Get_HMDStatus_modeType", 1).get("modeType");
    }
    public static String Get_HMDStatus_hmd_name(){
        return (String)rpcCallMessage("Get_HMDStatus_hmd_name", 1).get("hmd_name");
    }
    public static String Get_HMDStatus_displayHmdName(){
        return (String)rpcCallMessage("Get_HMDStatus_displayHmdName", 1).get("displayHmdName");
    }
    public static ControllerStatus Get_HMDStatus_ControllerStatus(){
        var values = rpcCallMessage("Get_HMDStatus_ControllerStatus", 1);
        ControllerStatus status = new ControllerStatus();
        status.controller0 = (boolean)values.get("controller0");
        status.controller0Status = (int)values.get("controller0Status");
        status.controller0Tracked = (boolean)values.get("controller0Tracked");
        status.controller0Product = (String)values.get("controller0Product");
        status.controller1 = (boolean)values.get("controller1");
        status.controller1Status = (int)values.get("controller1Status");
        status.controller1Tracked = (boolean)values.get("controller1Tracked");
        status.controller1Product = (String)values.get("controller1Product");
        return status;
    }
    public static LocatorStatus Get_HMDStatus_LocatorStatus(){
        var values = rpcCallMessage("Get_HMDStatus_LocatorStatus", 1);
        LocatorStatus status = new LocatorStatus();
        ArrayList locatorTracked = (ArrayList)values.get("locatorTracked");
        ArrayList locator = (ArrayList)values.get("locator");
        for(int i = 0; i<locatorTracked.size(); i++)status.locatorTracked[i] = (boolean)locatorTracked.get(i);
        for(int i = 0; i<locator.size(); i++)status.locator[i] = (boolean)locator.get(i);
        return status;
    }
    public static TrackerStatus Get_HMDStatus_TrackerStatus(){
        var values = rpcCallMessage("Get_HMDStatus_TrackerStatus", 1);
        TrackerStatus status = new TrackerStatus();
        ArrayList trackerTracked = (ArrayList)values.get("trackerTracked");
        ArrayList tracker = (ArrayList)values.get("tracker");
        for(int i = 0; i<trackerTracked.size(); i++)status.trackerTracked[i] = (boolean)trackerTracked.get(i);
        for(int i = 0; i<tracker.size(); i++)status.tracker[i] = (boolean)tracker.get(i);
        return status;
    }
    public static List<Game> Get_Game_GameList(){
        var values = rpcCallMessage("Get_Game_GameList", 1);
        ArrayList<Game> games = new ArrayList<>();
        for(Object str : (ArrayList)values.get("GameList")){
            String[] fields = ((String)str).split("\\|-\\|");
            Game game = new Game();
            game.name = fields[0];
            game.source = fields[1];
            game.imageLink = fields[2];
            game.path = fields[3];
            //always 1.0.1
            //always -2147483
            //always -2147483
            game.id = fields[7];
        }
        return games;
    }
    public static boolean Get_HMDStatus_lighthouseEnabledState(){
        return (boolean)rpcCallMessage("Get_HMDStatus_lighthouseEnabledState", 1).get("lighthouseEnabledState");
    }
    public static String Get_GeneralSetting_FirmwareVersion(){
        return (String)rpcCallMessage("Get_GeneralSetting_FirmwareVersion", 1).get("Setting_FirmwareVersion");
    }
    public static boolean Get_GeneralSetting_PvrHome(){
        return (boolean)rpcCallMessage("Get_GeneralSetting_PvrHome", 1).get("Setting_Home_Clicked");
    }
    public static boolean Get_GeneralSetting_ScreenProtector(){
        return (boolean)rpcCallMessage("Get_GeneralSetting_ScreenProtector", 1).get("Setting_ScreenProtector_Clicked");
    }
    public static SerialNumber Get_GeneralSetting_SerialNumber(){
        var values = rpcCallMessage("Get_GeneralSetting_SerialNumber", 1);
        SerialNumber serialNumber = new SerialNumber();
        serialNumber.HardwareSn = (String)values.get("HardwareSn");
        serialNumber.DeviceId = (String)values.get("DeviceId");
        serialNumber.SN_Code_Lock_Status = (String)values.get("SN_Code_Lock_Status");
        serialNumber.Sn = (String)values.get("Sn");
        return serialNumber;
    }
    public static P3B_Charge Get_HMDStatus_P3B_Charge(){
        var values = rpcCallMessage("Get_HMDStatus_P3B_Charge", 1);
        P3B_Charge charge = new P3B_Charge();
        charge.hmdchargeing4p3b = (boolean)values.get("hmdchargeing4p3b");
        charge.rcrlcharge4p3b = (int)values.get("rcrlcharge4p3b");
        charge.hmdcharge4p3b = (int)values.get("hmdcharge4p3b");
        charge.lcrlcharge4p3b = (int)values.get("lcrlcharge4p3b");
        return charge;
    }
    public static ErrorInfo Get_HMDStatus_ErrorInfo(){
        var values = rpcCallMessage("Get_HMDStatus_ErrorInfo", 1);
        ErrorInfo info = new ErrorInfo();
        info.error_title = (String)values.get("error_title");
        info.error_string = (String)values.get("error_string");
        info.string_show = (boolean)values.get("string_show");
        info.error_code = (int)values.get("error_code");
        info.title_can_click = (boolean)values.get("title_can_click");
        info.type = (int)values.get("type");
        info.string_can_click = (boolean)values.get("string_can_click");
        info.hmd_name = (String)values.get("hmd_name");
        return info;
    }
    public static SystemInfo Get_System_SystemInfo(){
        var values = rpcCallMessage("Get_System_SystemInfo", 1);
        SystemInfo info = new SystemInfo();
        info.memlen = (int)values.get("memlen");
        info.os = (String)values.get("os");
        info.cpu = (String)values.get("cpu");
        info.gpu = (String)values.get("gpu");
        return info;
    }
    public static String Get_Pimax_UserInfo(){
        return (String)rpcCallMessage("Get_System_SystemInfo", 1).get("hmd_name");
    }
    public static int Get_HMDStatus_currefresh(){
        return (int)rpcCallMessage("Get_HMDStatus_currefresh", 1).get("curRefresh");
    }
    public static boolean Get_HMDStatus_PimaxUiConfig(){
        return (boolean)rpcCallMessage("Get_HMDStatus_PimaxUiConfig", 1).get("turnOffEnable");
    }
    public static void Click_FirstShowConnect(){
        rpcCallMessage("Click_FirstShowConnect", 0);
    }
    public static void Click_ShowPitool(){
        rpcCallMessage("Click_ShowPitool", 0);
    }
    public static void Click_GuiStart(){
        rpcCallMessage("Click_GuiStart", 0);
    }
    public static void Click_SteamVR(){
        rpcCallMessage("Click_SteamVR", 0);
    }
    public static void Click_roomSetting(){
        rpcCallMessage("Click_roomSetting", 0);
    }
    public static void Click_PairControllerChoose(){
        rpcCallMessage("Click_PairControllerChoose", 0);
    }
    /**
     * @deprecated You probably want to use `Event_rebootHmdAuto` instead
     */
    @Deprecated
    public static void Click_rebootHmd(){
        rpcCallMessage("Click_PairControllerChoose", 0);
    }
    /**
     * @deprecated You probably want to use `Event_restartServiceAuto` instead
     */
    @Deprecated
    public static void Click_restartService(){
        rpcCallMessage("Click_restartService", 0);
    }
    public static void Click_LeapMotion(){
        rpcCallMessage("Click_LeapMotion", 0);
    }
    public static void Click_DownLoadLeapMotion(){
        rpcCallMessage("Click_DownLoadLeapMotion", 0);
    }
    public static void Click_ASeeVR(){
        rpcCallMessage("Click_ASeeVR", 0);
    }
    public static void Click_DownLoadASeeVR(){
        rpcCallMessage("Click_DownLoadASeeVR", 0);
    }
    public static void Click_GeneralSetting_PvrHome_Open(){
        rpcCallMessage("Click_GeneralSetting_PvrHome_Open", 0);
    }
    public static void Click_GeneralSetting_PvrHome_Close(){
        rpcCallMessage("Click_GeneralSetting_PvrHome_Close", 0);
    }
    public static void Click_GeneralSetting_ScreenProtector_Open(){
        rpcCallMessage("Click_GeneralSetting_ScreenProtector_Open", 0);
    }
    public static void Click_GeneralSetting_ScreenProtector_Close(){
        rpcCallMessage("Click_GeneralSetting_ScreenProtector_Close", 0);
    }
    public static void Event_LanguageSwitch_ZNCH(){
        rpcCallMessage("Event_LanguageSwitch_ZNCH", 0);
    }
    public static void Event_LanguageSwitch_ENUS(){
        rpcCallMessage("Event_LanguageSwitch_ENUS", 0);
    }
    public static void Event_Click_ErrorConnect(){
        rpcCallMessage("Event_Click_ErrorConnect", 0);
    }
    public static void Event_Click_UntrackerNote(){
        rpcCallMessage("Event_Click_UntrackerNote", 0);
    }
    public static void Event_Click_EyeTrackInstall(){
        rpcCallMessage("Event_Click_EyeTrackInstall", 0);
    }
    public static void Event_Click_EyeTrackStartServer(){
        rpcCallMessage("Event_Click_EyeTrackStartServer", 0);
    }
    public static void Event_Click_LeapInstall(){
        rpcCallMessage("Event_Click_LeapInstall", 0);
    }
    public static void Event_Click_LeapServiceStart(){
        rpcCallMessage("Event_Click_LeapServiceStart", 0);
    }
    public static void Event_Click_LeapConnect(){
        rpcCallMessage("Event_Click_LeapConnect", 0);
    }
    public static void Event_Click_Title(){
        rpcCallMessage("Event_Click_Title", 0);
    }
    public static void Event_Send_Pimax_Game(){
        rpcCallMessage("Event_Send_Pimax_Game", 0);
    }
    public static void Event_rebootHmdAuto(){
        rpcCallMessage("Event_rebootHmdAuto", 0);
    }
    public static void Event_restartServiceAuto(){
        rpcCallMessage("Event_restartServiceAuto", 0);
    }
    public static void Event_Click_RefreshLockStatus(){
        rpcCallMessage("Event_Click_RefreshLockStatus", 0);
    }
    public static void Event_ReLogin(){
        rpcCallMessage("Event_ReLogin", 0);
    }
    public static void Event_Click_HMDShutdown(){
        rpcCallMessage("Event_Click_HMDShutdown", 0);
    }
    public static void Event_ImportGame_Add(){
        rpcCallMessage("Event_ImportGame_Add", 0);
    }
    public static void Event_ImportGame_Delete(){
        rpcCallMessage("Event_ImportGame_Delete", 0);
    }
    public static List<Game> Event_RefreshGame(){
        var values = rpcCallMessage("Event_RefreshGame", 1);
        ArrayList<Game> games = new ArrayList<>();
        for(Object str : (ArrayList)values.get("GameList")){
            String[] fields = ((String)str).split("\\|-\\|");
            Game game = new Game();
            game.name = fields[0];
            game.source = fields[1];
            game.imageLink = fields[2];
            game.path = fields[3];
            //always 1.0.1
            //always -2147483
            //always -2147483
            game.id = fields[7];
        }
        return games;
    }
}
