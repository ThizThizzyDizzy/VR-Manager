package com.thizthizzydizzy.vrmanager.special;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.thizthizzydizzy.vrmanager.Logger;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.protobuf.ProtoUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
public class PimaxGRPC{
    public static boolean active = false;
    public static Descriptors.FileDescriptor protoDescriptor;
    private static ManagedChannel rpcChannel;
    public static Descriptors.ServiceDescriptor piRPC;
    public static Descriptors.EnumDescriptor getEnum(String name){
        for(var e : protoDescriptor.getEnumTypes()){
            if(e.getName().equals(name))return e;
        }
        return null;
    }
    private static Descriptors.ServiceDescriptor getService(String name){
        for(var s : protoDescriptor.getServices()){
            if(s.getName().equals(name))return s;
        }
        return null;
    }
    /*
    
rpcCallMessage
rpcPollMessage
rpcPollAnyMessage
rpcPollAnyMessageForTool
rpcPollStreamMessage
rpcCheckConnectStatus
     */
    public static Descriptors.MethodDescriptor getMethod(String name){
        for(var m : piRPC.getMethods()){
            if(m.getName().equals(name))return m;
        }
        return null;
    }
    public static Descriptors.FieldDescriptor getMessageField(String message, String field){
        for(var m : protoDescriptor.getMessageTypes()){
            if(m.getName().equals(message)){
                return m.findFieldByName(field);
            }
        }
        return null;
    }
    public static void start(){
        Logger.push(PimaxGRPC.class);
        Logger.info("Initializing GRPC!");
        int port;
        port = WindowsManager.getRegistryValueHex("HKEY_CURRENT_USER\\Software\\PiTool", "DeviceSettingPort");
        Logger.info("PiTool DeviceSettingPort: "+port);
        try(FileInputStream input = new FileInputStream("src\\rpc.desc")){
            for(int i = 0; i<3; i++)input.read(); //read 3 bytes because either the compiler or the parser doesn't understand its own format properly
            var proto = DescriptorProtos.FileDescriptorProto.parseFrom(input);
            protoDescriptor = Descriptors.FileDescriptor.buildFrom(proto, new Descriptors.FileDescriptor[0]);
        }catch(IOException|Descriptors.DescriptorValidationException ex){
            Logger.error("Could not read Pimax RPC proto descriptor!", ex);
            return;
        }
        rpcChannel = NettyChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
        piRPC = getService("Greeter");
        active = true;
        Logger.info("GRPC is now active!");
        Logger.pop();
    }
    public static void stop(){
        Logger.push(PimaxGRPC.class);
        Logger.info("Shutting down GRPC!");
        rpcChannel.shutdownNow();
        //also clean up variables
        rpcChannel = null;
        piRPC = null;
        protoDescriptor = null;
        active = false;
        Logger.info("GRPC has stopped.");
        Logger.pop();
    }
    public static HashMap<String, Object> rpcCallMessage(Descriptors.EnumValueDescriptor requestType) throws InterruptedException{//TODO proper error handling
        Logger.push(PimaxGRPC.class);
        var callMessage = PimaxGRPC.getMethod("rpcCallMessage");
        var reqType = PimaxGRPC.getField(callMessage, "req_type");
        var replyArray = getMessageField("PollAnyMsgReply", "reply_array");
        var pollReqType = getMessageField("PollMsgReply", "poll_req_type");
        var mapResult = getMessageField("PollMsgReply", "map_result");
        var isErr = getMessageField("PollMsgReply", "is_err");
        var replyFields = getMessageField("Struct", "fields");
        HashMap<String, Object> rpcResult = new HashMap<>();
        boolean[] hasResult = new boolean[1];
        for(int i = 0; i<10000; i++){
            if(i%1000==0){
                Logger.info("Sending GRPC: "+callMessage.getFullName()+" "+requestType.getFullName()+" (Attempt "+((i/1000)+1)+")");
                callRPC(callMessage, (t) -> t.setField(reqType, requestType).build(), (t) -> {
                    Logger.info(t.toString());
                });
            }
            callRPC(getMethod("rpcPollAnyMessage"), (builder) -> builder.build(), (msg) -> {
                if(msg.getAllFields().isEmpty())return;
                for(int j = 0; j<msg.getRepeatedFieldCount(replyArray); j++){
                    DynamicMessage message = (DynamicMessage)msg.getRepeatedField(replyArray, j);
                    if(message.hasField(pollReqType)&&message.getField(pollReqType).equals(requestType)){
                        if((Boolean)message.getField(isErr))Logger.info("RPC returned an error!");
                        DynamicMessage result = (DynamicMessage)message.getField(mapResult);
                        for(int k = 0; k<result.getRepeatedFieldCount(replyFields); k++){
                            DynamicMessage replyField = (DynamicMessage)result.getRepeatedField(replyFields, k);
                            String key = null;
                            DynamicMessage value = null;
                            for(var field : replyField.getAllFields().keySet()){
                                if(field.getName().equals("key"))key = (String)replyField.getField(field);
                                if(field.getName().equals("value"))value = (DynamicMessage)replyField.getField(field);
                            }
                            for(var field : value.getAllFields().keySet()){
                                switch(field.getName()){
                                    case "null_value" -> {
                                        hasResult[0] = true;
                                    }
                                    case "double_value" -> {
                                        rpcResult.put(key, (double)value.getField(field));
                                        hasResult[0] = true;
                                    }
                                    case "string_value" -> {
                                        rpcResult.put(key, (String)value.getField(field));
                                        hasResult[0] = true;
                                    }
                                    case "bool_value" -> {
                                        rpcResult.put(key, (boolean)value.getField(field));
                                        hasResult[0] = true;
                                    }
                                    case "int32_value" -> {
                                        rpcResult.put(key, (int)value.getField(field));
                                        hasResult[0] = true;
                                    }
                                    case "int64_value" -> {
                                        rpcResult.put(key, (long)value.getField(field));
                                        hasResult[0] = true;
                                    }
                                    default ->
                                        Logger.error("Unsupported value type: "+field.getFullName()+"! ("+value.getField(field).getClass().getName()+")");
                                }
                            }
                        }
                    }
                }
            });
            if(hasResult[0])break;
        }
        if(hasResult[0]){
            Logger.info("Success!");
        }else{
            Logger.info("RPC Timed out!");
        }
        Logger.pop();
        return rpcResult;
    }
    public static void callRPC(Descriptors.MethodDescriptor method, Function<DynamicMessage.Builder, DynamicMessage> buildMessage, Consumer<DynamicMessage> listener) throws InterruptedException{//TODO proper error handling
        Metadata metadata = new Metadata();
        var call = rpcChannel.newCall(MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
            .setType(getMethodTypeFromDesc(method))
            .setFullMethodName(MethodDescriptor.generateFullMethodName(
                piRPC.getFullName(), method.getName()))
            .setRequestMarshaller(ProtoUtils.marshaller(
                DynamicMessage.getDefaultInstance(method.getInputType())))
            .setResponseMarshaller(ProtoUtils.marshaller(
                DynamicMessage.getDefaultInstance(method.getOutputType())))
            .build(), CallOptions.DEFAULT);

        DynamicMessage request = buildMessage.apply(DynamicMessage.newBuilder(method.getInputType()));
        boolean[] closed = new boolean[1];
        call.start(new ClientCall.Listener<DynamicMessage>(){
            @Override
            public void onMessage(DynamicMessage message){
                if(listener!=null)listener.accept(message);
            }
            @Override
            public void onClose(Status status, Metadata trailers){
                closed[0] = true;
            }
        }, metadata);
        call.sendMessage(request);
        call.halfClose();
        call.request(1);
        while(!closed[0])Thread.sleep(1);
    }

    static MethodDescriptor.MethodType getMethodTypeFromDesc(Descriptors.MethodDescriptor methodDesc){
        if(!methodDesc.isServerStreaming()
            &&!methodDesc.isClientStreaming()){
            return MethodDescriptor.MethodType.UNARY;
        }else if(methodDesc.isServerStreaming()
            &&!methodDesc.isClientStreaming()){
            return MethodDescriptor.MethodType.SERVER_STREAMING;
        }else if(!methodDesc.isServerStreaming()){
            return MethodDescriptor.MethodType.CLIENT_STREAMING;
        }else{
            return MethodDescriptor.MethodType.BIDI_STREAMING;
        }
    }
    public static Descriptors.FieldDescriptor getField(Descriptors.MethodDescriptor method, String field){
        for(var f : method.getInputType().getFields()){
            if(f.getName().equals(field))return f;
        }
        return null;
    }
}
