package com.thizthizzydizzy.vrmanager.special.piSvc.piSvcDesc;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
public class piSvcControllerInfo extends Structure{
    public char[] ProductName = new char[64];
    public char[] Manufacturer = new char[64];

    // SN, type, version
    public char[] SerialNumber = new char[64];
    public char[] version_generation = new char[64];
    public int version_major;
    public int version_Minor;
    public char[] DisplayPiSAvcControllericeName = new char[64];
    public piSvcControllerInfo(){
        super();
    }
    public piSvcControllerInfo(Pointer p){
        super(p);
        read();
    }
    @Override
    protected List<String> getFieldOrder(){
        return Arrays.asList("ProductName", "Manufacturer", "SerialNumber", "version_generation", "version_major", "version_Minor", "DisplayPiSAvcControllericeName");
    }
}
