package com.thizthizzydizzy.vrmanager.special;
import com.thizthizzydizzy.vrmanager.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
public class Windows{
    public static String getRegistryValue(String path, String key){
        for(String str : runCommand("reg query "+path+" /v "+key).split("\n")){
            str = str.trim();
            if(str.startsWith(key))return str.split("    ")[2];
        }
        return null;
    }
    public static Integer getRegistryValueHex(String path, String key){
        return Integer.valueOf(getRegistryValue(path, key).substring(2), 16);
    }
    public static void taskkill(String exeName){
        runCommand("taskkill /IM "+exeName);
    }
    public static String runCommand(String command){
        Logger.push("Windows");
        Logger.info("Running windows command: "+command);
        Process p;
        try{
            p = Runtime.getRuntime().exec(command);
        }catch(IOException ex){
            Logger.error("Failed to start process!", ex);
            Logger.pop();
            return null;
        }
        String[] output = new String[]{""};
        Process prog = p;
        new Thread(()->{
            Logger.push("CMD");
            BufferedReader in = new BufferedReader(new InputStreamReader(prog.getInputStream()));
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
    public static ArrayList<WindowsTask> getTasks() throws IOException{
        return getTasks(false);
    }
    public static ArrayList<WindowsTask> getTasks(boolean verbose) throws IOException{
        ArrayList<WindowsTask> tasks = new ArrayList<>();
        Process process = Runtime.getRuntime().exec("tasklist"+(verbose?" /v":""));
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
            String line;
            int[] startIndicies = new int[verbose?9:6];
            startIndicies[0] = -1;
            while ((line = reader.readLine()) != null) {
                if(line.startsWith("=")){
                    startIndicies[0] = 0;
                    for(int i = 1; i<startIndicies.length; i++){
                        startIndicies[i] = line.indexOf(" ", startIndicies[i-1]+1)+1;
                    }
                }else if(startIndicies[0]>=0){
                    tasks.add(new WindowsTask(line.substring(startIndicies[0], startIndicies[1]).trim(),
                    Long.parseLong(line.substring(startIndicies[1], startIndicies[2]).trim()),
                    line.substring(startIndicies[2], startIndicies[3]).trim(),
                    verbose?line.substring(startIndicies[5], startIndicies[6]).trim():null,
                    verbose?line.substring(startIndicies[6], startIndicies[7]).trim():null,
                    verbose?line.substring(startIndicies[8]).trim():null));
                }
            }
        }
        return tasks;
    }
    public static ArrayList<String> getWindowTitle() throws IOException{
        ArrayList<String> titles = new ArrayList<>();
        for(var task : getTasks())titles.add(task.windowTitle);
        return titles;
    }
    public static boolean hasTask(String target){
        Logger.push("Windows");
        try{
            for(WindowsTask task : getTasks()){
                if(task.imageName.trim().equals(target.trim())){
                    Logger.pop();
                    return true;
                }
            }
        }catch(IOException ex){
            Logger.error("Unable to list tasks!", ex);
        }
        Logger.pop();
        return false;
    }
    public static class WindowsTask{
        public final String imageName;
        public final long pid;
        public final String sessionName;
        public final String status;
        public final String userName;
        public final String windowTitle;
        public WindowsTask(String imageName, long pid, String sessionName, String status, String userName, String windowTitle){
            this.imageName = imageName;
            this.pid = pid;
            this.sessionName = sessionName;
            this.status = status;
            this.userName = userName;
            this.windowTitle = windowTitle;
        }
    }
}
