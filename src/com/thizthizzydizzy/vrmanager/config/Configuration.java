package com.thizthizzydizzy.vrmanager.config;
import com.thizthizzydizzy.vrmanager.config.init.InitConfiguration;
import com.thizthizzydizzy.vrmanager.config.module.PimaxConfiguration;
import java.util.ArrayList;
public class Configuration{
    public InitConfiguration initialization = new InitConfiguration();
    public ArrayList<String> modules = new ArrayList<>();
    public PimaxConfiguration pimax = new PimaxConfiguration();
}
