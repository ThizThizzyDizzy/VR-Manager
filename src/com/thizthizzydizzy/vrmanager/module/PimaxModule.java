package com.thizthizzydizzy.vrmanager.module;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.command.Command;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.special.PiSvc;
import com.thizthizzydizzy.vrmanager.special.PimaxGRPC;
import com.thizthizzydizzy.vrmanager.special.piSvc.piSvcCAPI;
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
                    if(!Command.noArguments(base, args))return;
                    if(PiSvc.active){
                        Logger.info("PiSvc Manager is already active!");
                        return;
                    }
                    PiSvc.start();
                }),
                new NamedCommand("stop", (base, args) -> {
                    if(!Command.noArguments(base, args))return;
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    PiSvc.stop();
                }),
                new NamedCommand("ipd", (base, args) -> {
                    if(!Command.noArguments(base, args))return;
                    if(!PiSvc.active){
                        Logger.info("PiSvc Manager is not active!");
                        return;
                    }
                    Logger.info("Current IPD: "+PiSvc.svc_getFloatConfig("ipd"));
                })
            )),
            new NamedCommand("grpc", Command.subcommand(null,
                new NamedCommand("start", (base, args) -> {
                    if(!Command.noArguments(base, args))return;
                    if(PimaxGRPC.active){
                        Logger.info("Pimax GRPC Manager is already active!");
                        return;
                    }
                    PimaxGRPC.start();
                }),
                new NamedCommand("stop", (base, args) -> {
                    if(!Command.noArguments(base, args))return;
                    if(!PimaxGRPC.active){
                        Logger.info("Pimax GRPC Manager is not active!");
                        return;
                    }
                    PimaxGRPC.stop();
                }),
                new NamedCommand("call", (base, args) -> {
                    if(!PimaxGRPC.active){
                        Logger.info("Pimax GRPC Manager is not active!");
                        return;
                    }
                    var msgType = PimaxGRPC.getEnum("MsgType");
                    if(args.length<1){
                        Logger.info("Please choose a message to call:");
                        for(var method : msgType.getValues())Logger.info(method.getNumber()+" - "+method.getName());
                        return;
                    }
                    if(!Command.nArguments(base, args, 1))return;
                    for(var method : msgType.getValues()){
                        if(args[0].equals(method.getName())){
                            try{
                                var map = PimaxGRPC.rpcCallMessage(method, 1);
                                for(String key : map.keySet()){
                                    Logger.info(key+": "+map.get(key));
                                }
                            }catch(InterruptedException ex){
                                Logger.error("RPC was interrupted!", ex);
                            }
                            return;
                        }
                    }
                    Logger.info("Invalid RPC message: "+args[0]);
                }))
            ));
    }
}
