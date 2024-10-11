package com.thizthizzydizzy.vrmanager.command;
import java.util.HashSet;
public interface CommandWithFlags{
    public void run(String base, String[] args, HashSet<Character> flags);
}
