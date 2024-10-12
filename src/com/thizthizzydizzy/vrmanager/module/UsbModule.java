package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.command.CommandUtil;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.gui.module.ConfigureUsbGUI;
import com.thizthizzydizzy.vrmanager.special.usb.Usb;
import javax.swing.JDialog;
import javax.swing.JFrame;
public class UsbModule extends VRModule{
    @Override
    public String getName(){
        return "usb";
    }
    @Override
    public NamedCommand[] getCommands(){
        return new NamedCommand[]{
            new NamedCommand("start", (base, args) -> {
                if(Usb.isActive()){
                    Logger.info("USB Watcher is already active!");
                    return;
                }
                if(!CommandUtil.noArguments(base, args))return;
                Usb.start();
            }),
            new NamedCommand("stop", (base, args) -> {
                if(!Usb.isActive()){
                    Logger.info("USB Watcher is not active!");
                    return;
                }
                if(!CommandUtil.noArguments(base, args))return;
                Usb.stop();
            }),
            new NamedCommand("watch", (base, args) -> {
                if((args.length<1||args.length>2)&&!CommandUtil.nArguments(base, args, 2, "<idVendor> [<idProduct>]"))return;
                int vendorID = -1;
                try{
                    vendorID = Integer.parseInt(args[0], 16);
                }catch(NumberFormatException ex){
                    Logger.info("Invalid vendor ID: "+args[0]+"!");
                    return;
                }
                int productID = -1;
                try{
                    if(args.length>1)productID = Integer.parseInt(args[1], 16);
                }catch(NumberFormatException ex){
                    Logger.info("Invalid product ID: "+args[1]+"!");
                    return;
                }
                Usb.watch(vendorID, productID);
            }),
            new NamedCommand("list", (base, args) -> {
                if(!CommandUtil.noArguments(base, args))return;
                for(var device : Usb.allDevices){
                    boolean connected = Usb.devices.contains(device);
                    var desc = device.getUsbDeviceDescriptor();
                    Logger.info(Integer.toHexString(desc.idVendor())+" "+Integer.toHexString(desc.idProduct())+" "+(connected?"(Connected)":"(Disconnected)"));
                }
            })
        };
    }
    @Override
    public void init(){
        Usb.start();
        for(var device : VRManager.configuration.usb.devices)Usb.watch(device.vendor, device.product);
        for(var vendor : VRManager.configuration.usb.vendors)Usb.watch(vendor);
        if(VRManager.configuration.usb.watchAllDevices)Usb.watch(-1);
    }
    @Override
    public boolean hasConfiguration(){
        return true;
    }
    @Override
    public JDialog getConfigurationGUI(JFrame parent){
        return new ConfigureUsbGUI(parent);
    }
}
