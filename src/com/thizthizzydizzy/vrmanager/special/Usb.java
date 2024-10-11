package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.task.Task;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;
public class Usb{
    private static Task watcherTask;
    public static HashSet<UsbDevice> allDevices = new HashSet<>();
    public static ArrayList<UsbDevice> devices = new ArrayList<>();
    public static ArrayList<WatchInfo> watching = new ArrayList<>();
    public static void start(){
        VRManager.startTask(watcherTask = new Task("USB Watcher"){
            private Thread watcherThread;
            @Override
            public void start(){
                watcherThread = new Thread(() -> {
                    allDevices.clear();
                    devices.clear();
                    Logger.push("USB");
                    while(!Thread.interrupted()){
                        try{
                            UsbServices services = UsbHostManager.getUsbServices();
                            UsbHub root = services.getRootUsbHub();
                            ArrayList<UsbDevice> more = getDevices(root);
                            more.removeAll(devices);
                            for(UsbDevice device : more){
                                device.addUsbDeviceListener(new UsbDeviceListener(){
                                    @Override
                                    public void usbDeviceDetached(UsbDeviceEvent ude){
                                        Logger.push("USB");
                                        onDeviceDetached(ude);
                                        Logger.pop();
                                    }
                                    @Override
                                    public void errorEventOccurred(UsbDeviceErrorEvent udee){
                                        Logger.push("USB");
                                        onDeviceError(udee);
                                        Logger.pop();
                                    }
                                    @Override
                                    public void dataEventOccurred(UsbDeviceDataEvent udde){
                                        Logger.push("USB");
                                        onDeviceData(udde);
                                        Logger.pop();
                                    }
                                });
                                onDeviceAttached(device);
                            }
                            devices.addAll(more);
                            allDevices.addAll(more);
                            Thread.sleep(1000);
                        }catch(UsbException|SecurityException ex){
                            Logger.error(ex);
                        }catch(InterruptedException ex){
                            break;
                        }
                    }
                    Logger.pop();
                });
                watcherThread.start();
            }
            @Override
            public boolean isActive(){
                return watcherThread.isAlive();
            }
            @Override
            public void shutdown(){
                watcherThread.interrupt();
            }
        });
    }
    public static boolean isActive(){
        return watcherTask!=null;
    }
    public static void stop(){
        if(watcherTask!=null)watcherTask.shutdown();
        watcherTask = null;
    }
    private static ArrayList<UsbDevice> getDevices(UsbHub hub){
        ArrayList<UsbDevice> devices = new ArrayList<>();
        devices.add(hub);
        for(UsbDevice device : (List<UsbDevice>)hub.getAttachedUsbDevices()){
            if(device.isUsbHub())devices.addAll(getDevices((UsbHub)device));
            else
                devices.add(device);
        }
        return devices;
    }
    private static void onDeviceDetached(UsbDeviceEvent event){
        var device = event.getUsbDevice();
        var watch = isWatching(device);
        if(watch==null)return;
        var desc = device.getUsbDeviceDescriptor();
        Logger.info("Device Detached: "+getDeviceDescription(device));
    }
    private static void onDeviceError(UsbDeviceErrorEvent event){
        var device = event.getUsbDevice();
        var watch = isWatching(device);
        if(watch==null)return;
        var desc = device.getUsbDeviceDescriptor();
        Logger.error("Device Error: "+getDeviceDescription(device), event.getUsbException());
    }
    private static void onDeviceData(UsbDeviceDataEvent event){
        var device = event.getUsbDevice();
        var watch = isWatching(device);
        if(watch==null)return;
        var desc = device.getUsbDeviceDescriptor();
        Logger.info("Device Data: "+getDeviceDescription(device));
    }
    private static void onDeviceAttached(UsbDevice device){
        var watch = isWatching(device);
        if(watch==null)return;
        var desc = device.getUsbDeviceDescriptor();
        Logger.info("Device Attached: "+getDeviceDescription(device));
    }
    public static void watch(int vendorID, int productID){
        for(var watch : watching){
            if(watch.vendorID==vendorID&&watch.productID==productID)return;
        }
        Logger.push(Usb.class);
        watching.add(new WatchInfo(vendorID, productID));
        Logger.info("Started watching devices (Vendor "+Integer.toHexString(vendorID)+(productID>=0?", Product "+Integer.toHexString(productID):"")+")");
        Logger.pop();
    }
    public static void watch(int vendorID){
        watch(vendorID, -1);
    }
    private static WatchInfo isWatching(UsbDevice device){
        var desc = device.getUsbDeviceDescriptor();
        for(var watch : watching){
            if(watch.vendorID==desc.idVendor()&&(watch.productID==-1||watch.productID==desc.idProduct()))return watch;
        }
        return null;
    }
    private static String getDeviceDescription(UsbDevice device){
        var desc = device.getUsbDeviceDescriptor();
        try{
            return device.getManufacturerString()+" "+device.getProductString();//doesn't work, not sure why "Insufficient permissions"
        }catch(UsbException|UnsupportedEncodingException|UsbDisconnectedException ex){
        }
        return Integer.toString(desc.idVendor(), 16)+" "+Integer.toString(desc.idProduct(), 16);
    }
    private static class WatchInfo{
        private final int vendorID;
        private final int productID;
        public WatchInfo(int vendorID, int productID){
            this.vendorID = vendorID;
            this.productID = productID;
        }
    }
}
