package com.thizthizzydizzy.vrmanager.command;
import com.thizthizzydizzy.vrmanager.Logger;
import java.util.function.Function;
public interface Command{
    public void run(String base, String[] arguments);
    public static String[] trimArgument(String[] arguments){
        return trimArguments(arguments, 1);
    }
    public static String[] trimArguments(String[] arguments, int count){
        String[] trimmed = new String[Math.max(0, arguments.length-count)];
        for(int i = 0; i<trimmed.length; i++)trimmed[i] = arguments[i+1];
        return trimmed;
    }
    public static Command subcommand(Runnable onNull, NamedCommand... options){
        return (String base, String[] args) -> {
            chooseSubcommand(base, args, onNull, options);
        };
    }
    public static NamedCommand[] subcommands(NamedCommand... commands){
        return commands;
    }
    public static void chooseCommand(String base, String[] arguments, Function<String, String> onNull, NamedCommand... options){
        String[] args = new String[arguments.length+1];
        args[0] = base;
        System.arraycopy(arguments, 0, args, 1, arguments.length);
        chooseSubcommand("<base>", args, null, options);
    }
    public static void chooseSubcommand(String base, String[] arguments, Runnable onNull, NamedCommand... options){
        String[] strs = new String[options.length];
        for(int i = 0; i<options.length; i++)strs[i] = options[i].name;
        if(arguments.length==0){
            if(onNull==null){
                Logger.info(base+" requires an argument! Valid arguments: "+String.join(", ", strs));
                return;
            }
            onNull.run();
            return;
        }
        for(NamedCommand option : options){
            if(arguments[0].equals(option.name)){
                option.run(arguments[0], trimArgument(arguments));
                return;
            }
        }
        if(base.equals("<base>"))
            Logger.info("Invalid command! Valid commands: "+String.join(", ", strs));
        else
            Logger.info("Invalid argument for "+base+"! Valid arguments: "+String.join(", ", strs));
    }
    public static boolean noArguments(String base, String[] arguments){
        if(arguments.length>0){
            Logger.info(base+" does not accept any arguments!");
            return false;
        }
        return true;
    }
    public static boolean nArguments(String base, String[] arguments, int n){
        if(arguments.length!=n){
            Logger.info(base+" must have exactly "+n+" argument"+(arguments.length==1?"":"s")+"!");
            return false;
        }
        return true;
    }
}
