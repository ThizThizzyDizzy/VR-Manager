package com.thizthizzydizzy.vrmanager.module;
import com.thizthizzydizzy.vrmanager.command.NamedCommand;
import com.thizthizzydizzy.vrmanager.command.Command;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.piRpc.PiRpcAPI;
public class PimaxModule extends VRModule{
    @Override
    public String getName(){
        return "pimax";
    }
    @Override
    public NamedCommand[] getCommands(){
        return Command.subcommands(new NamedCommand("pisvc", Command.subcommand(null,
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
                    if(PiRpcAPI.active){
                        Logger.info("Pimax GRPC Manager is already active!");
                        return;
                    }
                    PiRpcAPI.start();
                }),
                new NamedCommand("stop", (base, args) -> {
                    if(!Command.noArguments(base, args))return;
                    if(!PiRpcAPI.active){
                        Logger.info("Pimax GRPC Manager is not active!");
                        return;
                    }
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
                }))
            ));
    }
}
