package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.command.CommandUtil;
import com.thizthizzydizzy.vrmanager.gui.module.ConfigurePimaxGUI;
import com.thizthizzydizzy.vrmanager.special.pimax.Pimax;
import com.thizthizzydizzy.vrmanager.special.pimax.PiRpc;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.PiRpcAPI;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JFrame;
public class PimaxModule extends VRModule{
    @Override
    public String getName(){
        return "pimax";
    }
    @Override
    public NamedCommand[] getCommands(){
        return CommandUtil.subcommands(
            new NamedCommand("pisvc", CommandUtil.subcommand(null,
                new NamedCommand("scanlog", (base, args) -> {
                    if(!CommandUtil.noArguments(base, args))return;
                    PiSvc.scanLog();
                }),
                new NamedCommand("start", (base, args) -> {
                    if(PiSvc.active){
                        Logger.info("PiSvc Manager is already active!");
                        return;
                    }
                    if(!CommandUtil.noArguments(base, args))return;
                    PiSvc.start();
                }),
                new NamedCommand("stop", (base, args) -> {
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    if(!CommandUtil.noArguments(base, args))return;
                    PiSvc.stop();
                }),
                new NamedCommand("get", (base, args) -> {
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    if(args.length==0){
                        String text = "Please provide a variable to GET. Known variables:";
                        for(var key : PiSvc.knownConfigKeys){
                            text+="\n"+key.toString();
                        }
                        Logger.info(text);
                        return;
                    }
                    if(!CommandUtil.nArguments(base, args, 1))return;
                    Logger.info("String: "+PiSvc.svc_getStringConfig(args[0], 1024));
                    Logger.info("Device String: "+PiSvc.svc_getStringDeviceConfig(args[0], 1024));
                    Logger.info("Int: "+PiSvc.svc_getIntConfig(args[0]));
                    Logger.info("Float: "+PiSvc.svc_getFloatConfig(args[0]));
                    var vec = PiSvc.svc_getVector3fConfig(args[0]);
                    Logger.info("Vector3: ["+vec.x+", "+vec.y+", "+vec.z+"]");
                }),
                new NamedCommand("set", (base, args) -> {
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    if(args.length==0){
                        String text = "Please provide a variable to SET. Known variables:";
                        for(var key : PiSvc.knownConfigKeys){
                            if(key.writable)text+="\n"+key.toString();
                        }
                        Logger.info(text);
                        return;
                    }
                    if(!CommandUtil.nArguments(base, args, 2))return;
                    String key = args[0];
                    try{
                        int val = Integer.parseInt(args[1]);
                        PiSvc.svc_setIntConfig(key, val);
                        Logger.info("Set Int "+key+" = "+val);
                    }catch(NumberFormatException ex){
                        try{
                            float val = Float.parseFloat(args[1]);
                            PiSvc.svc_setFloatConfig(key, val);
                            Logger.info("Set Float "+key+" = "+val);
                        }catch(NumberFormatException ex2){
                            try{
                                String[] nums = args[1].split(",", 3);
                                var val = new piVector3f();
                                val.x = Float.parseFloat(nums[0]);
                                val.y = Float.parseFloat(nums[1]);
                                val.z = Float.parseFloat(nums[2]);
                                PiSvc.svc_setVector3fConfig(key, val);
                                Logger.info("Set Vector "+key+" = ["+val.x+", "+val.y+", "+val.z+"]");
                            }catch(Exception ex3){
                                String val = args[1];
                                PiSvc.svc_setStringConfig(key, val);
                                Logger.info("Set String "+key+" = "+val);
                            }
                        }
                    }
                }),
                new NamedCommand("bruteforce", (base, args) -> {
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    boolean[] included = new boolean[args.length];
                    int[] variant = new int[args.length];
                    int variants = 2;
                    ArrayList<String> successes = new ArrayList<>();
                    while(true){
                        boolean isDone = true;
                        for(int i = 0; i<included.length; i++){
                            if(!included[i]){
                                included[i] = true;
                                isDone = false;
                                break;
                            }
                            if(variant[i]<variants){
                                variant[i]++;
                                isDone = false;
                                break;
                            }
                            included[i] = false;
                            variant[i] = 0;
                        }
                        if(isDone)break;
                        String var = "";
                        for(int i = 0; i<included.length; i++){
                            if(included[i]){
                                String v = args[i];
                                switch(variant[i]){
                                    case 0 -> {
                                    }
                                    case 1 ->
                                        v = Character.toUpperCase(v.charAt(0))+v.substring(1);
                                    case 2 ->
                                        v = "_"+v;
                                    case 3 ->
                                        v = "_"+Character.toUpperCase(v.charAt(0))+v.substring(1);
                                }
                                var += v;
                            }
                        }
                        try{
                            String str = PiSvc.svc_getStringConfig(var, 1024);
                            for(int i = 0; i<16; i++)if(!str.isBlank())str = PiSvc.svc_getStringConfig(var, 1024);
                            String str2 = PiSvc.svc_getStringDeviceConfig(var, 1024);
                            for(int i = 0; i<1024; i++)if(!str2.isBlank())if(!str2.equals(PiSvc.svc_getStringDeviceConfig(var, 1024)))str2 = "";
                            int i = PiSvc.svc_getIntConfig(var);
                            float f = PiSvc.svc_getFloatConfig(var);
                            var vec = PiSvc.svc_getVector3fConfig(var);
                            if(str.isEmpty()
                                &&str2.isEmpty()
                                &&i==0
                                &&f==0
                                &&vec.x==0&&vec.y==0&&vec.z==0){
                                Logger.info("FAILED: "+var+" - |"+str+"|"+str2+"|"+i+"|"+f+"|"+vec.x+","+vec.y+","+vec.z+"|");
                            }else{
                                Logger.info("SUCCESS: "+var+" - |"+str+"|"+str2+"|"+i+"|"+f+"|"+vec.x+","+vec.y+","+vec.z+"|");
                                successes.add(var);
                            }
                        }catch(Error error){
                        }
                    }
                    for(String s : successes)Logger.info(s);
                }),
                new NamedCommand("ipd", (base, args) -> {
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    if(!CommandUtil.noArguments(base, args))return;
                    Logger.info("Current IPD: "+PiSvc.svc_getFloatConfig("ipd"));
                })
            )),
            new NamedCommand("grpc", CommandUtil.subcommand(null,
                new NamedCommand("start", (base, args) -> {
                    if(PiRpcAPI.active){
                        Logger.info("Pimax GRPC Manager is already active!");
                        return;
                    }
                    if(!CommandUtil.noArguments(base, args))return;
                    PiRpcAPI.start();
                }),
                new NamedCommand("stop", (base, args) -> {
                    if(!PiRpcAPI.active){
                        Logger.info("Pimax GRPC Manager is not active!");
                        return;
                    }
                    if(!CommandUtil.noArguments(base, args))return;
                    PiRpcAPI.stop();
                }),
                new NamedCommand("call", (base, args) -> {
                    if(!PiRpcAPI.active){
                        Logger.info("Pimax GRPC Manager is not active!");
                        return;
                    }
                    var msgType = PiRpcAPI.getEnum("MsgType");
                    if(args.length<1){
                        Logger.info("Please choose a message to call:");
                        for(var method : msgType.getValues())Logger.info(method.getNumber()+" - "+method.getName());
                        return;
                    }
                    if(!CommandUtil.nArguments(base, args, 1))return;
                    for(var method : msgType.getValues()){
                        if(args[0].equals(method.getName())){
                            var map = PiRpcAPI.rpcCallMessage(method, 1);
                            for(String key : map.keySet()){
                                Logger.info(key+": "+map.get(key));
                            }
                            return;
                        }
                    }
                    Logger.info("Invalid RPC message: "+args[0]);
                }),
                new NamedCommand("open", CommandUtil.subcommand(null,
                    new NamedCommand("settings", (base, args) -> {
                        if(!PiRpcAPI.active){
                            Logger.info("Pimax GRPC Manager is not active!");
                            return;
                        }
                        if(!CommandUtil.noArguments(base, args))return;
                        PiRpc.Click_ShowPitool();
                    })
                )))
            )
        );
    }
    @Override
    public void init(){
        Pimax.init();
    }
    @Override
    public boolean hasConfiguration(){
        return true;
    }
    @Override
    public JDialog getConfigurationGUI(JFrame parent){
        return new ConfigurePimaxGUI(parent);
    }
}
