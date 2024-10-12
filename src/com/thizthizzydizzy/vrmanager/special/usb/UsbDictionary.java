package com.thizthizzydizzy.vrmanager.special.usb;
import java.util.HashMap;
public class UsbDictionary{
    public static final HashMap<Integer, String> knownVendors = new HashMap<>();
    public static final HashMap<Integer, HashMap<Integer, String>> knownProducts = new HashMap<>();
    public static final HashMap<Integer, HashMap<Integer, Boolean>> transientProducts = new HashMap<>();
    static{
        addVendor((short)0x2104, "Tobii");
        addProduct((short)0x2104,(short) 0x0220, "Tobii Eye Tracking", true);
        addVendor((short)0x28DE, "Valve");
        addProduct((short)0x28DE, (short)0x2101, "Watchman Dongle", false);
        addProduct((short)0x28DE, (short)0x2102, "High-Power Watchman Dongle", false);
        addProduct((short)0x28DE, (short)0x2613, "Valve Index");
        addProduct((short)0x28DE, (short)0x2400, "Valve Index Camera");
        addVendor((short)0x18d1, "Google");
        addProduct((short)0x18d1, (short)0x4ee2, "Android Device", false);
        addVendor((short)0x34A4, "Pimax");
        addProduct((short)0x28DE, (short)0x2300, "SteamVR-Tracked Device", true);
        addProduct((short)0x34A4, (short)0x0012, "Pimax Crystal", false);
        addProduct((short)0x34A4, (short)0x0014, "Pimax Crystal Light (0x14)", true);
        addProduct((short)0x34A4, (short)0x0016, "Pimax Crystal Light (0x16)", false);
        addProduct((short)0x34A4, (short)0x0018, "Pimax Crystal Light (0x18)", false);
        addVendor((short)0x2936, "Ultraleap");
        addProduct((short)0x2936, (short)0x1206, "Leap Motion Controller 2", true);
        addVendor((short)0x0BB4, "HTC");
        addProduct((short)0x0BB4, (short)0x0321, "Vive Facial Tracker", true);
        addVendor((short)0x1915, "bHaptics");
        addProduct((short)0x1915, (short)0x521a, "bHaptics TactSuit X40", true);
        addVendor((short)0xFFFFC4F4, "KatVR");
        addProduct((short)0xFFFFC4F4, (short)0x8f37, "KatVR Wireless Reciever", false);
        addProduct((short)0xFFFFC4F4, (short)0x2f37, "Kat Walk C2+", false);
        addProduct((short)0xFFFFC4F4, (short)0x7f37, "KatVR Directional Sensor", false);
        addProduct((short)0xFFFFC4F4, (short)0x6f37, "KatVR Foot Sensor", false);
    }
    private static void addVendor(int vendorID, String name){
        knownVendors.put(vendorID, name);
    }
    private static void addProduct(int vendorID, int productID, String name){
        addProduct(vendorID, productID, name, true);
    }
    private static void addProduct(int vendorID, int productID, String name, boolean isTransient){
        if(!knownProducts.containsKey(vendorID))knownProducts.put(vendorID, new HashMap<>());
        knownProducts.get(vendorID).put(productID, name);
        if(!transientProducts.containsKey(vendorID))transientProducts.put(vendorID, new HashMap<>());
        transientProducts.get(vendorID).put(productID, isTransient);
    }
    public static String getDisplayName(int vendorID, int productID){
        if(knownProducts.containsKey(vendorID)){
            var map = knownProducts.get(vendorID);
            if(map.containsKey(productID))return map.get(productID);
        }
        if(knownVendors.containsKey(vendorID))return knownVendors.get(vendorID)+" "+Integer.toHexString(productID);
        return Integer.toHexString(vendorID)+" "+Integer.toHexString(productID);
    }
    public static String getDisplayName(int vendorID){
        if(knownVendors.containsKey(vendorID))return knownVendors.get(vendorID);
        return Integer.toHexString(vendorID);
    }
    public static boolean isTransient(int vendorID, int productID){
        if(transientProducts.containsKey(vendorID)){
            var map = transientProducts.get(vendorID);
            if(map.containsKey(productID))return map.get(productID);
        }
        return true;//treat all unknown devices as transient
    }
}
