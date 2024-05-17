package com.thizthizzydizzy.vrmanager.special.piSvc.piSvcType;
public class piSvcResult{
    public static final int svc_success = 0;
    public static final int svc_failed = -1;
    public static final int svc_not_initialize = 100;
    public static final int svc_not_ready = 101;
    public static final int svc_usb_no_connect = 102;
    public static final int svc_hdmi_no_connect = 103;
    public static final int svc_handle_controller_no_connect = 104;
    public static final int svc_hid_failed = 105;
    public static final int svc_driver_failed = 106;// maybe i2c suspend
    public static final int svc_firmware_failed = 107;
    public static final int svc_path_not_found = 108;
    public static final int svc_state_not_connected = 109;
    public static final int svc_capability_no_support = 110;
    public static final int svc_param_error = 111;
    public static final int svc_timeout = 112;
    public static final int svc_dll_failed = 113;
    public static final int svc_rpc_failed = 114;
    public static final int svc_mode_no_support = 115;
    public static final int svc_interface_no_support = 116;
    public static final int svc_no_completed = 117;
    public static final int svc_no_sp1 = 118;
    public static final int svc_gpu_version_blacklist = 119;

    public static final int svc_service_failed = 400;
    public static final int svc_service_no_start = 401;
    public static final int svc_service_display_lost = 402;
    public static final int svc_service_init_failed = 403;
    public static final int svc_service_create_session_failed = 404;
    public static final int svc_service_should_quit = 405;

//    public static final int svc_unknow;
}
