package com.thizthizzydizzy.vrmanager.gui.module;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvc;
import com.thizthizzydizzy.vrmanager.special.pimax.PiSvcConfig;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
public class ConfigurePimaxGUI extends javax.swing.JDialog{
    private HashMap<PiSvcConfig, JCheckBox> bools = new HashMap<>();
    private HashMap<PiSvcConfig, JRadioButton[]> smallInts = new HashMap<>();
    private HashMap<PiSvcConfig, ButtonGroup> smallIntGroups = new HashMap<>();
    private HashMap<PiSvcConfig, JSlider> ints = new HashMap<>();
    private HashMap<PiSvcConfig, JSlider> floats = new HashMap<>();
    private HashMap<PiSvcConfig, JSlider[]> vectors = new HashMap<>();
    private static final float FLOAT_MULTIPLIER = 10_000;//used to convert float values into ints to display on sliders. This defines the resolution of the slider.
    private boolean refreshing;
    public ConfigurePimaxGUI(java.awt.Frame parent){
        super(parent, true);
        initComponents();
        panelKnownSettings.removeAll();
        for(var config : PiSvc.knownConfigKeys){
            if(!config.writable||!config.hasDefinedRange)continue;
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new LineBorder(Color.gray));
            panel.add(BorderLayout.NORTH, new JLabel(config.description));
            switch(config.type){
                case INT -> {
                    int min = (int)config.min;
                    int max = (int)config.max;
                    if(min==0&&max==1){
                        JCheckBox cb = new JCheckBox();
                        cb.addActionListener((e) -> {
                            VRManager.configuration.pimax.intSettings.put(config.key, cb.isSelected()?1:0);
                            VRManager.saveConfig();
                            refresh();
                        });
                        bools.put(config, cb);
                        panel.add(BorderLayout.CENTER, cb);
                    }else if(max-min<5){
                        ButtonGroup group = new ButtonGroup();
                        JPanel row = new JPanel(new GridLayout(1, 0));
                        JRadioButton[] buttons = new JRadioButton[max-min+1];
                        for(int i = min; i<=max; i++){
                            int val = i;
                            group.add(buttons[i-min] = new JRadioButton(i+""));
                            buttons[i-min].addActionListener((e) -> {
                                VRManager.configuration.pimax.intSettings.put(config.key, val);
                                VRManager.saveConfig();
                                refresh();
                            });
                            row.add(buttons[i-min]);
                        }
                        smallInts.put(config, buttons);
                        smallIntGroups.put(config, group);
                        panel.add(BorderLayout.CENTER, row);
                    }else{
                        JSlider slider = new JSlider(min, max, min);
                        slider.addChangeListener((e) -> {
                            if(refreshing)return;
                            VRManager.configuration.pimax.intSettings.put(config.key, slider.getValue());
                            VRManager.saveConfig();
                            refresh();
                        });
                        ints.put(config, slider);
                        panel.add(BorderLayout.CENTER, slider);
                    }
                }
                case FLOAT -> {
                    int min = (int)(config.min*FLOAT_MULTIPLIER);
                    int max = (int)(config.max*FLOAT_MULTIPLIER);
                    JSlider slider = new JSlider(min, max, min);
                    slider.addChangeListener((e) -> {
                        if(refreshing)return;
                        VRManager.configuration.pimax.floatSettings.put(config.key, slider.getValue()/FLOAT_MULTIPLIER);
                        VRManager.saveConfig();
                        refresh();
                    });
                    floats.put(config, slider);
                    panel.add(BorderLayout.CENTER, slider);
                }
                case VECTOR3F -> {
                    JPanel row = new JPanel(new GridLayout(1, 0));
                    int min = (int)(config.min*FLOAT_MULTIPLIER);
                    int max = (int)(config.max*FLOAT_MULTIPLIER);
                    JSlider[] sliders = new JSlider[3];
                    for(int i = 0; i<3; i++){
                        int dim = i;
                        JSlider slider = sliders[i] = new JSlider(min, max, min);
                        var prefSize = slider.getPreferredSize();
                        prefSize.width /= 2;
                        slider.setPreferredSize(prefSize);
                        slider.addChangeListener((e) -> {
                            if(refreshing)return;
                            piVector3f vec = VRManager.configuration.pimax.vectorSettings.getOrDefault(config.key, new piVector3f());
                            if(dim==0)vec.x = slider.getValue()/FLOAT_MULTIPLIER;
                            if(dim==1)vec.y = slider.getValue()/FLOAT_MULTIPLIER;
                            if(dim==2)vec.z = slider.getValue()/FLOAT_MULTIPLIER;
                            VRManager.configuration.pimax.vectorSettings.put(config.key, vec);
                            VRManager.saveConfig();
                            refresh();
                        });
                        row.add(slider);
                    }
                    vectors.put(config, sliders);
                    panel.add(BorderLayout.CENTER, row);
                }
            }
            panelKnownSettings.add(panel);
        }
        refresh();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        boxUsePimaxClient = new javax.swing.JCheckBox();
        boxForceReboot = new javax.swing.JCheckBox();
        boxStartSteamVR = new javax.swing.JCheckBox();
        boxUsePimaxClientShutdown = new javax.swing.JCheckBox();
        boxWatchUSB = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelKnownSettings = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        buttonAddSetting = new javax.swing.JButton();
        buttonRemoveSetting = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listSettings = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pimax Configuration");
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Initialization Settings");
        jPanel1.add(jLabel1);

        boxUsePimaxClient.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        boxUsePimaxClient.setText("Use Pimax Client");
        boxUsePimaxClient.setToolTipText("Should the pimax play client be run on startup? This is not neccesary, and will reduce the reliability of any RPC calls by VR Manager. (You can always start it later)");
        boxUsePimaxClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxUsePimaxClientActionPerformed(evt);
            }
        });
        jPanel1.add(boxUsePimaxClient);

        boxForceReboot.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        boxForceReboot.setText("Force Reboot");
        boxForceReboot.setToolTipText("Should the headset be rebooted during initialization? This fixes many random issues, but makes initialization take longer.");
        boxForceReboot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxForceRebootActionPerformed(evt);
            }
        });
        jPanel1.add(boxForceReboot);

        boxStartSteamVR.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        boxStartSteamVR.setText("Start SteamVR");
        boxStartSteamVR.setToolTipText("Should SteamVR be started once pimax initialization is complete?");
        boxStartSteamVR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxStartSteamVRActionPerformed(evt);
            }
        });
        jPanel1.add(boxStartSteamVR);

        boxUsePimaxClientShutdown.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        boxUsePimaxClientShutdown.setText("Use Pimax Client for Shutdown");
        boxUsePimaxClientShutdown.setToolTipText("Should the pimax client be started for shutdown?\nThis helps ensure a clean shutdown, as the pimax service doesn't like to shut down without it.\nNote: If this doesn't work, make sure pimax play is up to date and you are logged in.");
        boxUsePimaxClientShutdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxUsePimaxClientShutdownActionPerformed(evt);
            }
        });
        jPanel1.add(boxUsePimaxClientShutdown);

        boxWatchUSB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        boxWatchUSB.setText("Watch USB Devices");
        boxWatchUSB.setToolTipText("This registers known pimax USB devices to be watched by the USB module. This does nothing if the USB module is not active.");
        boxWatchUSB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxWatchUSBActionPerformed(evt);
            }
        });
        jPanel1.add(boxWatchUSB);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Known Headset Settings");
        jPanel1.add(jLabel3);

        jPanel3.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelKnownSettings.setLayout(new java.awt.GridLayout(0, 1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 256)); // NOI18N
        jLabel4.setText("        ");
        panelKnownSettings.add(jLabel4);

        jScrollPane2.setViewportView(panelKnownSettings);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Raw Headset Settings");
        jPanel2.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        buttonAddSetting.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonAddSetting.setText("Add Custom Setting");
        buttonAddSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddSettingActionPerformed(evt);
            }
        });
        jPanel6.add(buttonAddSetting);

        buttonRemoveSetting.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonRemoveSetting.setText("Remove Setting");
        buttonRemoveSetting.setEnabled(false);
        buttonRemoveSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSettingActionPerformed(evt);
            }
        });
        jPanel6.add(buttonRemoveSetting);

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        listSettings.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listSettings.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listSettingsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listSettings);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void boxUsePimaxClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxUsePimaxClientActionPerformed
        VRManager.configuration.pimax.usePimaxClient = boxUsePimaxClient.isSelected();
        VRManager.saveConfig();
    }//GEN-LAST:event_boxUsePimaxClientActionPerformed
    private void boxForceRebootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxForceRebootActionPerformed
        VRManager.configuration.pimax.forceReboot = boxForceReboot.isSelected();
        VRManager.saveConfig();
    }//GEN-LAST:event_boxForceRebootActionPerformed
    private void boxStartSteamVRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxStartSteamVRActionPerformed
        VRManager.configuration.pimax.startSteamVR = boxStartSteamVR.isSelected();
        VRManager.saveConfig();
    }//GEN-LAST:event_boxStartSteamVRActionPerformed
    private void boxUsePimaxClientShutdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxUsePimaxClientShutdownActionPerformed
        VRManager.configuration.pimax.usePimaxClientForShutdown = boxUsePimaxClientShutdown.isSelected();
        VRManager.saveConfig();
    }//GEN-LAST:event_boxUsePimaxClientShutdownActionPerformed
    private void boxWatchUSBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxWatchUSBActionPerformed
        VRManager.configuration.pimax.watchUSBDevices = boxWatchUSB.isSelected();
        VRManager.saveConfig();
    }//GEN-LAST:event_boxWatchUSBActionPerformed
    private void buttonAddSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddSettingActionPerformed
        String key = JOptionPane.showInputDialog(this, "Enter the setting key", "Add custom setting", JOptionPane.PLAIN_MESSAGE);
        if(key==null)return;
        String[] options = new String[]{"Integer", "Float", "String", "Vector3f"};
        int type = JOptionPane.showOptionDialog(this, "Choose the data type for "+key, "Add custom setting", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if(type<0)return;
        try{
            switch(type){
                case 0 -> {
                    var val = JOptionPane.showInputDialog(this, "Enter the integer value for "+key, "Add custom setting (int)", JOptionPane.PLAIN_MESSAGE);
                    if(val==null)return;
                    VRManager.configuration.pimax.intSettings.put(key, Integer.valueOf(val));
                }
                case 1 -> {
                    var val = JOptionPane.showInputDialog(this, "Enter the float value for "+key, "Add custom setting (float)", JOptionPane.PLAIN_MESSAGE);
                    if(val==null)return;
                    VRManager.configuration.pimax.floatSettings.put(key, Float.valueOf(val));
                }
                case 2 -> {
                    var val = JOptionPane.showInputDialog(this, "Enter the string value for "+key, "Add custom setting (String)", JOptionPane.PLAIN_MESSAGE);
                    if(val==null)return;
                    VRManager.configuration.pimax.stringSettings.put(key, val);
                }
                case 3 -> {
                    var x = JOptionPane.showInputDialog(this, "Enter the first float value for "+key, "Add custom setting (Vector3f", JOptionPane.PLAIN_MESSAGE);
                    if(x==null)return;
                    var y = JOptionPane.showInputDialog(this, "Enter the second float value for "+key, "Add custom setting (Vector3f", JOptionPane.PLAIN_MESSAGE);
                    if(y==null)return;
                    var z = JOptionPane.showInputDialog(this, "Enter the third float value for "+key, "Add custom setting (Vector3f", JOptionPane.PLAIN_MESSAGE);
                    if(z==null)return;
                    VRManager.configuration.pimax.vectorSettings.put(key, new piVector3f(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z)));
                }
                default -> {
                    Logger.error("Invalid setting type: "+type+"!");
                    return;
                }
            }
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Entered value is not a valid "+(type==0?"integer":"float"), "Invalid value", JOptionPane.ERROR_MESSAGE);
        }
        VRManager.saveConfig();
        refresh();
    }//GEN-LAST:event_buttonAddSettingActionPerformed
    private void buttonRemoveSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveSettingActionPerformed
        String key = listSettings.getSelectedValue().split(" = ")[0];
        VRManager.configuration.pimax.intSettings.remove(key);
        VRManager.configuration.pimax.floatSettings.remove(key);
        VRManager.configuration.pimax.stringSettings.remove(key);
        VRManager.configuration.pimax.vectorSettings.remove(key);
        VRManager.saveConfig();
        refresh();
    }//GEN-LAST:event_buttonRemoveSettingActionPerformed
    private void listSettingsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listSettingsValueChanged
        buttonRemoveSetting.setEnabled(listSettings.getSelectedIndex()>=0);
    }//GEN-LAST:event_listSettingsValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox boxForceReboot;
    private javax.swing.JCheckBox boxStartSteamVR;
    private javax.swing.JCheckBox boxUsePimaxClient;
    private javax.swing.JCheckBox boxUsePimaxClientShutdown;
    private javax.swing.JCheckBox boxWatchUSB;
    private javax.swing.JButton buttonAddSetting;
    private javax.swing.JButton buttonRemoveSetting;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listSettings;
    private javax.swing.JPanel panelKnownSettings;
    // End of variables declaration//GEN-END:variables
    private void refresh(){
        refreshing = true;
        boxForceReboot.setSelected(VRManager.configuration.pimax.forceReboot);
        boxStartSteamVR.setSelected(VRManager.configuration.pimax.startSteamVR);
        boxUsePimaxClient.setSelected(VRManager.configuration.pimax.usePimaxClient);
        boxUsePimaxClientShutdown.setSelected(VRManager.configuration.pimax.usePimaxClientForShutdown);
        boxWatchUSB.setSelected(VRManager.configuration.pimax.watchUSBDevices);
        var listModel = new DefaultListModel<String>();
        ArrayList<String> settings = new ArrayList<>();
        settings.addAll(VRManager.configuration.pimax.intSettings.keySet());
        settings.addAll(VRManager.configuration.pimax.floatSettings.keySet());
        settings.addAll(VRManager.configuration.pimax.stringSettings.keySet());
        settings.addAll(VRManager.configuration.pimax.vectorSettings.keySet());
        for(var key : settings){
            Object value = VRManager.configuration.pimax.intSettings.get(key);
            if(value==null)value = VRManager.configuration.pimax.floatSettings.get(key);
            if(value==null)value = VRManager.configuration.pimax.stringSettings.get(key);
            if(value==null)value = VRManager.configuration.pimax.vectorSettings.get(key);
            //no setting should ever be duplicated, so this should be fine.
            listModel.addElement(key+" = "+value.toString());
        }
        for(var key : bools.keySet()){
            Integer val = VRManager.configuration.pimax.intSettings.get(key.key);
            var cb = bools.get(key);
            cb.getParent().setBackground(val==null?Color.lightGray:Color.green.darker());
            cb.setBackground(val==null?Color.lightGray:Color.green.darker());
            cb.setSelected(val==null?false:val>0);
        }
        for(var key : smallInts.keySet()){
            Integer val = VRManager.configuration.pimax.intSettings.get(key.key);
            JRadioButton[] buttons = smallInts.get(key);
            int min = (int)key.min;
            int max = (int)key.max;
            for(int i = min; i<=max; i++){
                int v = i;
                buttons[i-min].setSelected(val!=null&&val==v);
                buttons[i-min].setBackground(val==null?Color.lightGray:Color.green.darker());
            }
            if(val==null)smallIntGroups.get(key).clearSelection();
            buttons[0].getParent().setBackground(val==null?Color.lightGray:Color.green.darker());
            buttons[0].getParent().getParent().setBackground(val==null?Color.lightGray:Color.green.darker());
        }
        for(var key : ints.keySet()){
            Integer val = VRManager.configuration.pimax.intSettings.get(key.key);
            var slider = ints.get(key);
            slider.getParent().setBackground(val==null?Color.lightGray:Color.green.darker());
            slider.setBackground(val==null?Color.lightGray:Color.green.darker());
            slider.setValue(val==null?ints.get(key).getMinimum():val);
        }
        for(var key : floats.keySet()){
            Float val = VRManager.configuration.pimax.floatSettings.get(key.key);
            var slider = floats.get(key);
            slider.getParent().setBackground(val==null?Color.lightGray:Color.green.darker());
            slider.setBackground(val==null?Color.lightGray:Color.green.darker());
            slider.setValue((int)((val==null?floats.get(key).getMinimum():val)*FLOAT_MULTIPLIER));
        }
        for(var key : vectors.keySet()){
            piVector3f val = VRManager.configuration.pimax.vectorSettings.get(key.key);
            var sliders = vectors.get(key);
            for(int i = 0; i<3; i++){
                if(val!=null){
                    float v = 0;
                    if(i==0)v = val.x;
                    if(i==1)v = val.y;
                    if(i==2)v = val.z;
                    sliders[i].setValue((int)(v*FLOAT_MULTIPLIER));
                }else
                    sliders[i].setValue(Math.max(Math.min(sliders[i].getMaximum(), 0), sliders[i].getMinimum()));
                sliders[i].setBackground(val==null?Color.lightGray:Color.green.darker());
            }
            sliders[0].getParent().setBackground(val==null?Color.lightGray:Color.green.darker());
            sliders[0].getParent().getParent().setBackground(val==null?Color.lightGray:Color.green.darker());
        }
        listSettings.setModel(listModel);
        refreshing = false;
    }
}
