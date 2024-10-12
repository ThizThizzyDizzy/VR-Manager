package com.thizthizzydizzy.vrmanager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class Telemetry{
    public static void send(String string){
        if(!VRManager.configuration.enableTelemetry)return;
        var source = Logger.getCurrentSource();
        if(source!=null)string = "["+Logger.getCurrentSource()+"] "+string;
        Logger.push(Telemetry.class);
        Logger.info(string);
        try{
            URL url = new URL("http://analytics.thizthizzydizzy.com:8080/vrmanager");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setDoOutput(true);

            try(OutputStream os = connection.getOutputStream()){
                os.write(string.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if(responseCode>=300){
                Logger.error("Failed to send telemetry! (HTTP "+responseCode+")");
            }
        }catch(IOException ex){
            Logger.error("Failed to send telemetry! "+ex.getMessage());
        }
        Logger.pop();
    }
}
