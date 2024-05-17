package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class WindowsManager{
    public static String getRegistryValue(String path, String key){
        try{
            for(String str : runCommand("reg query "+path+" /v "+key).split("\n")){
                str = str.trim();
                if(str.startsWith(key))return str.split("    ")[2];
            }
        }catch(IOException ex){
            Logger.error("Could not fetch registry value at "+path+"/"+key+"!", ex);
            return null;
        }
        return null;
    }
    public static Integer getRegistryValueHex(String path, String key){
        return Integer.valueOf(getRegistryValue(path, key).substring(2), 16);
    }
    private static String runCommand(String command) throws IOException{
        Logger.push("Windows");
        Logger.info("Running windows command: "+command);
        Process p = Runtime.getRuntime().exec(command);
        String[] output = new String[]{""};
        new Thread(()->{
            Logger.push("CMD");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            try{
                while((line = in.readLine())!=null){
                    Logger.info(line);
                    output[0] += line+"\n";
                }
            }catch(IOException ex){
                throw new RuntimeException(ex);
            }
            Logger.pop();
        }, "System.out transfer").start();
        while(p.isAlive()){
            try{
                Thread.sleep(1000);
            }catch(InterruptedException ex){
                Logger.error("Command execution was interrupted!");
                p.destroy();
                break;
            }
        }
        Logger.pop();
        return output[0];
    }
}
