package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.command.Command;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.special.pimax.PiRpc;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.PiRpcAPI;
import java.util.ArrayList;
public class PimaxModule extends VRModule{
    @Override
    public String getName(){
        return "pimax";
    }
    @Override
    public NamedCommand[] getCommands(){
        return Command.subcommands(
            new NamedCommand("pisvc", Command.subcommand(null,
                new NamedCommand("start", (base, args) -> {
                    if(PiSvc.active){
                        Logger.info("PiSvc Manager is already active!");
                        return;
                    }
                    if(!Command.noArguments(base, args))return;
                    PiSvc.start();
                }),
                new NamedCommand("stop", (base, args) -> {
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    if(!Command.noArguments(base, args))return;
                    PiSvc.stop();
                }),
                new NamedCommand("get", (base, args) -> {
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    if(args.length==0){
                        Logger.info("""
                                    Please provide a variable to GET. Known variables:
                                    ipd (int)""");
                        return;
                    }
                    if(!Command.nArguments(base, args, 1))return;
                    Logger.info("String: "+PiSvc.svc_getStringConfig(args[0], 1024));
                    Logger.info("Device String: "+PiSvc.svc_getStringDeviceConfig(args[0], 1024));
                    Logger.info("Int: "+PiSvc.svc_getIntConfig(args[0]));
                    Logger.info("Float: "+PiSvc.svc_getFloatConfig(args[0]));
                    var vec = PiSvc.svc_getVector3fConfig(args[0]);
                    Logger.info("Vector3: ["+vec.x+", "+vec.y+", "+vec.z+"]");
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
                            for(int i = 0; i<16; i++)if(!str2.isBlank())if(!str2.equals(PiSvc.svc_getStringDeviceConfig(var, 1024)))str2 = "";
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
                    if(!Command.noArguments(base, args))return;
                    Logger.info("Current IPD: "+PiSvc.svc_getFloatConfig("ipd"));
                })
            )),
            new NamedCommand("grpc", Command.subcommand(null,
                new NamedCommand("start", (base, args) -> {
                    if(PiRpcAPI.active){
                        Logger.info("Pimax GRPC Manager is already active!");
                        return;
                    }
                    if(!Command.noArguments(base, args))return;
                    PiRpcAPI.start();
                }),
                new NamedCommand("stop", (base, args) -> {
                    if(!PiRpcAPI.active){
                        Logger.info("Pimax GRPC Manager is not active!");
                        return;
                    }
                    if(!Command.noArguments(base, args))return;
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
                    if(!Command.nArguments(base, args, 1))return;
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
                new NamedCommand("open", Command.subcommand(null,
                    new NamedCommand("settings", (base, args) -> {
                        if(!PiRpcAPI.active){
                            Logger.info("Pimax GRPC Manager is not active!");
                            return;
                        }
                        if(!Command.noArguments(base, args))return;
                        PiRpc.Click_ShowPitool();
                    })
                )))
            )
        );
    }
}
