package com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
public class piSvcHmdInfo extends Structure{
    public char[] ProductName = new char[64];
    public char[] Manufacturer = new char[64];

    // SN&lcd type, version
    public char[] SerialNumber = new char[64];
    public char[] version_generation = new char[64];
    public int version_major;
    public int version_Minor;
    public char[] DisplayPiSvcHmdiceName = new char[64];
    public piSvcHmdInfo(){
        super();
    }
    public piSvcHmdInfo(Pointer p){
        super(p);
        read();
    }
    @Override
    protected List<String> getFieldOrder(){
        return Arrays.asList("ProductName", "Manufacturer", "SerialNumber", "version_generation", "version_major", "version_Minor", "DisplayPiSvcHmdiceName");
    }
}
