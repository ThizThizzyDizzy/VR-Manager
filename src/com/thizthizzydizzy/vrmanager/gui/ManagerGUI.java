package com.thizthizzydizzy.vrmanager.gui;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.config.Configuration;
import com.thizthizzydizzy.vrmanager.module.VRModule;
import com.thizthizzydizzy.vrmanager.special.Usb;
import com.thizthizzydizzy.vrmanager.special.Usb.WatchInfo;
import com.thizthizzydizzy.vrmanager.special.pimax.PiRpc;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.PiRpcAPI;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.BrightState;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.LogLevel;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.Mode_Type;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.ToneState;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.TrackedDeviceBateryLevel;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.TrackedDeviceType;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.piHmdCapabilityMask;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcCapability.piResolutionCapabilityFlag;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcType.piSvcResult;
import com.thizthizzydizzy.vrmanager.task.Task;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.usb.UsbDevice;
public class ManagerGUI extends javax.swing.JFrame{
    private static ManagerGUI instance;
    private Configuration generatedConfig;
    private void updatePimaxBool(JPanel panel, Supplier<Boolean> boolSupplier){
        Boolean b = null;
        try{
            b = boolSupplier.get();
        }catch(Exception ex){
        }
        updatePimaxBool(panel, b);
    }
    private void updatePimaxBool(JPanel panel, Boolean b){
        if(b==null){
            panel.setBackground(null);
        }else{
            panel.setBackground(b?Color.green.darker():Color.red.darker());
        }
    }
    private void updatePimaxBool(JPanel panel, Boolean b, int error){
        if(error!=0)b = null;
        if(b==null){
            panel.setBackground(null);
        }else{
            panel.setBackground(b?Color.green.darker():Color.red.darker());
        }
    }
    private void updatePimaxBool(JPanel panel, Boolean b, JLabel statusLabel){
        int err = PiSvc.checkError();
        statusLabel.setText(piSvcResult.getName(err));
        updatePimaxBool(panel, b, err);
    }
    private void updatePimaxSvcMessage(JLabel label, String message){
        updatePimaxSvcMessage(label, message, PiSvc.checkError());
    }
    private void updatePimaxSvcMessage(JLabel label, String message, int error){
        if(error!=0)message = piSvcResult.getName(error);
        label.setText(message);
    }
    public ManagerGUI(){
        initComponents();
        panelDashboardPimax.setVisible(false);
        refreshModules();
        if(!new File("config.json").exists()){
            ((CardLayout)panelRoot.getLayout()).show(panelRoot, "autoconfig");
        }
        Thread t = new Thread(() -> {
            long lastPimaxCheck = System.currentTimeMillis();
            HashMap<Task, JLabel> taskLabels = new HashMap<>();
            HashMap<UsbDevice, JPanel> usbPanels = new HashMap<>();
            while(VRManager.running){
                try{
                    //TODO these should be moved to the actual modules
                    try{
                        // Pimax
                        panelDashboardPimax.setVisible(PiRpcAPI.active||PiSvc.active);
                        if(PiSvc.active){
                            PiSvc.svc_getUsbState();
                            int usbState = PiSvc.checkError();
                            updatePimaxBool(panelPimaxConnectionUSB, usbState==0);
                            labelPimaxConnectionUSB.setText(piSvcResult.getName(usbState));
                            PiSvc.svc_getHdmiState();
                            int hdmiState = PiSvc.checkError();
                            updatePimaxBool(panelPimaxConnectionVideo, hdmiState==0);
                            labelPimaxConnectionVideo.setText(piSvcResult.getName(hdmiState));
                            int serviceState = PiSvc.svc_getServiceConnection();
                            int err = PiSvc.checkError();
                            if(serviceState==1&&err==0){
                                panelPimaxConnectionService.setBackground(Color.green.darker());
                            }else if(serviceState==1){
                                panelPimaxConnectionService.setBackground(Color.yellow.darker());
                            }else{
                                panelPimaxConnectionService.setBackground(Color.red.darker());
                            }
                            updatePimaxSvcMessage(labelPimaxConnectionService, serviceState+"", err);
                            var info = PiSvc.svc_getSvcHmdDesc();
                            err = PiSvc.checkError();
                            updatePimaxSvcMessage(labelPimaxSvcProductName, String.copyValueOf(info.ProductName), err);
                            updatePimaxSvcMessage(labelPimaxSvcManufacturer, String.copyValueOf(info.Manufacturer), err);
                            updatePimaxSvcMessage(labelPimaxSvcSerialNumber, String.copyValueOf(info.SerialNumber), err);
                            updatePimaxSvcMessage(labelPimaxSvcVersionGen, String.copyValueOf(info.version_generation), err);
                            updatePimaxSvcMessage(labelPimaxSvcVersion, info.version_major+"."+info.version_Minor, err);
                            updatePimaxSvcMessage(labelPimaxSvcHmdice, String.copyValueOf(info.DisplayPiSvcHmdiceName), err);
                            updatePimaxSvcMessage(labelPimaxSvcResolution, piResolutionCapabilityFlag.getName(PiSvc.svc_getCurrentResolution()));
                            updatePimaxSvcMessage(labelPimaxSvcToneState, ToneState.getName(PiSvc.svc_getToneState()));
                            updatePimaxSvcMessage(labelPimaxSvcBrightState, BrightState.getName(PiSvc.svc_getBrightState()));
                            updatePimaxBool(panelPimaxSvcBlueLight, PiSvc.svc_getBlueLightState(), labelPimaxSvcBlueLight);
                            updatePimaxBool(panelPimaxSvcBacklight, PiSvc.svc_getBackLightState(), labelPimaxSvcBacklight);
                            updatePimaxBool(panelPimaxSvcProximity, PiSvc.svc_getProximityState(), labelPimaxSvcProximity);
                            updatePimaxBool(panelPimaxSvcGyro, PiSvc.svc_getGyroState(), labelPimaxSvcGyro);
                            updatePimaxSvcMessage(labelPimaxSvcIPD, PiSvc.svc_getIpdValue()+"");
                            updatePimaxSvcMessage(labelPimaxSvcLens, PiSvc.svc_getLensValue()+"");
                            updatePimaxSvcMessage(labelPimaxSvcDisplayQuality, PiSvc.svc_getDisplayQualityValue()+"");
                            updatePimaxBool(panelPimaxSvcDefAudio, PiSvc.svc_getPimaxDefaultAudioConfig(), labelPimaxSvcDefAudio);
                            int[] interfaceVersion = PiSvc.svc_getInterfaceVersion();
                            updatePimaxSvcMessage(labelPimaxSvcInterfaceVersion, interfaceVersion[0]+"."+interfaceVersion[1]);
                            int[] serviceVersion = PiSvc.svc_getServiceVersion();
                            updatePimaxSvcMessage(labelPimaxSvcServiceVersion, serviceVersion[0]+"."+serviceVersion[1]);
                            updatePimaxSvcMessage(labelPimaxSvcServiceLogLevel, LogLevel.getName(PiSvc.svc_getServiceLogLevel()));
                            updatePimaxSvcMessage(labelPimaxSvcPosition, PiSvc.svc_getHmdPosition().toString());
                            int caps = PiSvc.svc_getHmdCaps();
                            err = PiSvc.checkError();
                            updatePimaxBool(panelPimaxSvcCapModeCtrl, (caps&piHmdCapabilityMask.piHmdCap_ModeCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapBackLightCtrl, (caps&piHmdCapabilityMask.piHmdCap_BackLightCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapResolutionCtrl, (caps&piHmdCapabilityMask.piHmdCap_ResolutionCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapToneCtrl, (caps&piHmdCapabilityMask.piHmdCap_ToneCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapBrightCtrl, (caps&piHmdCapabilityMask.piHmdCap_BrightCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapIpdAdjust, (caps&piHmdCapabilityMask.piHmdCap_IpdAdjust)!=0);
                            updatePimaxBool(panelPimaxSvcCapBlueLightCtrl, (caps&piHmdCapabilityMask.piHmdCap_BlueLightCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapProximityCtrl, (caps&piHmdCapabilityMask.piHmdCap_ProximityCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapGyroControl, (caps&piHmdCapabilityMask.piHmdCap_GyroCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapEyePoseCtrl, (caps&piHmdCapabilityMask.piHmdCap_EyePoseCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapAudioCtrl, (caps&piHmdCapabilityMask.piHmdCap_AudioCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapDisplayQualityCtrl, (caps&piHmdCapabilityMask.piHmdCap_DisplayQualityCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapLensSeparationCtrl, (caps&piHmdCapabilityMask.piHmdCap_Lens_SeparationCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapReboot, (caps&piHmdCapabilityMask.piHmdCap_RebootCtrl)!=0);
                            updatePimaxBool(panelPimaxSvcCapBusinessFlag, (caps&piHmdCapabilityMask.piHmdCap_BussinessFlagCtrl)!=0);
                            labelPimaxSvcCapErr.setText(err==0?"":piSvcResult.getName(err));
                            int resCaps = PiSvc.svc_getResolutionCaps();
                            err = PiSvc.checkError();
                            updatePimaxBool(panelPimaxSvcCapRes1, (resCaps&piResolutionCapabilityFlag.e_1080_1920)!=0);
                            updatePimaxBool(panelPimaxSvcCapRes2, (resCaps&piResolutionCapabilityFlag.e_1440_2560)!=0);
                            updatePimaxBool(panelPimaxSvcCapRes3, (resCaps&piResolutionCapabilityFlag.e_1200_2160)!=0);
                            updatePimaxBool(panelPimaxSvcCapRes4, (resCaps&piResolutionCapabilityFlag.e_2160_3840)!=0);
                            labelPimaxSvcCapErr1.setText(err==0?"":piSvcResult.getName(err));
                            int modeCaps = PiSvc.svc_getModeCaps();
                            err = PiSvc.checkError();
                            updatePimaxBool(panelPimaxSvcCapMode1, (modeCaps&Mode_Type.mode_State_pimax_smart)!=0);
                            updatePimaxBool(panelPimaxSvcCapMode2, (modeCaps&Mode_Type.mode_State_video_p)!=0);
                            updatePimaxBool(panelPimaxSvcCapMode3, (modeCaps&Mode_Type.mode_State_dfu)!=0);
                            labelPimaxSvcCapErr2.setText(err==0?"":piSvcResult.getName(err));
                        }
                        if(PiRpcAPI.active&&System.currentTimeMillis()-lastPimaxCheck>500){//check every half second
                            lastPimaxCheck = System.currentTimeMillis();
                            updatePimaxBool(panelPimaxStatusHmdConnect, () -> PiRpc.Get_HMDStatus_hmd_connect());
                            updatePimaxBool(panelPimaxStatusSpaceCalibrated, () -> PiRpc.Get_HMDStatus_spaceCalibrated());
                            updatePimaxBool(panelPimaxStatusLeapMotionPlugin, () -> PiRpc.Get_HMDStatus_leapMotionPlugin());
                            updatePimaxBool(panelPimaxStatusHeadTracked, () -> PiRpc.Get_HMDStatus_headTracked());
                            updatePimaxBool(panelPimaxStatusLeapMotionInstalled, () -> PiRpc.Get_HMDStatus_leapMotionInstalled());
                            updatePimaxBool(panelPimaxStatusLeapMotionServiceReady, () -> PiRpc.Get_HMDStatus_leapMotionServiceReady());
                            updatePimaxBool(panelPimaxStatusLeapMotionDeviceReady, () -> PiRpc.Get_HMDStatus_leapMotionDeviceReady());
                            updatePimaxBool(panelPimaxStatusEyeTrackInstalled, () -> PiRpc.Get_HMDStatus_eyeTrackInstalled());
                            updatePimaxBool(panelPimaxStatusEyeTrackPlugin, () -> PiRpc.Get_HMDStatus_eyeTrackPlugin());
                            updatePimaxBool(panelPimaxStatusEyeTrackServiceReady, () -> PiRpc.Get_HMDStatus_eyeTrackServiceReady());
                            labelPimaxStatusErrorCode.setText(PiRpc.Get_HMDStatus_errorCode()+"");
                            labelPimaxStatusHmdMode.setText(PiRpc.Get_HMDStatus_modeType()+"");
                            labelPimaxStatusHmdName.setText(PiRpc.Get_HMDStatus_hmd_name());
                            labelPimaxStatusHmdDisplayName.setText(PiRpc.Get_HMDStatus_displayHmdName());
                            var controllers = PiRpc.Get_HMDStatus_ControllerStatus();
                            updatePimaxBool(panelPimaxStatusLeftController, () -> controllers.controller0);
                            updatePimaxBool(panelPimaxStatusRightController, () -> controllers.controller1);
                            labelPimaxStatusLeftControllerStatus.setText(controllers.controller0Status+"");
                            labelPimaxStatusRightControllerStatus.setText(controllers.controller1Status+"");
                            updatePimaxBool(panelPimaxStatusLeftControllerTracked, () -> controllers.controller0Tracked);
                            updatePimaxBool(panelPimaxStatusRightControllerTracked, () -> controllers.controller1Tracked);
                            labelPimaxStatusLeftControllerProduct.setText(controllers.controller0Product);
                            labelPimaxStatusRightControllerProduct.setText(controllers.controller1Product);
                            var locators = PiRpc.Get_HMDStatus_LocatorStatus();
                            panelPimaxStatusLocators.removeAll();
                            for(int i = 0; i<locators.locator.length; i++){
                                JPanel locatorPanel = new JPanel(new GridLayout(0, 1));
                                JPanel existsPanel = new JPanel(new GridLayout());
                                existsPanel.add(new JLabel(i+"", SwingConstants.CENTER));
                                updatePimaxBool(existsPanel, locators.locator[i]);
                                JPanel trackedPanel = new JPanel(new GridLayout());
                                trackedPanel.add(new JLabel("Tracked", SwingConstants.CENTER));
                                updatePimaxBool(trackedPanel, locators.locator[i]);
                                locatorPanel.add(existsPanel);
                                locatorPanel.add(trackedPanel);
                                panelPimaxStatusLocators.add(locatorPanel);
                            }
                            panelPimaxStatusTrackers.removeAll();
                            var trackers = PiRpc.Get_HMDStatus_TrackerStatus();
                            var trackerBattery = PiRpc.Get_HMDStatus_trackerBetterys();
                            for(int i = 0; i<trackers.tracker.length; i++){
                                JPanel trackerPanel = new JPanel(new GridLayout(0, 1));
                                JPanel existsPanel = new JPanel(new GridLayout());
                                existsPanel.add(new JLabel(i+"", SwingConstants.CENTER));
                                updatePimaxBool(existsPanel, trackers.tracker[i]);
                                JPanel trackedPanel = new JPanel(new GridLayout());
                                trackedPanel.add(new JLabel("Tracked", SwingConstants.CENTER));
                                updatePimaxBool(trackedPanel, trackers.trackerTracked[i]);
                                trackerPanel.add(existsPanel);
                                trackerPanel.add(trackedPanel);
                                if(i<trackerBattery.size())trackerPanel.add(new JLabel(trackerBattery.get(i)+"%", SwingConstants.CENTER));
                                panelPimaxStatusTrackers.add(trackerPanel);
                            }
                            updatePimaxBool(panelPimaxStatusLighthouseEnabled, () -> PiRpc.Get_HMDStatus_lighthouseEnabledState());
                            labelPimaxStatusFirmwareVersion.setText(PiRpc.Get_GeneralSetting_FirmwareVersion());
                            updatePimaxBool(panelPimaxStatusPvrHome, () -> PiRpc.Get_GeneralSetting_PvrHome());
                            updatePimaxBool(panelPimaxStatusScreenProtector, () -> PiRpc.Get_GeneralSetting_ScreenProtector());
                            var serialNumber = PiRpc.Get_GeneralSetting_SerialNumber();
                            labelPimaxStatusHardwareSerialNumber.setText(serialNumber.HardwareSn);
                            labelPimaxStatusDeviceID.setText(serialNumber.DeviceId);
                            updatePimaxBool(panelPimaxStatusSNCodeLock, () -> serialNumber.SN_Code_Lock_Status);
                            labelPimaxStatusSerialNumber.setText(serialNumber.Sn);
                            var hmdCharge = PiRpc.Get_HMDStatus_P3B_Charge();
                            updatePimaxBool(panelPimaxStatusHMDCharging, () -> hmdCharge.hmdchargeing4p3b);
                            labelPimaxStatusHMDCharge.setText(hmdCharge.hmdcharge4p3b+"%");
                            labelPimaxStatusLeftControllerCharge.setText(hmdCharge.lcrlcharge4p3b+"%");
                            labelPimaxStatusRightControllerCharge.setText(hmdCharge.rcrlcharge4p3b+"%");
                            var error = PiRpc.Get_HMDStatus_ErrorInfo();
                            labelPimaxStatusErrorTitle.setText(error.error_title);
                            labelPimaxStatusErrorString.setText(error.error_string);
                            updatePimaxBool(panelPimaxStatusShowString, () -> error.string_show);
                            labelPimaxErrorCode.setText(error.error_code+"");
                            updatePimaxBool(panelPimaxStatusCanClickTitle, () -> error.title_can_click);
                            labelPimaxStatusErrorType.setText(error.type+"");
                            updatePimaxBool(panelPimaxStatusCanClickString, () -> error.string_can_click);
                            labelPimaxStatusErrHmdName.setText(error.hmd_name);
                            var sysInfo = PiRpc.Get_System_SystemInfo();
                            labelPimaxStatusMemory.setText(sysInfo.memlen);
                            labelPimaxStatusOS.setText(sysInfo.os);
                            labelPimaxStatusCPU.setText(sysInfo.cpu);
                            labelPimaxStatusGPU.setText(sysInfo.gpu);
                            labelPimaxStatusUserInfo.setText(PiRpc.Get_Pimax_UserInfo());
                            labelPimaxStatusCurRefresh.setText(PiRpc.Get_HMDStatus_currefresh()+"");
                            updatePimaxBool(panelPimaxStatusUiConfig, () -> PiRpc.Get_HMDStatus_PimaxUiConfig());
                            labelPimaxStatusTrialInfo.setText(PiRpc.Get_HMDStatus_TrailInfo()+"");
                        }
                    }catch(Exception ex){
                    }
                    // USB
                    if(Usb.isActive()){
                        for(var device : Usb.allDevices){
                            WatchInfo watch = Usb.isWatching(device);
                            if(watch==null)continue;
                            boolean connected = Usb.devices.contains(device);
                            JPanel panel = usbPanels.get(device);
                            if(panel==null){
                                panel = new JPanel(new GridLayout());
                                var desc = device.getUsbDeviceDescriptor();
                                panel.add(new JLabel(watch.name==null?Integer.toHexString(desc.idVendor())+" "+Integer.toHexString(desc.idProduct()):(watch.name+(watch.productID<0?" "+Integer.toHexString(desc.idProduct()):""))));
                                panelUsbDevices.add(panel);
                                usbPanels.put(device, panel);
                            }
                            updatePimaxBool(panel, connected);
                        }
                    }

                    // Tasks
                    for(int i = 0; i<VRManager.tasks.size(); i++){
                        var task = VRManager.tasks.get(i);
                        JLabel label = taskLabels.get(task);
                        if(!task.isActive()){
                            if(label!=null){
                                panelTasks.remove(label);
                                revalidate();
                                taskLabels.remove(task);
                            }
                            continue;
                        }
                        if(label==null){
                            label = new JLabel(task.name+"    ");
                            label.setFont(labelTasks.getFont().deriveFont(Font.PLAIN));
                            label.setBackground(Color.white);
                            panelTasks.add(label);
                            revalidate();
                            taskLabels.put(task, label);
                        }
                    }
                    Thread.sleep(100);
                }catch(Exception ex){
                    Logger.error(ex);
                }
            }
        }, "Dashboard");
        t.setDaemon(true);
        t.start();
    }
    public void refreshModules(){
        panelModulesList.removeAll();
        ArrayList<VRModule> modules = new ArrayList<>(Arrays.asList(VRModule.modules));
        //put enabled modules up front, in order
        int i = 0;
        for(var m : VRManager.configuration.modules){
            for(VRModule mod : VRModule.modules){
                if(mod.getName().equals(m)){
                    modules.remove(mod);
                    modules.add(i++, mod);
                }
            }
        }
        //build the UI
        for(VRModule module : modules){
            JPanel row = new JPanel(new GridLayout(1, 0));
            var bg = new JPanel();
            var label = new JLabel(module.getName());
            bg.add(label);
            label.setFont(labelModules.getFont());
            bg.setBackground(VRManager.configuration.modules.contains(module.getName())?Color.green.darker():Color.red.darker());
            row.add(bg);
            if(module.hasConfiguration()){
                var configure = new JButton("Configure");
                configure.setFont(labelModules.getFont());
                configure.addActionListener((e) -> {
                    var dialog = module.getConfigurationGUI(this);
                    dialog.addWindowListener(new WindowAdapter(){
                        @Override
                        public void windowClosing(WindowEvent e){
                            VRManager.saveConfig();
                        }
                    });
                    dialog.setVisible(true);
                });
                row.add(configure);
            }else{
                var blankLabel = new JLabel();
                row.add(blankLabel);
            }
            var runOnStart = new JCheckBox("Run on initialization");
            runOnStart.setSelected(VRManager.configuration.modules.contains(module.getName()));
            runOnStart.setFont(labelModules.getFont());
            runOnStart.addActionListener((e) -> {
                if(runOnStart.isSelected())VRManager.configuration.modules.add(module.getName());
                else
                    VRManager.configuration.modules.remove(module.getName());
                VRManager.saveConfig();
                refreshModules();
            });
            row.add(runOnStart);
            panelModulesList.add(row);
            panelModules.revalidate();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        panelRoot = new javax.swing.JPanel();
        panelMain = new javax.swing.JPanel();
        panelMainTabs = new javax.swing.JPanel();
        tabButtonMain = new javax.swing.JToggleButton();
        tabButtonConfigure = new javax.swing.JToggleButton();
        panelMainContent = new javax.swing.JPanel();
        panelDashboard = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        labelTasks = new javax.swing.JLabel();
        panelTaskHeader = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        panelTasks = new javax.swing.JPanel();
        panelDashboardModules = new javax.swing.JPanel();
        panelDashboardPimax = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        buttonPimaxSetupGuide = new javax.swing.JButton();
        buttonPimaxStartSteamVR = new javax.swing.JButton();
        buttonPimaxRoomSetup = new javax.swing.JButton();
        buttonPimaxPairDevice = new javax.swing.JButton();
        buttonPimaxRestartHeadset = new javax.swing.JButton();
        buttonPimaxRestartService = new javax.swing.JButton();
        buttonPimaxDeviceSettings = new javax.swing.JButton();
        buttonPimaxShutDown = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        panelPimaxStatusLights = new javax.swing.JPanel();
        panelPimaxStatusHmdConnect = new javax.swing.JPanel();
        labelPimaxStatusHmdConnect = new javax.swing.JLabel();
        panelPimaxStatusSpaceCalibrated = new javax.swing.JPanel();
        labelPimaxStatusSpaceCalibrated = new javax.swing.JLabel();
        panelPimaxStatusLeapMotionPlugin = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionPlugin = new javax.swing.JLabel();
        panelPimaxStatusHeadTracked = new javax.swing.JPanel();
        labelPimaxStatusHeadTracked = new javax.swing.JLabel();
        panelPimaxStatusLeapMotionInstalled = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionInstalled = new javax.swing.JLabel();
        panelPimaxStatusLeapMotionServiceReady = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionServiceReady = new javax.swing.JLabel();
        panelPimaxStatusLeapMotionDeviceReady = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionDeviceReady = new javax.swing.JLabel();
        panelPimaxStatusEyeTrackInstalled = new javax.swing.JPanel();
        labelPimaxStatusEyeTrackInstalled = new javax.swing.JLabel();
        panelPimaxStatusEyeTrackPlugin = new javax.swing.JPanel();
        labelPimaxStatusEyeTrackPlugin = new javax.swing.JLabel();
        panelPimaxStatusEyeTrackServiceReady = new javax.swing.JPanel();
        labelPimaxStatusEyeTrackServiceReady = new javax.swing.JLabel();
        panelPimaxStatusLighthouseEnabled = new javax.swing.JPanel();
        labelPimaxStatusLighthouseEnabled = new javax.swing.JLabel();
        panelPimaxStatusPvrHome = new javax.swing.JPanel();
        labelPimaxStatusPvrHome = new javax.swing.JLabel();
        panelPimaxStatusScreenProtector = new javax.swing.JPanel();
        labelPimaxStatusScreenProtector = new javax.swing.JLabel();
        panelPimaxStatusUiConfig = new javax.swing.JPanel();
        labelPimaxStatusUiConfig = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        panelPimaxStatusHmdErrorCode = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        labelPimaxStatusErrorCode = new javax.swing.JLabel();
        panelPimaxStatusHmdModeType = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        labelPimaxStatusHmdMode = new javax.swing.JLabel();
        panelPimaxStatusHmdName = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        labelPimaxStatusHmdName = new javax.swing.JLabel();
        panelPimaxStatusDisplayHmdName = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        labelPimaxStatusHmdDisplayName = new javax.swing.JLabel();
        panelPimaxStatusFirmwareVersion = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        labelPimaxStatusFirmwareVersion = new javax.swing.JLabel();
        panelPimaxStatusSerialNumber = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        labelPimaxStatusHardwareSerialNumber = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        labelPimaxStatusDeviceID = new javax.swing.JLabel();
        panelPimaxStatusSNCodeLock = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        labelPimaxStatusSerialNumber = new javax.swing.JLabel();
        panelPimaxStatusHmdP3BCharge = new javax.swing.JPanel();
        panelPimaxStatusHMDCharging = new javax.swing.JPanel();
        jLabel91 = new javax.swing.JLabel();
        labelPimaxStatusHMDCharge = new javax.swing.JLabel();
        panelPimaxStatusCurRefresh = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        labelPimaxStatusCurRefresh = new javax.swing.JLabel();
        panelPimaxStatusUserInfo = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        labelPimaxStatusUserInfo = new javax.swing.JLabel();
        panelPimaxStatusTrialInfo = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        labelPimaxStatusTrialInfo = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        panelPimaxStatusControllers = new javax.swing.JPanel();
        panelPimaxStatusLeftController = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        panelPimaxStatusRightController = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        panelPimaxStatusLeftControllerTracked = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        panelPimaxStatusRightControllerTracked = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        labelPimaxStatusLeftControllerStatus = new javax.swing.JLabel();
        labelPimaxStatusRightControllerStatus = new javax.swing.JLabel();
        labelPimaxStatusLeftControllerProduct = new javax.swing.JLabel();
        labelPimaxStatusRightControllerProduct = new javax.swing.JLabel();
        labelPimaxStatusLeftControllerCharge = new javax.swing.JLabel();
        labelPimaxStatusRightControllerCharge = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        panelPimaxStatusLocators = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        panelPimaxStatusTrackers = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        panelPimaxStatusErrorInfo = new javax.swing.JPanel();
        labelPimaxStatusErrorTitle = new javax.swing.JLabel();
        panelPimaxStatusShowString = new javax.swing.JPanel();
        jLabel97 = new javax.swing.JLabel();
        labelPimaxErrorCode = new javax.swing.JLabel();
        panelPimaxStatusCanClickTitle = new javax.swing.JPanel();
        jLabel99 = new javax.swing.JLabel();
        labelPimaxStatusErrorType = new javax.swing.JLabel();
        panelPimaxStatusCanClickString = new javax.swing.JPanel();
        jLabel101 = new javax.swing.JLabel();
        labelPimaxStatusErrHmdName = new javax.swing.JLabel();
        labelPimaxStatusErrorString = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        panelPimaxStatusSystemInfo = new javax.swing.JPanel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        labelPimaxStatusMemory = new javax.swing.JLabel();
        labelPimaxStatusOS = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        labelPimaxStatusCPU = new javax.swing.JLabel();
        labelPimaxStatusGPU = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        panelPimaxConnectionUSB = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        panelPimaxConnectionVideo = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        panelPimaxConnectionService = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        labelPimaxConnectionUSB = new javax.swing.JLabel();
        labelPimaxConnectionVideo = new javax.swing.JLabel();
        labelPimaxConnectionService = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        panelPimaxSvc = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        labelPimaxSvcProductName = new javax.swing.JLabel();
        panelPimaxSvc1 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        labelPimaxSvcManufacturer = new javax.swing.JLabel();
        panelPimaxSvc2 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        labelPimaxSvcSerialNumber = new javax.swing.JLabel();
        panelPimaxSvc3 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        labelPimaxSvcVersionGen = new javax.swing.JLabel();
        panelPimaxSvc4 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        labelPimaxSvcVersion = new javax.swing.JLabel();
        panelPimaxSvc5 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        labelPimaxSvcHmdice = new javax.swing.JLabel();
        panelPimaxSvc8 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        labelPimaxSvcResolution = new javax.swing.JLabel();
        panelPimaxSvc10 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        labelPimaxSvcToneState = new javax.swing.JLabel();
        panelPimaxSvc11 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        labelPimaxSvcBrightState = new javax.swing.JLabel();
        panelPimaxSvc19 = new javax.swing.JPanel();
        panelPimaxSvcBlueLight = new javax.swing.JPanel();
        labelPimaxStatusPvrHome7 = new javax.swing.JLabel();
        labelPimaxSvcBlueLight = new javax.swing.JLabel();
        panelPimaxSvc20 = new javax.swing.JPanel();
        panelPimaxSvcBacklight = new javax.swing.JPanel();
        labelPimaxStatusPvrHome8 = new javax.swing.JLabel();
        labelPimaxSvcBacklight = new javax.swing.JLabel();
        panelPimaxSvc21 = new javax.swing.JPanel();
        panelPimaxSvcProximity = new javax.swing.JPanel();
        labelPimaxStatusPvrHome9 = new javax.swing.JLabel();
        labelPimaxSvcProximity = new javax.swing.JLabel();
        panelPimaxSvc18 = new javax.swing.JPanel();
        panelPimaxSvcGyro = new javax.swing.JPanel();
        labelPimaxStatusPvrHome5 = new javax.swing.JLabel();
        labelPimaxSvcGyro = new javax.swing.JLabel();
        panelPimaxSvc12 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        labelPimaxSvcIPD = new javax.swing.JLabel();
        panelPimaxSvc13 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        labelPimaxSvcLens = new javax.swing.JLabel();
        panelPimaxSvc14 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        labelPimaxSvcDisplayQuality = new javax.swing.JLabel();
        panelPimaxSvc23 = new javax.swing.JPanel();
        panelPimaxSvcDefAudio = new javax.swing.JPanel();
        labelPimaxStatusPvrHome10 = new javax.swing.JLabel();
        labelPimaxSvcDefAudio = new javax.swing.JLabel();
        panelPimaxSvc15 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        labelPimaxSvcInterfaceVersion = new javax.swing.JLabel();
        panelPimaxSvc16 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        labelPimaxSvcServiceVersion = new javax.swing.JLabel();
        panelPimaxSvc17 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        labelPimaxSvcServiceLogLevel = new javax.swing.JLabel();
        panelPimaxSvcPosition1 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        labelPimaxSvcPosition = new javax.swing.JLabel();
        panelHmdCapabilities = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        panelPimaxCapabilities = new javax.swing.JPanel();
        panelPimaxSvcCapModeCtrl = new javax.swing.JPanel();
        labelPimaxStatusHmdConnect1 = new javax.swing.JLabel();
        panelPimaxSvcCapBackLightCtrl = new javax.swing.JPanel();
        labelPimaxStatusSpaceCalibrated1 = new javax.swing.JLabel();
        panelPimaxSvcCapResolutionCtrl = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionPlugin1 = new javax.swing.JLabel();
        panelPimaxSvcCapToneCtrl = new javax.swing.JPanel();
        labelPimaxStatusHeadTracked1 = new javax.swing.JLabel();
        panelPimaxSvcCapBrightCtrl = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionInstalled1 = new javax.swing.JLabel();
        panelPimaxSvcCapIpdAdjust = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionServiceReady1 = new javax.swing.JLabel();
        panelPimaxSvcCapBlueLightCtrl = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionDeviceReady1 = new javax.swing.JLabel();
        panelPimaxSvcCapProximityCtrl = new javax.swing.JPanel();
        labelPimaxStatusEyeTrackInstalled1 = new javax.swing.JLabel();
        panelPimaxSvcCapGyroControl = new javax.swing.JPanel();
        labelPimaxStatusEyeTrackPlugin1 = new javax.swing.JLabel();
        panelPimaxSvcCapEyePoseCtrl = new javax.swing.JPanel();
        labelPimaxStatusEyeTrackServiceReady1 = new javax.swing.JLabel();
        panelPimaxSvcCapAudioCtrl = new javax.swing.JPanel();
        labelPimaxStatusLighthouseEnabled1 = new javax.swing.JLabel();
        panelPimaxSvcCapDisplayQualityCtrl = new javax.swing.JPanel();
        labelPimaxStatusPvrHome1 = new javax.swing.JLabel();
        panelPimaxSvcCapLensSeparationCtrl = new javax.swing.JPanel();
        labelPimaxStatusScreenProtector1 = new javax.swing.JLabel();
        panelPimaxSvcCapReboot = new javax.swing.JPanel();
        labelPimaxStatusUiConfig1 = new javax.swing.JLabel();
        panelPimaxSvcCapBusinessFlag = new javax.swing.JPanel();
        labelPimaxStatusUiConfig2 = new javax.swing.JLabel();
        labelPimaxSvcCapErr = new javax.swing.JLabel();
        panelResolutionCapabilities = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        panelPimaxCapabilities1 = new javax.swing.JPanel();
        panelPimaxSvcCapRes1 = new javax.swing.JPanel();
        labelPimaxStatusHmdConnect2 = new javax.swing.JLabel();
        panelPimaxSvcCapRes2 = new javax.swing.JPanel();
        labelPimaxStatusSpaceCalibrated2 = new javax.swing.JLabel();
        panelPimaxSvcCapRes3 = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionPlugin2 = new javax.swing.JLabel();
        panelPimaxSvcCapRes4 = new javax.swing.JPanel();
        labelPimaxStatusHeadTracked2 = new javax.swing.JLabel();
        labelPimaxSvcCapErr1 = new javax.swing.JLabel();
        panelResolutionCapabilities1 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        panelPimaxCapabilities2 = new javax.swing.JPanel();
        panelPimaxSvcCapMode1 = new javax.swing.JPanel();
        labelPimaxStatusHmdConnect3 = new javax.swing.JLabel();
        panelPimaxSvcCapMode2 = new javax.swing.JPanel();
        labelPimaxStatusSpaceCalibrated3 = new javax.swing.JLabel();
        panelPimaxSvcCapMode3 = new javax.swing.JPanel();
        labelPimaxStatusLeapMotionPlugin3 = new javax.swing.JLabel();
        labelPimaxSvcCapErr2 = new javax.swing.JLabel();
        panelDashboardUSB = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        panelTaskHeader1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel21 = new javax.swing.JPanel();
        panelUsbDevices = new javax.swing.JPanel();
        panelConfigure = new javax.swing.JPanel();
        panelModules = new javax.swing.JPanel();
        labelModules = new javax.swing.JLabel();
        scrollableModules = new javax.swing.JScrollPane();
        panelModulesListContainer = new javax.swing.JPanel();
        panelModulesList = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        panelAutoconfig = new javax.swing.JPanel();
        labelAutoconfigTitle = new javax.swing.JLabel();
        panelAutoconfigButtons = new javax.swing.JPanel();
        buttonAutoconfigSkip = new javax.swing.JButton();
        buttonAutoconfigConfirm = new javax.swing.JButton();
        scrollableAutoconfig = new javax.swing.JScrollPane();
        labelAutoconfig = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("VR Manager");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelRoot.setLayout(new java.awt.CardLayout());

        panelMain.setLayout(new java.awt.BorderLayout());

        panelMainTabs.setLayout(new java.awt.GridLayout(1, 0));

        buttonGroup1.add(tabButtonMain);
        tabButtonMain.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        tabButtonMain.setSelected(true);
        tabButtonMain.setText("Dashboard");
        tabButtonMain.setMargin(new java.awt.Insets(12, 14, 13, 14));
        tabButtonMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabButtonMainActionPerformed(evt);
            }
        });
        panelMainTabs.add(tabButtonMain);

        buttonGroup1.add(tabButtonConfigure);
        tabButtonConfigure.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        tabButtonConfigure.setText("Configuration");
        tabButtonConfigure.setMargin(new java.awt.Insets(12, 14, 13, 14));
        tabButtonConfigure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabButtonConfigureActionPerformed(evt);
            }
        });
        panelMainTabs.add(tabButtonConfigure);

        panelMain.add(panelMainTabs, java.awt.BorderLayout.PAGE_START);

        panelMainContent.setLayout(new java.awt.CardLayout());

        panelDashboard.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        buttonStop.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        buttonStop.setForeground(new java.awt.Color(102, 0, 0));
        buttonStop.setText("STOP ALL TASKS");
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });
        jPanel1.add(buttonStop);

        buttonStart.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        buttonStart.setForeground(new java.awt.Color(0, 102, 0));
        buttonStart.setText("START");
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });
        jPanel1.add(buttonStart);

        panelDashboard.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.BorderLayout());

        labelTasks.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        labelTasks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTasks.setText("Active Tasks");
        jPanel2.add(labelTasks, java.awt.BorderLayout.NORTH);

        panelTaskHeader.setBackground(new java.awt.Color(255, 255, 255));
        panelTaskHeader.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.BorderLayout());

        panelTasks.setLayout(new java.awt.GridLayout(0, 1));
        jPanel3.add(panelTasks, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(jPanel3);

        panelTaskHeader.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(panelTaskHeader, java.awt.BorderLayout.CENTER);

        panelDashboard.add(jPanel2, java.awt.BorderLayout.LINE_END);

        panelDashboardModules.setLayout(new java.awt.BorderLayout());

        panelDashboardPimax.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Pimax");
        panelDashboardPimax.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jPanel4.setLayout(new java.awt.GridLayout(0, 2));

        buttonPimaxSetupGuide.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxSetupGuide.setText("Setup Guide");
        buttonPimaxSetupGuide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxSetupGuideActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxSetupGuide);

        buttonPimaxStartSteamVR.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxStartSteamVR.setText("Start SteamVR");
        buttonPimaxStartSteamVR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxStartSteamVRActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxStartSteamVR);

        buttonPimaxRoomSetup.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxRoomSetup.setText("Room Setup");
        buttonPimaxRoomSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxRoomSetupActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxRoomSetup);

        buttonPimaxPairDevice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxPairDevice.setText("Pair Device");
        buttonPimaxPairDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxPairDeviceActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxPairDevice);

        buttonPimaxRestartHeadset.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxRestartHeadset.setText("Restart Headset");
        buttonPimaxRestartHeadset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxRestartHeadsetActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxRestartHeadset);

        buttonPimaxRestartService.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxRestartService.setText("Restart Service");
        buttonPimaxRestartService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxRestartServiceActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxRestartService);

        buttonPimaxDeviceSettings.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxDeviceSettings.setText("Device Settings");
        buttonPimaxDeviceSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxDeviceSettingsActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxDeviceSettings);

        buttonPimaxShutDown.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonPimaxShutDown.setText("Shut Down Headset");
        buttonPimaxShutDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPimaxShutDownActionPerformed(evt);
            }
        });
        jPanel4.add(buttonPimaxShutDown);

        panelDashboardPimax.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        jPanel19.setLayout(new java.awt.GridLayout(1, 0));

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.PAGE_AXIS));

        panelPimaxStatusLights.setLayout(new java.awt.GridLayout(0, 3));

        panelPimaxStatusHmdConnect.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusHmdConnect.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusHmdConnect.setText("HMD Connected");
        panelPimaxStatusHmdConnect.add(labelPimaxStatusHmdConnect);

        panelPimaxStatusLights.add(panelPimaxStatusHmdConnect);

        panelPimaxStatusSpaceCalibrated.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusSpaceCalibrated.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusSpaceCalibrated.setText("Space Calibrated");
        panelPimaxStatusSpaceCalibrated.add(labelPimaxStatusSpaceCalibrated);

        panelPimaxStatusLights.add(panelPimaxStatusSpaceCalibrated);

        panelPimaxStatusLeapMotionPlugin.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionPlugin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionPlugin.setText("Leap Motion Plugin");
        panelPimaxStatusLeapMotionPlugin.add(labelPimaxStatusLeapMotionPlugin);

        panelPimaxStatusLights.add(panelPimaxStatusLeapMotionPlugin);

        panelPimaxStatusHeadTracked.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusHeadTracked.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusHeadTracked.setText("Head Tracked");
        panelPimaxStatusHeadTracked.add(labelPimaxStatusHeadTracked);

        panelPimaxStatusLights.add(panelPimaxStatusHeadTracked);

        panelPimaxStatusLeapMotionInstalled.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionInstalled.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionInstalled.setText("Leap Motion Installed");
        panelPimaxStatusLeapMotionInstalled.add(labelPimaxStatusLeapMotionInstalled);

        panelPimaxStatusLights.add(panelPimaxStatusLeapMotionInstalled);

        panelPimaxStatusLeapMotionServiceReady.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionServiceReady.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionServiceReady.setText("Leap Motion Service Ready");
        panelPimaxStatusLeapMotionServiceReady.add(labelPimaxStatusLeapMotionServiceReady);

        panelPimaxStatusLights.add(panelPimaxStatusLeapMotionServiceReady);

        panelPimaxStatusLeapMotionDeviceReady.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionDeviceReady.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionDeviceReady.setText("Leap Motion Device Ready");
        panelPimaxStatusLeapMotionDeviceReady.add(labelPimaxStatusLeapMotionDeviceReady);

        panelPimaxStatusLights.add(panelPimaxStatusLeapMotionDeviceReady);

        panelPimaxStatusEyeTrackInstalled.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusEyeTrackInstalled.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusEyeTrackInstalled.setText("Eye Tracking Installed");
        panelPimaxStatusEyeTrackInstalled.add(labelPimaxStatusEyeTrackInstalled);

        panelPimaxStatusLights.add(panelPimaxStatusEyeTrackInstalled);

        panelPimaxStatusEyeTrackPlugin.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusEyeTrackPlugin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusEyeTrackPlugin.setText("Eye Tracking Plugin");
        panelPimaxStatusEyeTrackPlugin.add(labelPimaxStatusEyeTrackPlugin);

        panelPimaxStatusLights.add(panelPimaxStatusEyeTrackPlugin);

        panelPimaxStatusEyeTrackServiceReady.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusEyeTrackServiceReady.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusEyeTrackServiceReady.setText("Eye Tracking Service Ready");
        panelPimaxStatusEyeTrackServiceReady.add(labelPimaxStatusEyeTrackServiceReady);

        panelPimaxStatusLights.add(panelPimaxStatusEyeTrackServiceReady);

        panelPimaxStatusLighthouseEnabled.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLighthouseEnabled.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLighthouseEnabled.setText("Lighthouse Tracking");
        panelPimaxStatusLighthouseEnabled.add(labelPimaxStatusLighthouseEnabled);

        panelPimaxStatusLights.add(panelPimaxStatusLighthouseEnabled);

        panelPimaxStatusPvrHome.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusPvrHome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusPvrHome.setText("Pimax VR Home");
        panelPimaxStatusPvrHome.add(labelPimaxStatusPvrHome);

        panelPimaxStatusLights.add(panelPimaxStatusPvrHome);

        panelPimaxStatusScreenProtector.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusScreenProtector.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusScreenProtector.setText("Screen Protector");
        panelPimaxStatusScreenProtector.add(labelPimaxStatusScreenProtector);

        panelPimaxStatusLights.add(panelPimaxStatusScreenProtector);

        panelPimaxStatusUiConfig.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusUiConfig.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusUiConfig.setText("Pimax UI Config");
        panelPimaxStatusUiConfig.add(labelPimaxStatusUiConfig);

        panelPimaxStatusLights.add(panelPimaxStatusUiConfig);

        jPanel5.add(panelPimaxStatusLights);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("HMD");
        jPanel6.add(jLabel4, java.awt.BorderLayout.NORTH);

        jPanel7.setLayout(new java.awt.GridLayout(0, 4));

        panelPimaxStatusHmdErrorCode.setLayout(new java.awt.GridLayout(0, 1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Error Code");
        panelPimaxStatusHmdErrorCode.add(jLabel3);

        labelPimaxStatusErrorCode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusHmdErrorCode.add(labelPimaxStatusErrorCode);

        jPanel7.add(panelPimaxStatusHmdErrorCode);

        panelPimaxStatusHmdModeType.setLayout(new java.awt.GridLayout(0, 1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Mode Type");
        panelPimaxStatusHmdModeType.add(jLabel5);

        labelPimaxStatusHmdMode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusHmdModeType.add(labelPimaxStatusHmdMode);

        jPanel7.add(panelPimaxStatusHmdModeType);

        panelPimaxStatusHmdName.setLayout(new java.awt.GridLayout(0, 1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Name");
        panelPimaxStatusHmdName.add(jLabel7);

        labelPimaxStatusHmdName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusHmdName.add(labelPimaxStatusHmdName);

        jPanel7.add(panelPimaxStatusHmdName);

        panelPimaxStatusDisplayHmdName.setLayout(new java.awt.GridLayout(0, 1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Display Name");
        panelPimaxStatusDisplayHmdName.add(jLabel9);

        labelPimaxStatusHmdDisplayName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusDisplayHmdName.add(labelPimaxStatusHmdDisplayName);

        jPanel7.add(panelPimaxStatusDisplayHmdName);

        panelPimaxStatusFirmwareVersion.setLayout(new java.awt.GridLayout(0, 1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Firmware Version");
        panelPimaxStatusFirmwareVersion.add(jLabel11);

        labelPimaxStatusFirmwareVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusFirmwareVersion.add(labelPimaxStatusFirmwareVersion);

        jPanel7.add(panelPimaxStatusFirmwareVersion);

        panelPimaxStatusSerialNumber.setLayout(new java.awt.GridLayout(0, 1));

        jLabel83.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel83.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel83.setText("Hardware Serial #");
        panelPimaxStatusSerialNumber.add(jLabel83);

        labelPimaxStatusHardwareSerialNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusSerialNumber.add(labelPimaxStatusHardwareSerialNumber);

        jPanel7.add(panelPimaxStatusSerialNumber);

        jPanel12.setLayout(new java.awt.GridLayout(0, 1));

        jLabel85.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel85.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel85.setText("Device ID");
        jPanel12.add(jLabel85);

        labelPimaxStatusDeviceID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel12.add(labelPimaxStatusDeviceID);

        jPanel7.add(jPanel12);

        panelPimaxStatusSNCodeLock.setLayout(new java.awt.GridLayout(0, 1));

        jLabel87.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel87.setText("SN Code Lock");
        panelPimaxStatusSNCodeLock.add(jLabel87);

        jPanel7.add(panelPimaxStatusSNCodeLock);

        jPanel14.setLayout(new java.awt.GridLayout(0, 1));

        jLabel89.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel89.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel89.setText("Serial Number");
        jPanel14.add(jLabel89);

        labelPimaxStatusSerialNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel14.add(labelPimaxStatusSerialNumber);

        jPanel7.add(jPanel14);

        panelPimaxStatusHmdP3BCharge.setLayout(new java.awt.GridLayout(2, 1));

        panelPimaxStatusHMDCharging.setLayout(new java.awt.GridLayout(1, 0));

        jLabel91.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel91.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel91.setText("HMD Charging");
        panelPimaxStatusHMDCharging.add(jLabel91);

        panelPimaxStatusHmdP3BCharge.add(panelPimaxStatusHMDCharging);

        labelPimaxStatusHMDCharge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusHmdP3BCharge.add(labelPimaxStatusHMDCharge);

        jPanel7.add(panelPimaxStatusHmdP3BCharge);

        panelPimaxStatusCurRefresh.setLayout(new java.awt.GridLayout(0, 1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Refresh Rate Setting");
        panelPimaxStatusCurRefresh.add(jLabel15);

        labelPimaxStatusCurRefresh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusCurRefresh.add(labelPimaxStatusCurRefresh);

        jPanel7.add(panelPimaxStatusCurRefresh);

        panelPimaxStatusUserInfo.setLayout(new java.awt.GridLayout(0, 1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("User Info");
        panelPimaxStatusUserInfo.add(jLabel13);

        labelPimaxStatusUserInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusUserInfo.add(labelPimaxStatusUserInfo);

        jPanel7.add(panelPimaxStatusUserInfo);

        panelPimaxStatusTrialInfo.setLayout(new java.awt.GridLayout(0, 1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Trial Info");
        panelPimaxStatusTrialInfo.add(jLabel17);

        labelPimaxStatusTrialInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusTrialInfo.add(labelPimaxStatusTrialInfo);

        jPanel7.add(panelPimaxStatusTrialInfo);

        jPanel6.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel6);

        jPanel8.setLayout(new java.awt.BorderLayout());

        panelPimaxStatusControllers.setLayout(new java.awt.GridLayout(5, 2));

        panelPimaxStatusLeftController.setLayout(new java.awt.GridLayout(1, 0));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Left Controller");
        panelPimaxStatusLeftController.add(jLabel19);

        panelPimaxStatusControllers.add(panelPimaxStatusLeftController);

        panelPimaxStatusRightController.setLayout(new java.awt.GridLayout(1, 0));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Right Controller");
        panelPimaxStatusRightController.add(jLabel20);

        panelPimaxStatusControllers.add(panelPimaxStatusRightController);

        panelPimaxStatusLeftControllerTracked.setLayout(new java.awt.GridLayout(1, 0));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Tracked");
        panelPimaxStatusLeftControllerTracked.add(jLabel21);

        panelPimaxStatusControllers.add(panelPimaxStatusLeftControllerTracked);

        panelPimaxStatusRightControllerTracked.setLayout(new java.awt.GridLayout(1, 0));

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Tracked");
        panelPimaxStatusRightControllerTracked.add(jLabel22);

        panelPimaxStatusControllers.add(panelPimaxStatusRightControllerTracked);

        labelPimaxStatusLeftControllerStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusControllers.add(labelPimaxStatusLeftControllerStatus);

        labelPimaxStatusRightControllerStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusControllers.add(labelPimaxStatusRightControllerStatus);

        labelPimaxStatusLeftControllerProduct.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusControllers.add(labelPimaxStatusLeftControllerProduct);

        labelPimaxStatusRightControllerProduct.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusControllers.add(labelPimaxStatusRightControllerProduct);

        labelPimaxStatusLeftControllerCharge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusControllers.add(labelPimaxStatusLeftControllerCharge);

        labelPimaxStatusRightControllerCharge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusControllers.add(labelPimaxStatusRightControllerCharge);

        jPanel8.add(panelPimaxStatusControllers, java.awt.BorderLayout.CENTER);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Controllers");
        jPanel8.add(jLabel6, java.awt.BorderLayout.PAGE_START);

        jPanel5.add(jPanel8);

        jPanel15.setLayout(new java.awt.BorderLayout());

        panelPimaxStatusLocators.setLayout(new java.awt.GridLayout(0, 4));
        jPanel15.add(panelPimaxStatusLocators, java.awt.BorderLayout.CENTER);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Base Stations");
        jPanel15.add(jLabel8, java.awt.BorderLayout.PAGE_START);

        jPanel5.add(jPanel15);

        jPanel16.setLayout(new java.awt.BorderLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Trackers");
        jPanel16.add(jLabel10, java.awt.BorderLayout.PAGE_START);

        panelPimaxStatusTrackers.setLayout(new java.awt.GridLayout(0, 4));
        jPanel16.add(panelPimaxStatusTrackers, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel16);

        jPanel17.setLayout(new java.awt.BorderLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Error Info");
        jPanel17.add(jLabel14, java.awt.BorderLayout.PAGE_START);

        panelPimaxStatusErrorInfo.setLayout(new java.awt.GridLayout(0, 4));

        labelPimaxStatusErrorTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusErrorInfo.add(labelPimaxStatusErrorTitle);

        panelPimaxStatusShowString.setLayout(new java.awt.GridLayout(1, 0));

        jLabel97.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel97.setText("Show String");
        panelPimaxStatusShowString.add(jLabel97);

        panelPimaxStatusErrorInfo.add(panelPimaxStatusShowString);

        labelPimaxErrorCode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusErrorInfo.add(labelPimaxErrorCode);

        panelPimaxStatusCanClickTitle.setLayout(new java.awt.GridLayout(1, 0));

        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel99.setText("Can Click Title");
        panelPimaxStatusCanClickTitle.add(jLabel99);

        panelPimaxStatusErrorInfo.add(panelPimaxStatusCanClickTitle);

        labelPimaxStatusErrorType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusErrorInfo.add(labelPimaxStatusErrorType);

        panelPimaxStatusCanClickString.setLayout(new java.awt.GridLayout(1, 0));

        jLabel101.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel101.setText("Can click String");
        panelPimaxStatusCanClickString.add(jLabel101);

        panelPimaxStatusErrorInfo.add(panelPimaxStatusCanClickString);

        labelPimaxStatusErrHmdName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusErrorInfo.add(labelPimaxStatusErrHmdName);

        jPanel17.add(panelPimaxStatusErrorInfo, java.awt.BorderLayout.CENTER);

        labelPimaxStatusErrorString.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel17.add(labelPimaxStatusErrorString, java.awt.BorderLayout.PAGE_END);

        jPanel5.add(jPanel17);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("System Info");
        jPanel9.add(jLabel24, java.awt.BorderLayout.PAGE_START);

        panelPimaxStatusSystemInfo.setLayout(new java.awt.GridLayout(0, 2));

        jLabel103.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel103.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel103.setText("Memory");
        panelPimaxStatusSystemInfo.add(jLabel103);

        jLabel104.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel104.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel104.setText("OS");
        panelPimaxStatusSystemInfo.add(jLabel104);

        labelPimaxStatusMemory.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusSystemInfo.add(labelPimaxStatusMemory);

        labelPimaxStatusOS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusSystemInfo.add(labelPimaxStatusOS);

        jLabel105.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel105.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel105.setText("CPU");
        panelPimaxStatusSystemInfo.add(jLabel105);

        jLabel106.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel106.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel106.setText("GPU");
        panelPimaxStatusSystemInfo.add(jLabel106);

        labelPimaxStatusCPU.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusSystemInfo.add(labelPimaxStatusCPU);

        labelPimaxStatusGPU.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxStatusSystemInfo.add(labelPimaxStatusGPU);

        jPanel9.add(panelPimaxStatusSystemInfo, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel9);

        jPanel19.add(jPanel5);

        jPanel20.setLayout(new javax.swing.BoxLayout(jPanel20, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel10.setLayout(new java.awt.BorderLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Connection Info");
        jPanel10.add(jLabel12, java.awt.BorderLayout.PAGE_START);

        jPanel11.setLayout(new java.awt.GridLayout(0, 3));

        panelPimaxConnectionUSB.setLayout(new java.awt.GridLayout(1, 0));

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("USB Connected");
        panelPimaxConnectionUSB.add(jLabel16);

        jPanel11.add(panelPimaxConnectionUSB);

        panelPimaxConnectionVideo.setLayout(new java.awt.GridLayout(1, 0));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Video Connected");
        panelPimaxConnectionVideo.add(jLabel18);

        jPanel11.add(panelPimaxConnectionVideo);

        panelPimaxConnectionService.setLayout(new java.awt.GridLayout(1, 0));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Service Connected");
        panelPimaxConnectionService.add(jLabel23);

        jPanel11.add(panelPimaxConnectionService);

        labelPimaxConnectionUSB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel11.add(labelPimaxConnectionUSB);

        labelPimaxConnectionVideo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel11.add(labelPimaxConnectionVideo);

        labelPimaxConnectionService.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel11.add(labelPimaxConnectionService);

        jPanel10.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel20.add(jPanel10);

        jPanel13.setLayout(new java.awt.BorderLayout());

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("HMD Info");
        jPanel13.add(jLabel27, java.awt.BorderLayout.NORTH);

        jPanel18.setLayout(new java.awt.GridLayout(0, 3));

        panelPimaxSvc.setLayout(new java.awt.GridLayout(0, 1));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Product Name");
        panelPimaxSvc.add(jLabel36);

        labelPimaxSvcProductName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc.add(labelPimaxSvcProductName);

        jPanel18.add(panelPimaxSvc);

        panelPimaxSvc1.setLayout(new java.awt.GridLayout(0, 1));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Manufacturer");
        panelPimaxSvc1.add(jLabel38);

        labelPimaxSvcManufacturer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc1.add(labelPimaxSvcManufacturer);

        jPanel18.add(panelPimaxSvc1);

        panelPimaxSvc2.setLayout(new java.awt.GridLayout(0, 1));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Serial Number");
        panelPimaxSvc2.add(jLabel39);

        labelPimaxSvcSerialNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc2.add(labelPimaxSvcSerialNumber);

        jPanel18.add(panelPimaxSvc2);

        panelPimaxSvc3.setLayout(new java.awt.GridLayout(0, 1));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Version Generation");
        panelPimaxSvc3.add(jLabel40);

        labelPimaxSvcVersionGen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc3.add(labelPimaxSvcVersionGen);

        jPanel18.add(panelPimaxSvc3);

        panelPimaxSvc4.setLayout(new java.awt.GridLayout(0, 1));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("Major & Minor Version");
        panelPimaxSvc4.add(jLabel41);

        labelPimaxSvcVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc4.add(labelPimaxSvcVersion);

        jPanel18.add(panelPimaxSvc4);

        panelPimaxSvc5.setLayout(new java.awt.GridLayout(0, 1));

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("HmdiceName");
        panelPimaxSvc5.add(jLabel42);

        labelPimaxSvcHmdice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc5.add(labelPimaxSvcHmdice);

        jPanel18.add(panelPimaxSvc5);

        panelPimaxSvc8.setLayout(new java.awt.GridLayout(0, 1));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Resolution");
        panelPimaxSvc8.add(jLabel28);

        labelPimaxSvcResolution.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc8.add(labelPimaxSvcResolution);

        jPanel18.add(panelPimaxSvc8);

        panelPimaxSvc10.setLayout(new java.awt.GridLayout(0, 1));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("Tone");
        panelPimaxSvc10.add(jLabel33);

        labelPimaxSvcToneState.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc10.add(labelPimaxSvcToneState);

        jPanel18.add(panelPimaxSvc10);

        panelPimaxSvc11.setLayout(new java.awt.GridLayout(0, 1));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("Brightness");
        panelPimaxSvc11.add(jLabel34);

        labelPimaxSvcBrightState.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc11.add(labelPimaxSvcBrightState);

        jPanel18.add(panelPimaxSvc11);

        panelPimaxSvc19.setLayout(new java.awt.GridLayout(0, 1));

        panelPimaxSvcBlueLight.setLayout(new java.awt.GridLayout());

        labelPimaxStatusPvrHome7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPimaxStatusPvrHome7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusPvrHome7.setText("Blue Light");
        panelPimaxSvcBlueLight.add(labelPimaxStatusPvrHome7);

        panelPimaxSvc19.add(panelPimaxSvcBlueLight);

        labelPimaxSvcBlueLight.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc19.add(labelPimaxSvcBlueLight);

        jPanel18.add(panelPimaxSvc19);

        panelPimaxSvc20.setLayout(new java.awt.GridLayout(0, 1));

        panelPimaxSvcBacklight.setLayout(new java.awt.GridLayout());

        labelPimaxStatusPvrHome8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPimaxStatusPvrHome8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusPvrHome8.setText("Backlight");
        panelPimaxSvcBacklight.add(labelPimaxStatusPvrHome8);

        panelPimaxSvc20.add(panelPimaxSvcBacklight);

        labelPimaxSvcBacklight.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc20.add(labelPimaxSvcBacklight);

        jPanel18.add(panelPimaxSvc20);

        panelPimaxSvc21.setLayout(new java.awt.GridLayout(0, 1));

        panelPimaxSvcProximity.setLayout(new java.awt.GridLayout());

        labelPimaxStatusPvrHome9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPimaxStatusPvrHome9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusPvrHome9.setText("Proximity");
        panelPimaxSvcProximity.add(labelPimaxStatusPvrHome9);

        panelPimaxSvc21.add(panelPimaxSvcProximity);

        labelPimaxSvcProximity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc21.add(labelPimaxSvcProximity);

        jPanel18.add(panelPimaxSvc21);

        panelPimaxSvc18.setLayout(new java.awt.GridLayout(0, 1));

        panelPimaxSvcGyro.setLayout(new java.awt.GridLayout());

        labelPimaxStatusPvrHome5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPimaxStatusPvrHome5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusPvrHome5.setText("Gyro");
        panelPimaxSvcGyro.add(labelPimaxStatusPvrHome5);

        panelPimaxSvc18.add(panelPimaxSvcGyro);

        labelPimaxSvcGyro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc18.add(labelPimaxSvcGyro);

        jPanel18.add(panelPimaxSvc18);

        panelPimaxSvc12.setLayout(new java.awt.GridLayout(0, 1));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("IPD");
        panelPimaxSvc12.add(jLabel35);

        labelPimaxSvcIPD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc12.add(labelPimaxSvcIPD);

        jPanel18.add(panelPimaxSvc12);

        panelPimaxSvc13.setLayout(new java.awt.GridLayout(0, 1));

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Lens");
        panelPimaxSvc13.add(jLabel43);

        labelPimaxSvcLens.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc13.add(labelPimaxSvcLens);

        jPanel18.add(panelPimaxSvc13);

        panelPimaxSvc14.setLayout(new java.awt.GridLayout(0, 1));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("Display Quality");
        panelPimaxSvc14.add(jLabel44);

        labelPimaxSvcDisplayQuality.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc14.add(labelPimaxSvcDisplayQuality);

        jPanel18.add(panelPimaxSvc14);

        panelPimaxSvc23.setLayout(new java.awt.GridLayout(0, 1));

        panelPimaxSvcDefAudio.setLayout(new java.awt.GridLayout());

        labelPimaxStatusPvrHome10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPimaxStatusPvrHome10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusPvrHome10.setText("Default Audio Config");
        panelPimaxSvcDefAudio.add(labelPimaxStatusPvrHome10);

        panelPimaxSvc23.add(panelPimaxSvcDefAudio);

        labelPimaxSvcDefAudio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc23.add(labelPimaxSvcDefAudio);

        jPanel18.add(panelPimaxSvc23);

        panelPimaxSvc15.setLayout(new java.awt.GridLayout(0, 1));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("Interface Version");
        panelPimaxSvc15.add(jLabel45);

        labelPimaxSvcInterfaceVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc15.add(labelPimaxSvcInterfaceVersion);

        jPanel18.add(panelPimaxSvc15);

        panelPimaxSvc16.setLayout(new java.awt.GridLayout(0, 1));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("Service Version");
        panelPimaxSvc16.add(jLabel46);

        labelPimaxSvcServiceVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc16.add(labelPimaxSvcServiceVersion);

        jPanel18.add(panelPimaxSvc16);

        panelPimaxSvc17.setLayout(new java.awt.GridLayout(0, 1));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("Service Log Level");
        panelPimaxSvc17.add(jLabel47);

        labelPimaxSvcServiceLogLevel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvc17.add(labelPimaxSvcServiceLogLevel);

        jPanel18.add(panelPimaxSvc17);

        jPanel13.add(jPanel18, java.awt.BorderLayout.CENTER);

        panelPimaxSvcPosition1.setLayout(new java.awt.GridLayout(0, 1));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("HMD Position");
        panelPimaxSvcPosition1.add(jLabel37);

        labelPimaxSvcPosition.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelPimaxSvcPosition1.add(labelPimaxSvcPosition);

        jPanel13.add(panelPimaxSvcPosition1, java.awt.BorderLayout.PAGE_END);

        jPanel20.add(jPanel13);

        panelHmdCapabilities.setLayout(new java.awt.BorderLayout());

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("HMD Capabilities");
        panelHmdCapabilities.add(jLabel29, java.awt.BorderLayout.NORTH);

        panelPimaxCapabilities.setLayout(new java.awt.GridLayout(0, 5));

        panelPimaxSvcCapModeCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusHmdConnect1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusHmdConnect1.setText("Mode");
        panelPimaxSvcCapModeCtrl.add(labelPimaxStatusHmdConnect1);

        panelPimaxCapabilities.add(panelPimaxSvcCapModeCtrl);

        panelPimaxSvcCapBackLightCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusSpaceCalibrated1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusSpaceCalibrated1.setText("Backlight");
        panelPimaxSvcCapBackLightCtrl.add(labelPimaxStatusSpaceCalibrated1);

        panelPimaxCapabilities.add(panelPimaxSvcCapBackLightCtrl);

        panelPimaxSvcCapResolutionCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionPlugin1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionPlugin1.setText("Resolution");
        panelPimaxSvcCapResolutionCtrl.add(labelPimaxStatusLeapMotionPlugin1);

        panelPimaxCapabilities.add(panelPimaxSvcCapResolutionCtrl);

        panelPimaxSvcCapToneCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusHeadTracked1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusHeadTracked1.setText("Tone");
        panelPimaxSvcCapToneCtrl.add(labelPimaxStatusHeadTracked1);

        panelPimaxCapabilities.add(panelPimaxSvcCapToneCtrl);

        panelPimaxSvcCapBrightCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionInstalled1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionInstalled1.setText("Brightness");
        panelPimaxSvcCapBrightCtrl.add(labelPimaxStatusLeapMotionInstalled1);

        panelPimaxCapabilities.add(panelPimaxSvcCapBrightCtrl);

        panelPimaxSvcCapIpdAdjust.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionServiceReady1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionServiceReady1.setText("IPD Adjustment");
        panelPimaxSvcCapIpdAdjust.add(labelPimaxStatusLeapMotionServiceReady1);

        panelPimaxCapabilities.add(panelPimaxSvcCapIpdAdjust);

        panelPimaxSvcCapBlueLightCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionDeviceReady1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionDeviceReady1.setText("Blue Light");
        panelPimaxSvcCapBlueLightCtrl.add(labelPimaxStatusLeapMotionDeviceReady1);

        panelPimaxCapabilities.add(panelPimaxSvcCapBlueLightCtrl);

        panelPimaxSvcCapProximityCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusEyeTrackInstalled1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusEyeTrackInstalled1.setText("Proximity");
        panelPimaxSvcCapProximityCtrl.add(labelPimaxStatusEyeTrackInstalled1);

        panelPimaxCapabilities.add(panelPimaxSvcCapProximityCtrl);

        panelPimaxSvcCapGyroControl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusEyeTrackPlugin1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusEyeTrackPlugin1.setText("Gyro");
        panelPimaxSvcCapGyroControl.add(labelPimaxStatusEyeTrackPlugin1);

        panelPimaxCapabilities.add(panelPimaxSvcCapGyroControl);

        panelPimaxSvcCapEyePoseCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusEyeTrackServiceReady1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusEyeTrackServiceReady1.setText("Eye Pose");
        panelPimaxSvcCapEyePoseCtrl.add(labelPimaxStatusEyeTrackServiceReady1);

        panelPimaxCapabilities.add(panelPimaxSvcCapEyePoseCtrl);

        panelPimaxSvcCapAudioCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLighthouseEnabled1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLighthouseEnabled1.setText("Audio");
        panelPimaxSvcCapAudioCtrl.add(labelPimaxStatusLighthouseEnabled1);

        panelPimaxCapabilities.add(panelPimaxSvcCapAudioCtrl);

        panelPimaxSvcCapDisplayQualityCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusPvrHome1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusPvrHome1.setText("Display Quality");
        panelPimaxSvcCapDisplayQualityCtrl.add(labelPimaxStatusPvrHome1);

        panelPimaxCapabilities.add(panelPimaxSvcCapDisplayQualityCtrl);

        panelPimaxSvcCapLensSeparationCtrl.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusScreenProtector1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusScreenProtector1.setText("Lens Separation");
        panelPimaxSvcCapLensSeparationCtrl.add(labelPimaxStatusScreenProtector1);

        panelPimaxCapabilities.add(panelPimaxSvcCapLensSeparationCtrl);

        panelPimaxSvcCapReboot.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusUiConfig1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusUiConfig1.setText("Reboot");
        panelPimaxSvcCapReboot.add(labelPimaxStatusUiConfig1);

        panelPimaxCapabilities.add(panelPimaxSvcCapReboot);

        panelPimaxSvcCapBusinessFlag.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusUiConfig2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusUiConfig2.setText("Business Flag");
        panelPimaxSvcCapBusinessFlag.add(labelPimaxStatusUiConfig2);

        panelPimaxCapabilities.add(panelPimaxSvcCapBusinessFlag);

        panelHmdCapabilities.add(panelPimaxCapabilities, java.awt.BorderLayout.CENTER);

        labelPimaxSvcCapErr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelHmdCapabilities.add(labelPimaxSvcCapErr, java.awt.BorderLayout.SOUTH);

        jPanel20.add(panelHmdCapabilities);

        panelResolutionCapabilities.setLayout(new java.awt.BorderLayout());

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Resolution Capabilities");
        panelResolutionCapabilities.add(jLabel30, java.awt.BorderLayout.NORTH);

        panelPimaxCapabilities1.setLayout(new java.awt.GridLayout(0, 4));

        panelPimaxSvcCapRes1.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusHmdConnect2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusHmdConnect2.setText("1920x1080");
        panelPimaxSvcCapRes1.add(labelPimaxStatusHmdConnect2);

        panelPimaxCapabilities1.add(panelPimaxSvcCapRes1);

        panelPimaxSvcCapRes2.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusSpaceCalibrated2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusSpaceCalibrated2.setText("2560x1440");
        panelPimaxSvcCapRes2.add(labelPimaxStatusSpaceCalibrated2);

        panelPimaxCapabilities1.add(panelPimaxSvcCapRes2);

        panelPimaxSvcCapRes3.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusLeapMotionPlugin2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionPlugin2.setText("2160x1200");
        panelPimaxSvcCapRes3.add(labelPimaxStatusLeapMotionPlugin2);

        panelPimaxCapabilities1.add(panelPimaxSvcCapRes3);

        panelPimaxSvcCapRes4.setLayout(new java.awt.GridLayout(1, 0));

        labelPimaxStatusHeadTracked2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusHeadTracked2.setText("3840x2160");
        panelPimaxSvcCapRes4.add(labelPimaxStatusHeadTracked2);

        panelPimaxCapabilities1.add(panelPimaxSvcCapRes4);

        panelResolutionCapabilities.add(panelPimaxCapabilities1, java.awt.BorderLayout.CENTER);

        labelPimaxSvcCapErr1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelResolutionCapabilities.add(labelPimaxSvcCapErr1, java.awt.BorderLayout.SOUTH);

        jPanel20.add(panelResolutionCapabilities);

        panelResolutionCapabilities1.setLayout(new java.awt.BorderLayout());

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("Mode Capabilities");
        panelResolutionCapabilities1.add(jLabel32, java.awt.BorderLayout.NORTH);

        panelPimaxCapabilities2.setLayout(new java.awt.GridLayout(0, 3));

        panelPimaxSvcCapMode1.setLayout(new java.awt.GridLayout());

        labelPimaxStatusHmdConnect3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusHmdConnect3.setText("Smart");
        panelPimaxSvcCapMode1.add(labelPimaxStatusHmdConnect3);

        panelPimaxCapabilities2.add(panelPimaxSvcCapMode1);

        panelPimaxSvcCapMode2.setLayout(new java.awt.GridLayout());

        labelPimaxStatusSpaceCalibrated3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusSpaceCalibrated3.setText("Video");
        panelPimaxSvcCapMode2.add(labelPimaxStatusSpaceCalibrated3);

        panelPimaxCapabilities2.add(panelPimaxSvcCapMode2);

        panelPimaxSvcCapMode3.setLayout(new java.awt.GridLayout());

        labelPimaxStatusLeapMotionPlugin3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPimaxStatusLeapMotionPlugin3.setText("DFU");
        panelPimaxSvcCapMode3.add(labelPimaxStatusLeapMotionPlugin3);

        panelPimaxCapabilities2.add(panelPimaxSvcCapMode3);

        panelResolutionCapabilities1.add(panelPimaxCapabilities2, java.awt.BorderLayout.CENTER);

        labelPimaxSvcCapErr2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelResolutionCapabilities1.add(labelPimaxSvcCapErr2, java.awt.BorderLayout.SOUTH);

        jPanel20.add(panelResolutionCapabilities1);

        jPanel19.add(jPanel20);

        panelDashboardPimax.add(jPanel19, java.awt.BorderLayout.CENTER);

        panelDashboardModules.add(panelDashboardPimax, java.awt.BorderLayout.CENTER);

        panelDashboardUSB.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("USB Tracker");
        panelDashboardUSB.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        panelTaskHeader1.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel21.setLayout(new java.awt.BorderLayout());

        panelUsbDevices.setLayout(new java.awt.GridLayout(0, 1));
        jPanel21.add(panelUsbDevices, java.awt.BorderLayout.NORTH);

        jScrollPane2.setViewportView(jPanel21);

        panelTaskHeader1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        panelDashboardUSB.add(panelTaskHeader1, java.awt.BorderLayout.CENTER);

        panelDashboardModules.add(panelDashboardUSB, java.awt.BorderLayout.EAST);

        panelDashboard.add(panelDashboardModules, java.awt.BorderLayout.CENTER);

        panelMainContent.add(panelDashboard, "dashboard");

        panelConfigure.setLayout(new java.awt.BorderLayout());

        panelModules.setLayout(new java.awt.BorderLayout());

        labelModules.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        labelModules.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelModules.setText("Modules");
        panelModules.add(labelModules, java.awt.BorderLayout.PAGE_START);

        scrollableModules.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelModulesListContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelModulesListContainer.setLayout(new java.awt.BorderLayout());

        panelModulesList.setLayout(new java.awt.GridLayout(0, 1));
        panelModulesListContainer.add(panelModulesList, java.awt.BorderLayout.PAGE_START);

        scrollableModules.setViewportView(panelModulesListContainer);

        panelModules.add(scrollableModules, java.awt.BorderLayout.CENTER);

        panelConfigure.add(panelModules, java.awt.BorderLayout.CENTER);

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setText("Auto-config");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        panelConfigure.add(jButton1, java.awt.BorderLayout.PAGE_END);

        panelMainContent.add(panelConfigure, "configure");

        panelMain.add(panelMainContent, java.awt.BorderLayout.CENTER);

        panelRoot.add(panelMain, "main");

        panelAutoconfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panelAutoconfig.setLayout(new java.awt.BorderLayout());

        labelAutoconfigTitle.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        labelAutoconfigTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelAutoconfigTitle.setText("Welcome to VR Manager!");
        panelAutoconfig.add(labelAutoconfigTitle, java.awt.BorderLayout.PAGE_START);

        panelAutoconfigButtons.setLayout(new java.awt.GridLayout(1, 0, 50, 50));

        buttonAutoconfigSkip.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        buttonAutoconfigSkip.setText("Manual Configuration");
        buttonAutoconfigSkip.setMargin(new java.awt.Insets(22, 14, 23, 14));
        buttonAutoconfigSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAutoconfigSkipActionPerformed(evt);
            }
        });
        panelAutoconfigButtons.add(buttonAutoconfigSkip);

        buttonAutoconfigConfirm.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        buttonAutoconfigConfirm.setText("Auto-Configure");
        buttonAutoconfigConfirm.setMargin(new java.awt.Insets(22, 14, 23, 14));
        buttonAutoconfigConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAutoconfigConfirmActionPerformed(evt);
            }
        });
        panelAutoconfigButtons.add(buttonAutoconfigConfirm);

        panelAutoconfig.add(panelAutoconfigButtons, java.awt.BorderLayout.PAGE_END);

        scrollableAutoconfig.setBorder(null);

        labelAutoconfig.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        labelAutoconfig.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelAutoconfig.setText("""
            <html>
            <div style="text-align: center; text-height: 240px">
            Would you like to automatically configure VR Manager?
            <br>
            <br>
            This will attempt to auto-detect installed software and configure the reccommended settings for them in VR Manager.
            <br>
            <br>
            Standalone software, such as simple OSC programs, must be located in the same folder as VR Manager, or in a subfolder.
            </div>
            </html>
            """);
            scrollableAutoconfig.setViewportView(labelAutoconfig);

            panelAutoconfig.add(scrollableAutoconfig, java.awt.BorderLayout.CENTER);

            panelRoot.add(panelAutoconfig, "autoconfig");

            getContentPane().add(panelRoot, java.awt.BorderLayout.CENTER);

            pack();
        }// </editor-fold>//GEN-END:initComponents
    private void buttonAutoconfigConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAutoconfigConfirmActionPerformed
        if(generatedConfig==null){
            labelAutoconfig.setHorizontalAlignment(SwingConstants.LEFT);
            labelAutoconfig.setVerticalAlignment(SwingConstants.TOP);
            buttonAutoconfigSkip.setEnabled(false);
            buttonAutoconfigConfirm.setEnabled(false);
            labelAutoconfig.setText("<html>");
            generatedConfig = VRManager.autoConfig((str) -> labelAutoconfig.setText(labelAutoconfig.getText()+"\n<br>"+str));
            buttonAutoconfigConfirm.setText("Use Configuration");
            buttonAutoconfigSkip.setText("Discard Configuration");
            buttonAutoconfigSkip.setEnabled(true);
            buttonAutoconfigConfirm.setEnabled(true);
        }else{
            labelAutoconfig.setHorizontalAlignment(SwingConstants.CENTER);
            labelAutoconfig.setVerticalAlignment(SwingConstants.CENTER);
            VRManager.configuration = generatedConfig;
            VRManager.saveConfig();
            generatedConfig = null;
            ((CardLayout)panelRoot.getLayout()).show(panelRoot, "main");
            ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "configure");
            tabButtonConfigure.setSelected(true);
            refreshModules();
        }
    }//GEN-LAST:event_buttonAutoconfigConfirmActionPerformed
    private void buttonAutoconfigSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAutoconfigSkipActionPerformed
        labelAutoconfig.setHorizontalAlignment(SwingConstants.CENTER);
        labelAutoconfig.setVerticalAlignment(SwingConstants.CENTER);
        generatedConfig = null;
        ((CardLayout)panelRoot.getLayout()).show(panelRoot, "main");
        ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "configure");
        tabButtonConfigure.setSelected(true);
    }//GEN-LAST:event_buttonAutoconfigSkipActionPerformed
    private void tabButtonMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabButtonMainActionPerformed
        ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "dashboard");
    }//GEN-LAST:event_tabButtonMainActionPerformed
    private void tabButtonConfigureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabButtonConfigureActionPerformed
        ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "configure");
    }//GEN-LAST:event_tabButtonConfigureActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Automatically generate configuration?\nWARNING: current settings will be lost!", "Autoconfig", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null)!=JOptionPane.YES_OPTION)return;
        VRManager.configuration = VRManager.autoConfig(null);
        VRManager.saveConfig();
        refreshModules();
    }//GEN-LAST:event_jButton1ActionPerformed
    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        if(VRManager.hasActiveTasks()){
            ArrayList<String> taskNames = new ArrayList<>();
            for(int i = 0; i<VRManager.tasks.size(); i++){
                var task = VRManager.tasks.get(i);
                if(task.isActive())taskNames.add(task.name);
            }
            if(JOptionPane.showConfirmDialog(this, "Tasks are already running:\n"+String.join(", ", taskNames)+"\nAre you sure you want to start them again?", "Tasks already running", JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        }
        buttonStart.setEnabled(false);
        Thread initThread = new Thread(() -> {
            VRManager.init();
            buttonStart.setEnabled(true);
        }, "Initialization Thread");
        initThread.start();
    }//GEN-LAST:event_buttonStartActionPerformed
    private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Stop all tasks? (This will exit VR)", "Stop Tasks", JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        buttonStop.setEnabled(false);
        Thread stopThread = new Thread(() -> {
            for(var task : VRManager.tasks){
                if(task.isActive())task.shutdown();
            }
            buttonStop.setEnabled(true);
        }, "Stop Tasks");
        stopThread.setDaemon(true);
        stopThread.start();
    }//GEN-LAST:event_buttonStopActionPerformed
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Thread exitThread = new Thread(() -> {
            if(!VRManager.shutdown(false, false, false, false)){
                switch(JOptionPane.showConfirmDialog(this, "Tasks are still running!\nDo you want to stop all tasks before closing? (This will exit VR)", "Tasks still running", JOptionPane.YES_NO_CANCEL_OPTION)){
                    case JOptionPane.YES_OPTION -> {
                        if(VRManager.shutdown(true, false, true, false)){
                            dispose();
                            System.exit(0);
                        }else{
                            ArrayList<String> taskNames = new ArrayList<>();
                            for(int i = 0; i<VRManager.tasks.size(); i++){
                                var task = VRManager.tasks.get(i);
                                if(task.isActive())taskNames.add(task.name);
                            }
                            if(JOptionPane.showConfirmDialog(this, "Some tasks haven't shut down after multiple attempts:\n"+String.join(", ", taskNames)+"\nClose anyway?", "Tasks still running", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                dispose();
                                System.exit(0);
                            }
                        }
                    }
                    case JOptionPane.NO_OPTION -> {
                        dispose();
                        System.exit(0);
                    }
                }
            }else{
                dispose();
                System.exit(0);
            }
        }, "Exit Thread");
        exitThread.setDaemon(true);
        exitThread.start();
    }//GEN-LAST:event_formWindowClosing
    private void buttonPimaxSetupGuideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxSetupGuideActionPerformed
        PiRpc.Click_GuiStart();
    }//GEN-LAST:event_buttonPimaxSetupGuideActionPerformed
    private void buttonPimaxStartSteamVRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxStartSteamVRActionPerformed
        PiRpc.Click_SteamVR();
    }//GEN-LAST:event_buttonPimaxStartSteamVRActionPerformed
    private void buttonPimaxRoomSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxRoomSetupActionPerformed
        PiRpc.Click_roomSetting();
    }//GEN-LAST:event_buttonPimaxRoomSetupActionPerformed
    private void buttonPimaxPairDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxPairDeviceActionPerformed
        PiRpc.Click_PairControllerChoose();
    }//GEN-LAST:event_buttonPimaxPairDeviceActionPerformed
    private void buttonPimaxRestartHeadsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxRestartHeadsetActionPerformed
        PiRpc.Event_rebootHmdAuto();
    }//GEN-LAST:event_buttonPimaxRestartHeadsetActionPerformed
    private void buttonPimaxRestartServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxRestartServiceActionPerformed
        PiRpc.Event_restartServiceAuto();
    }//GEN-LAST:event_buttonPimaxRestartServiceActionPerformed
    private void buttonPimaxDeviceSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxDeviceSettingsActionPerformed
        PiRpc.Click_ShowPitool();
    }//GEN-LAST:event_buttonPimaxDeviceSettingsActionPerformed
    private void buttonPimaxShutDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPimaxShutDownActionPerformed
        PiRpc.Event_Click_HMDShutdown();
    }//GEN-LAST:event_buttonPimaxShutDownActionPerformed
    public static void start(){
        String[] preferredLookAndFeels = new String[]{"Windows", "Nimbus"};
        String[] classNames = new String[preferredLookAndFeels.length];
        for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
            for(int i = 0; i<preferredLookAndFeels.length; i++){
                if(preferredLookAndFeels[i].equals(info.getName())){
                    classNames[i] = info.getClassName();
                }
            }
        }
        for(int i = 0; i<classNames.length; i++){
            if(classNames[i]!=null){
                try{
                    javax.swing.UIManager.setLookAndFeel(classNames[i]);
                    Logger.info("Loaded look and feel: "+preferredLookAndFeels[i]);
                }catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex){
                    Logger.warn("Failed to load look and feel: "+preferredLookAndFeels[i], ex);
                }
                break;
            }
        }
        java.awt.EventQueue.invokeLater(() -> {
            instance = new ManagerGUI();
            instance.setVisible(true);
        });
    }
    public static void stop(){
        if(instance!=null)instance.dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAutoconfigConfirm;
    private javax.swing.JButton buttonAutoconfigSkip;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton buttonPimaxDeviceSettings;
    private javax.swing.JButton buttonPimaxPairDevice;
    private javax.swing.JButton buttonPimaxRestartHeadset;
    private javax.swing.JButton buttonPimaxRestartService;
    private javax.swing.JButton buttonPimaxRoomSetup;
    private javax.swing.JButton buttonPimaxSetupGuide;
    private javax.swing.JButton buttonPimaxShutDown;
    private javax.swing.JButton buttonPimaxStartSteamVR;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelAutoconfig;
    private javax.swing.JLabel labelAutoconfigTitle;
    private javax.swing.JLabel labelModules;
    private javax.swing.JLabel labelPimaxConnectionService;
    private javax.swing.JLabel labelPimaxConnectionUSB;
    private javax.swing.JLabel labelPimaxConnectionVideo;
    private javax.swing.JLabel labelPimaxErrorCode;
    private javax.swing.JLabel labelPimaxStatusCPU;
    private javax.swing.JLabel labelPimaxStatusCurRefresh;
    private javax.swing.JLabel labelPimaxStatusDeviceID;
    private javax.swing.JLabel labelPimaxStatusErrHmdName;
    private javax.swing.JLabel labelPimaxStatusErrorCode;
    private javax.swing.JLabel labelPimaxStatusErrorString;
    private javax.swing.JLabel labelPimaxStatusErrorTitle;
    private javax.swing.JLabel labelPimaxStatusErrorType;
    private javax.swing.JLabel labelPimaxStatusEyeTrackInstalled;
    private javax.swing.JLabel labelPimaxStatusEyeTrackInstalled1;
    private javax.swing.JLabel labelPimaxStatusEyeTrackPlugin;
    private javax.swing.JLabel labelPimaxStatusEyeTrackPlugin1;
    private javax.swing.JLabel labelPimaxStatusEyeTrackServiceReady;
    private javax.swing.JLabel labelPimaxStatusEyeTrackServiceReady1;
    private javax.swing.JLabel labelPimaxStatusFirmwareVersion;
    private javax.swing.JLabel labelPimaxStatusGPU;
    private javax.swing.JLabel labelPimaxStatusHMDCharge;
    private javax.swing.JLabel labelPimaxStatusHardwareSerialNumber;
    private javax.swing.JLabel labelPimaxStatusHeadTracked;
    private javax.swing.JLabel labelPimaxStatusHeadTracked1;
    private javax.swing.JLabel labelPimaxStatusHeadTracked2;
    private javax.swing.JLabel labelPimaxStatusHmdConnect;
    private javax.swing.JLabel labelPimaxStatusHmdConnect1;
    private javax.swing.JLabel labelPimaxStatusHmdConnect2;
    private javax.swing.JLabel labelPimaxStatusHmdConnect3;
    private javax.swing.JLabel labelPimaxStatusHmdDisplayName;
    private javax.swing.JLabel labelPimaxStatusHmdMode;
    private javax.swing.JLabel labelPimaxStatusHmdName;
    private javax.swing.JLabel labelPimaxStatusLeapMotionDeviceReady;
    private javax.swing.JLabel labelPimaxStatusLeapMotionDeviceReady1;
    private javax.swing.JLabel labelPimaxStatusLeapMotionInstalled;
    private javax.swing.JLabel labelPimaxStatusLeapMotionInstalled1;
    private javax.swing.JLabel labelPimaxStatusLeapMotionPlugin;
    private javax.swing.JLabel labelPimaxStatusLeapMotionPlugin1;
    private javax.swing.JLabel labelPimaxStatusLeapMotionPlugin2;
    private javax.swing.JLabel labelPimaxStatusLeapMotionPlugin3;
    private javax.swing.JLabel labelPimaxStatusLeapMotionServiceReady;
    private javax.swing.JLabel labelPimaxStatusLeapMotionServiceReady1;
    private javax.swing.JLabel labelPimaxStatusLeftControllerCharge;
    private javax.swing.JLabel labelPimaxStatusLeftControllerProduct;
    private javax.swing.JLabel labelPimaxStatusLeftControllerStatus;
    private javax.swing.JLabel labelPimaxStatusLighthouseEnabled;
    private javax.swing.JLabel labelPimaxStatusLighthouseEnabled1;
    private javax.swing.JLabel labelPimaxStatusMemory;
    private javax.swing.JLabel labelPimaxStatusOS;
    private javax.swing.JLabel labelPimaxStatusPvrHome;
    private javax.swing.JLabel labelPimaxStatusPvrHome1;
    private javax.swing.JLabel labelPimaxStatusPvrHome10;
    private javax.swing.JLabel labelPimaxStatusPvrHome5;
    private javax.swing.JLabel labelPimaxStatusPvrHome7;
    private javax.swing.JLabel labelPimaxStatusPvrHome8;
    private javax.swing.JLabel labelPimaxStatusPvrHome9;
    private javax.swing.JLabel labelPimaxStatusRightControllerCharge;
    private javax.swing.JLabel labelPimaxStatusRightControllerProduct;
    private javax.swing.JLabel labelPimaxStatusRightControllerStatus;
    private javax.swing.JLabel labelPimaxStatusScreenProtector;
    private javax.swing.JLabel labelPimaxStatusScreenProtector1;
    private javax.swing.JLabel labelPimaxStatusSerialNumber;
    private javax.swing.JLabel labelPimaxStatusSpaceCalibrated;
    private javax.swing.JLabel labelPimaxStatusSpaceCalibrated1;
    private javax.swing.JLabel labelPimaxStatusSpaceCalibrated2;
    private javax.swing.JLabel labelPimaxStatusSpaceCalibrated3;
    private javax.swing.JLabel labelPimaxStatusTrialInfo;
    private javax.swing.JLabel labelPimaxStatusUiConfig;
    private javax.swing.JLabel labelPimaxStatusUiConfig1;
    private javax.swing.JLabel labelPimaxStatusUiConfig2;
    private javax.swing.JLabel labelPimaxStatusUserInfo;
    private javax.swing.JLabel labelPimaxSvcBacklight;
    private javax.swing.JLabel labelPimaxSvcBlueLight;
    private javax.swing.JLabel labelPimaxSvcBrightState;
    private javax.swing.JLabel labelPimaxSvcCapErr;
    private javax.swing.JLabel labelPimaxSvcCapErr1;
    private javax.swing.JLabel labelPimaxSvcCapErr2;
    private javax.swing.JLabel labelPimaxSvcDefAudio;
    private javax.swing.JLabel labelPimaxSvcDisplayQuality;
    private javax.swing.JLabel labelPimaxSvcGyro;
    private javax.swing.JLabel labelPimaxSvcHmdice;
    private javax.swing.JLabel labelPimaxSvcIPD;
    private javax.swing.JLabel labelPimaxSvcInterfaceVersion;
    private javax.swing.JLabel labelPimaxSvcLens;
    private javax.swing.JLabel labelPimaxSvcManufacturer;
    private javax.swing.JLabel labelPimaxSvcPosition;
    private javax.swing.JLabel labelPimaxSvcProductName;
    private javax.swing.JLabel labelPimaxSvcProximity;
    private javax.swing.JLabel labelPimaxSvcResolution;
    private javax.swing.JLabel labelPimaxSvcSerialNumber;
    private javax.swing.JLabel labelPimaxSvcServiceLogLevel;
    private javax.swing.JLabel labelPimaxSvcServiceVersion;
    private javax.swing.JLabel labelPimaxSvcToneState;
    private javax.swing.JLabel labelPimaxSvcVersion;
    private javax.swing.JLabel labelPimaxSvcVersionGen;
    private javax.swing.JLabel labelTasks;
    private javax.swing.JPanel panelAutoconfig;
    private javax.swing.JPanel panelAutoconfigButtons;
    private javax.swing.JPanel panelConfigure;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelDashboardModules;
    private javax.swing.JPanel panelDashboardPimax;
    private javax.swing.JPanel panelDashboardUSB;
    private javax.swing.JPanel panelHmdCapabilities;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelMainContent;
    private javax.swing.JPanel panelMainTabs;
    private javax.swing.JPanel panelModules;
    private javax.swing.JPanel panelModulesList;
    private javax.swing.JPanel panelModulesListContainer;
    private javax.swing.JPanel panelPimaxCapabilities;
    private javax.swing.JPanel panelPimaxCapabilities1;
    private javax.swing.JPanel panelPimaxCapabilities2;
    private javax.swing.JPanel panelPimaxConnectionService;
    private javax.swing.JPanel panelPimaxConnectionUSB;
    private javax.swing.JPanel panelPimaxConnectionVideo;
    private javax.swing.JPanel panelPimaxStatusCanClickString;
    private javax.swing.JPanel panelPimaxStatusCanClickTitle;
    private javax.swing.JPanel panelPimaxStatusControllers;
    private javax.swing.JPanel panelPimaxStatusCurRefresh;
    private javax.swing.JPanel panelPimaxStatusDisplayHmdName;
    private javax.swing.JPanel panelPimaxStatusErrorInfo;
    private javax.swing.JPanel panelPimaxStatusEyeTrackInstalled;
    private javax.swing.JPanel panelPimaxStatusEyeTrackPlugin;
    private javax.swing.JPanel panelPimaxStatusEyeTrackServiceReady;
    private javax.swing.JPanel panelPimaxStatusFirmwareVersion;
    private javax.swing.JPanel panelPimaxStatusHMDCharging;
    private javax.swing.JPanel panelPimaxStatusHeadTracked;
    private javax.swing.JPanel panelPimaxStatusHmdConnect;
    private javax.swing.JPanel panelPimaxStatusHmdErrorCode;
    private javax.swing.JPanel panelPimaxStatusHmdModeType;
    private javax.swing.JPanel panelPimaxStatusHmdName;
    private javax.swing.JPanel panelPimaxStatusHmdP3BCharge;
    private javax.swing.JPanel panelPimaxStatusLeapMotionDeviceReady;
    private javax.swing.JPanel panelPimaxStatusLeapMotionInstalled;
    private javax.swing.JPanel panelPimaxStatusLeapMotionPlugin;
    private javax.swing.JPanel panelPimaxStatusLeapMotionServiceReady;
    private javax.swing.JPanel panelPimaxStatusLeftController;
    private javax.swing.JPanel panelPimaxStatusLeftControllerTracked;
    private javax.swing.JPanel panelPimaxStatusLighthouseEnabled;
    private javax.swing.JPanel panelPimaxStatusLights;
    private javax.swing.JPanel panelPimaxStatusLocators;
    private javax.swing.JPanel panelPimaxStatusPvrHome;
    private javax.swing.JPanel panelPimaxStatusRightController;
    private javax.swing.JPanel panelPimaxStatusRightControllerTracked;
    private javax.swing.JPanel panelPimaxStatusSNCodeLock;
    private javax.swing.JPanel panelPimaxStatusScreenProtector;
    private javax.swing.JPanel panelPimaxStatusSerialNumber;
    private javax.swing.JPanel panelPimaxStatusShowString;
    private javax.swing.JPanel panelPimaxStatusSpaceCalibrated;
    private javax.swing.JPanel panelPimaxStatusSystemInfo;
    private javax.swing.JPanel panelPimaxStatusTrackers;
    private javax.swing.JPanel panelPimaxStatusTrialInfo;
    private javax.swing.JPanel panelPimaxStatusUiConfig;
    private javax.swing.JPanel panelPimaxStatusUserInfo;
    private javax.swing.JPanel panelPimaxSvc;
    private javax.swing.JPanel panelPimaxSvc1;
    private javax.swing.JPanel panelPimaxSvc10;
    private javax.swing.JPanel panelPimaxSvc11;
    private javax.swing.JPanel panelPimaxSvc12;
    private javax.swing.JPanel panelPimaxSvc13;
    private javax.swing.JPanel panelPimaxSvc14;
    private javax.swing.JPanel panelPimaxSvc15;
    private javax.swing.JPanel panelPimaxSvc16;
    private javax.swing.JPanel panelPimaxSvc17;
    private javax.swing.JPanel panelPimaxSvc18;
    private javax.swing.JPanel panelPimaxSvc19;
    private javax.swing.JPanel panelPimaxSvc2;
    private javax.swing.JPanel panelPimaxSvc20;
    private javax.swing.JPanel panelPimaxSvc21;
    private javax.swing.JPanel panelPimaxSvc23;
    private javax.swing.JPanel panelPimaxSvc3;
    private javax.swing.JPanel panelPimaxSvc4;
    private javax.swing.JPanel panelPimaxSvc5;
    private javax.swing.JPanel panelPimaxSvc8;
    private javax.swing.JPanel panelPimaxSvcBacklight;
    private javax.swing.JPanel panelPimaxSvcBlueLight;
    private javax.swing.JPanel panelPimaxSvcCapAudioCtrl;
    private javax.swing.JPanel panelPimaxSvcCapBackLightCtrl;
    private javax.swing.JPanel panelPimaxSvcCapBlueLightCtrl;
    private javax.swing.JPanel panelPimaxSvcCapBrightCtrl;
    private javax.swing.JPanel panelPimaxSvcCapBusinessFlag;
    private javax.swing.JPanel panelPimaxSvcCapDisplayQualityCtrl;
    private javax.swing.JPanel panelPimaxSvcCapEyePoseCtrl;
    private javax.swing.JPanel panelPimaxSvcCapGyroControl;
    private javax.swing.JPanel panelPimaxSvcCapIpdAdjust;
    private javax.swing.JPanel panelPimaxSvcCapLensSeparationCtrl;
    private javax.swing.JPanel panelPimaxSvcCapMode1;
    private javax.swing.JPanel panelPimaxSvcCapMode2;
    private javax.swing.JPanel panelPimaxSvcCapMode3;
    private javax.swing.JPanel panelPimaxSvcCapModeCtrl;
    private javax.swing.JPanel panelPimaxSvcCapProximityCtrl;
    private javax.swing.JPanel panelPimaxSvcCapReboot;
    private javax.swing.JPanel panelPimaxSvcCapRes1;
    private javax.swing.JPanel panelPimaxSvcCapRes2;
    private javax.swing.JPanel panelPimaxSvcCapRes3;
    private javax.swing.JPanel panelPimaxSvcCapRes4;
    private javax.swing.JPanel panelPimaxSvcCapResolutionCtrl;
    private javax.swing.JPanel panelPimaxSvcCapToneCtrl;
    private javax.swing.JPanel panelPimaxSvcDefAudio;
    private javax.swing.JPanel panelPimaxSvcGyro;
    private javax.swing.JPanel panelPimaxSvcPosition1;
    private javax.swing.JPanel panelPimaxSvcProximity;
    private javax.swing.JPanel panelResolutionCapabilities;
    private javax.swing.JPanel panelResolutionCapabilities1;
    private javax.swing.JPanel panelRoot;
    private javax.swing.JPanel panelTaskHeader;
    private javax.swing.JPanel panelTaskHeader1;
    private javax.swing.JPanel panelTasks;
    private javax.swing.JPanel panelUsbDevices;
    private javax.swing.JScrollPane scrollableAutoconfig;
    private javax.swing.JScrollPane scrollableModules;
    private javax.swing.JToggleButton tabButtonConfigure;
    private javax.swing.JToggleButton tabButtonMain;
    // End of variables declaration//GEN-END:variables
}
