package com.thizthizzydizzy.vrmanager.config.module;
import java.util.ArrayList;
import java.util.HashSet;
public class UsbConfiguration{
    public HashSet<Integer> vendors = new HashSet<>();
    public ArrayList<Device> devices = new ArrayList<>();
    public boolean watchAllDevices;
    public static class Device{
        public int vendor;
        public int product;
        public Device(int vendor, int product){
            this.vendor = vendor;
            this.product = product;
        }
        @Override
        public boolean equals(Object obj){
            return obj instanceof Device d&&d.vendor==vendor&&d.product==product;
        }
        @Override
        public int hashCode(){
            int hash = 5;
            hash = 47*hash+this.vendor;
            hash = 47*hash+this.product;
            return hash;
        }

    }
}
