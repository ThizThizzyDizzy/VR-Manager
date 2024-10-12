package com.thizthizzydizzy.vrmanager.gui.module;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.config.module.UsbConfiguration;
import com.thizthizzydizzy.vrmanager.special.usb.UsbDictionary;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
public class ConfigureUsbGUI extends javax.swing.JDialog{
    private HashMap<JCheckBox, Integer> checkBoxes = new HashMap<>();
    public ConfigureUsbGUI(java.awt.Frame parent){
        super(parent, true);
        initComponents();
        for(var vendor : UsbDictionary.knownVendors.keySet()){
            JCheckBox cb = new JCheckBox(UsbDictionary.knownVendors.get(vendor));
            cb.setFont(checkBoxWatchAll.getFont());
            cb.addActionListener((e) -> {
                if(cb.isSelected()){
                    VRManager.configuration.usb.vendors.add(vendor);
                }else{
                    VRManager.configuration.usb.vendors.remove(vendor);
                }
                VRManager.saveConfig();
                refresh();
            });
            checkBoxes.put(cb, vendor);
            panelSettings.add(cb, panelSettings.getComponentCount()-1);
        }
        refresh();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        buttonAddDevice = new javax.swing.JButton();
        buttonRemoveDevice = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listDevices = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        buttonAddVendor = new javax.swing.JButton();
        buttonRemoveVendor = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        listVendors = new javax.swing.JList<>();
        panelSettings = new javax.swing.JPanel();
        checkBoxWatchAll = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Webhook Manager Configuration");

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Watch Devices");
        jPanel2.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        buttonAddDevice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonAddDevice.setText("Add Device");
        buttonAddDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddDeviceActionPerformed(evt);
            }
        });
        jPanel6.add(buttonAddDevice);

        buttonRemoveDevice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonRemoveDevice.setText("Remove Device");
        buttonRemoveDevice.setEnabled(false);
        buttonRemoveDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveDeviceActionPerformed(evt);
            }
        });
        jPanel6.add(buttonRemoveDevice);

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        listDevices.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        listDevices.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listDevicesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listDevices);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Watch Vendors");
        jPanel3.add(jLabel5, java.awt.BorderLayout.PAGE_START);

        jPanel8.setLayout(new java.awt.GridLayout());

        buttonAddVendor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonAddVendor.setText("Add Vendor");
        buttonAddVendor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddVendorActionPerformed(evt);
            }
        });
        jPanel8.add(buttonAddVendor);

        buttonRemoveVendor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonRemoveVendor.setText("Remove Vendor");
        buttonRemoveVendor.setEnabled(false);
        buttonRemoveVendor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveVendorActionPerformed(evt);
            }
        });
        jPanel8.add(buttonRemoveVendor);

        jPanel3.add(jPanel8, java.awt.BorderLayout.PAGE_END);

        listVendors.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        listVendors.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listVendorsValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(listVendors);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        panelSettings.setLayout(new java.awt.GridLayout(0, 1));

        checkBoxWatchAll.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        checkBoxWatchAll.setText("Watch ALL Devices");
        checkBoxWatchAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxWatchAllActionPerformed(evt);
            }
        });
        panelSettings.add(checkBoxWatchAll);

        getContentPane().add(panelSettings, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void buttonAddDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddDeviceActionPerformed
        String vendorStr = JOptionPane.showInputDialog(this, "Enter Vendor ID (hex)", "Watch Device", JOptionPane.PLAIN_MESSAGE);
        if(vendorStr==null)return;
        int vendorID = -1;
        try{
            vendorID = Integer.parseInt(vendorStr, 16);
        }catch(NumberFormatException ex){
            return;
        }
        String productStr = JOptionPane.showInputDialog(this, "Enter Product ID (hex)", "Watch Device", JOptionPane.PLAIN_MESSAGE);
        if(productStr==null)return;
        int productID = -1;
        try{
            productID = Integer.parseInt(productStr, 16);
        }catch(NumberFormatException ex){
            return;
        }
        VRManager.configuration.usb.devices.add(new UsbConfiguration.Device(vendorID, productID));
        VRManager.saveConfig();
        refresh();
    }//GEN-LAST:event_buttonAddDeviceActionPerformed
    private void buttonRemoveDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveDeviceActionPerformed
        VRManager.configuration.usb.devices.remove(listDevices.getSelectedIndex());
        VRManager.saveConfig();
        refresh();
    }//GEN-LAST:event_buttonRemoveDeviceActionPerformed
    private void listDevicesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listDevicesValueChanged
        buttonRemoveDevice.setEnabled(listDevices.getSelectedIndex()>=0);
    }//GEN-LAST:event_listDevicesValueChanged
    private void buttonAddVendorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddVendorActionPerformed
        String vendorStr = JOptionPane.showInputDialog(this, "Enter Vendor ID", "Watch Device", JOptionPane.PLAIN_MESSAGE);
        if(vendorStr==null)return;
        int vendorID = -1;
        try{
            vendorID = Integer.parseInt(vendorStr);
        }catch(NumberFormatException ex){
            return;
        }
        VRManager.configuration.usb.vendors.add(vendorID);
        VRManager.saveConfig();
        refresh();
    }//GEN-LAST:event_buttonAddVendorActionPerformed
    private void buttonRemoveVendorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveVendorActionPerformed
        for(Iterator<Integer> it = VRManager.configuration.usb.vendors.iterator(); it.hasNext();){
            int vendor = it.next();
            if(UsbDictionary.getDisplayName(vendor).equals(listVendors.getSelectedValue()))it.remove();
        }
        VRManager.saveConfig();
        refresh();
    }//GEN-LAST:event_buttonRemoveVendorActionPerformed
    private void listVendorsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listVendorsValueChanged
        buttonRemoveVendor.setEnabled(listVendors.getSelectedIndex()>=0);
    }//GEN-LAST:event_listVendorsValueChanged
    private void checkBoxWatchAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxWatchAllActionPerformed
        VRManager.configuration.usb.watchAllDevices = checkBoxWatchAll.isSelected();
        VRManager.saveConfig();
    }//GEN-LAST:event_checkBoxWatchAllActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddDevice;
    private javax.swing.JButton buttonAddVendor;
    private javax.swing.JButton buttonRemoveDevice;
    private javax.swing.JButton buttonRemoveVendor;
    private javax.swing.JCheckBox checkBoxWatchAll;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listDevices;
    private javax.swing.JList<String> listVendors;
    private javax.swing.JPanel panelSettings;
    // End of variables declaration//GEN-END:variables
    private void refresh(){
        checkBoxWatchAll.setSelected(VRManager.configuration.usb.watchAllDevices);
        for(var box : checkBoxes.keySet()){
            box.setSelected(VRManager.configuration.usb.vendors.contains(checkBoxes.get(box)));
        }
        var vendorListModel = new DefaultListModel<String>();
        for(var vendor : VRManager.configuration.usb.vendors){
            vendorListModel.addElement(UsbDictionary.getDisplayName(vendor));
        }
        listVendors.setModel(vendorListModel);
        var deviceListModel = new DefaultListModel<String>();
        for(var device : VRManager.configuration.usb.devices){
            deviceListModel.addElement(UsbDictionary.getDisplayName(device.vendor, device.product));
        }
        listDevices.setModel(deviceListModel);
    }
}
