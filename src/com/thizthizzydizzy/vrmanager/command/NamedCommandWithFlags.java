package com.thizthizzydizzy.vrmanager.command;
import java.util.HashMap;
import java.util.HashSet;
public class NamedCommandWithFlags extends NamedCommand{
    private final HashMap<Character, String> possibleFlags = new HashMap<>();
    public NamedCommandWithFlags(String name, CommandWithFlags commandWithFlags){
        super(name);
        command = (base, args) -> {
            HashSet<Character> flags = new HashSet<>();
            commandWithFlags.run(base, extractFlags(args, flags), flags);
        };
    }
    public NamedCommandWithFlags addFlag(char c, String description){
        possibleFlags.put(c, description);
        return this;
    }
    public String[] extractFlags(String[] arguments, HashSet<Character> flags){
        char[] thePossibleFlags = new char[possibleFlags.size()];
        int i = 0;
        for(char c : possibleFlags.keySet())thePossibleFlags[i++] = c;
        return CommandUtil.extractFlags(arguments, flags, thePossibleFlags);
    }
}
