package com.thizthizzydizzy.vrmanager.config.init;
import com.thizthizzydizzy.vrmanager.VRManager;
import java.io.File;
import java.util.ArrayList;
public class InitTask{
    public String title;
    public Type type = Type.RUN;
    public String target;
    public ArrayList<String> arguments = new ArrayList<>();
    public void run(){
        VRManager.start(new File(target), arguments.toArray(String[]::new));
    }
    public static enum Type{
        RUN, WATCH, RUN_JAVA;
    }
}
