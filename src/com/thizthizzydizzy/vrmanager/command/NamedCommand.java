package com.thizthizzydizzy.vrmanager.command;
public class NamedCommand implements Command{
    public final String name;
    protected Command command;
    protected NamedCommand(String name){
        this.name = name;
    }
    public NamedCommand(String name, Command command){
        this.name = name;
        this.command = command;
    }
    @Override
    public void run(String base, String[] arguments){
        command.run(base, arguments);
    }
}
