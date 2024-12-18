package com.thizthizzydizzy.vrmanager;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Stack;
public class Logger{
    private static final HashMap<Thread, Stack<String>> sourceStacks = new HashMap<>();
    public static Stack<String> getSourceStack(){
        var thread = Thread.currentThread();
        if(!sourceStacks.containsKey(thread)){
            sourceStacks.put(thread, new Stack<>());
        }
        return sourceStacks.get(thread);
    }
    public static void push(Object source){
        push(source.getClass());
    }
    public static void push(Class source){
        push(source.getSimpleName());
    }
    public static void push(String source){
        getSourceStack().push(source);
    }
    public static void pop(){
        var stack = getSourceStack();
        if(stack.isEmpty())warn("Tried to pop empty logger source stack!", new RuntimeException());
        else getSourceStack().pop();
    }
    public static void reset(){
        getSourceStack().clear();
    }
    public static String getCurrentSource(){
        var stack = getSourceStack();
        return stack.isEmpty()?null:stack.peek();
    }
    public static void log(MessageType type, String message, Throwable t){//TODO log to a file
        PrintStream out = type==MessageType.ERROR?System.err:System.out;
        String source = getCurrentSource();
        String err = message!=null?message+"\n":"";
        if(t!=null){
            err += t.getClass().getName()+": "+t.getMessage();
            for(var stackTrace : t.getStackTrace())err += "\n"+stackTrace.toString();
        }
        err = (source!=null?"["+source+"] ":"")+err;
        String line = LocalDateTime.now().toString()+" "+Thread.currentThread().getName()+" "+type.toString()+": "+err;
        out.println(err.trim());
    }
    public static void info(String message, Throwable t){
        log(MessageType.INFO, message, t);
    }
    public static void info(String message){
        info(message, null);
    }
    public static void warn(String message, Throwable t){
        log(MessageType.WARN, message, t);
    }
    public static void warn(String message){
        warn(message, null);
    }
    public static void error(String message, Throwable t){
        log(MessageType.ERROR, message, t);
    }
    public static void error(String message){
        error(message, null);
    }
    public static void error(Throwable t){
        error(null, t);
    }
    public static enum MessageType{
        INFO,
        WARN,
        ERROR;
    }
}
