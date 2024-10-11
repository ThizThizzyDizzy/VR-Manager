package com.thizthizzydizzy.vrmanager.gui;
import com.thizthizzydizzy.vrmanager.Logger;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.config.Configuration;
import com.thizthizzydizzy.vrmanager.module.VRModule;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
public class ManagerGUI extends javax.swing.JFrame{
    private static ManagerGUI instance;
    private Configuration generatedConfig;
    public ManagerGUI(){
        initComponents();
        refreshModules();
        if(!new File("config.json").exists()){
            ((CardLayout)panelRoot.getLayout()).show(panelRoot, "autoconfig");
        }
    }
    public void refreshModules(){
        panelModulesList.removeAll();
        ArrayList<VRModule> modules = new ArrayList<>(Arrays.asList(VRModule.modules));
        //put enabled modules up front, in order
        int i = 0;
        for(var m : VRManager.configuration.modules){
            for(VRModule mod : VRModule.modules){
                if(mod.getName().equals(m)){
                    modules.remove(mod);
                    modules.add(i++, mod);
                }
            }
        }
        //build the UI
        for(VRModule module : modules){
            JPanel row = new JPanel(new GridLayout(1, 0));
            var bg = new JPanel();
            var label = new JLabel(module.getName());
            bg.add(label);
            label.setFont(labelModules.getFont());
            bg.setBackground(VRManager.configuration.modules.contains(module.getName())?Color.green.darker():Color.red.darker());
            row.add(bg);
            if(module.hasConfiguration()){
                var configure = new JButton("Configure");
                configure.setFont(labelModules.getFont());
                configure.addActionListener((e) -> {
                    var dialog = module.getConfigurationGUI(this);
                    dialog.addWindowListener(new WindowAdapter(){
                        @Override
                        public void windowClosing(WindowEvent e){
                            VRManager.saveConfig();
                        }
                    });
                    dialog.setVisible(true);
                });
                row.add(configure);
            }else{
                var blankLabel = new JLabel();
                row.add(blankLabel);
            }
            var runOnStart = new JCheckBox("Run on initialization");
            runOnStart.setSelected(VRManager.configuration.modules.contains(module.getName()));
            runOnStart.setFont(labelModules.getFont());
            runOnStart.addActionListener((e) -> {
                if(runOnStart.isSelected())VRManager.configuration.modules.add(module.getName());
                else
                    VRManager.configuration.modules.remove(module.getName());
                VRManager.saveConfig();
                refreshModules();
            });
            row.add(runOnStart);
            panelModulesList.add(row);
            panelModules.revalidate();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        panelRoot = new javax.swing.JPanel();
        panelMain = new javax.swing.JPanel();
        panelMainTabs = new javax.swing.JPanel();
        tabButtonMain = new javax.swing.JToggleButton();
        tabButtonConfigure = new javax.swing.JToggleButton();
        panelMainContent = new javax.swing.JPanel();
        panelDashboard = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        panelConfigure = new javax.swing.JPanel();
        panelModules = new javax.swing.JPanel();
        labelModules = new javax.swing.JLabel();
        scrollableModules = new javax.swing.JScrollPane();
        panelModulesListContainer = new javax.swing.JPanel();
        panelModulesList = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        panelAutoconfig = new javax.swing.JPanel();
        labelAutoconfigTitle = new javax.swing.JLabel();
        panelAutoconfigButtons = new javax.swing.JPanel();
        buttonAutoconfigSkip = new javax.swing.JButton();
        buttonAutoconfigConfirm = new javax.swing.JButton();
        scrollableAutoconfig = new javax.swing.JScrollPane();
        labelAutoconfig = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VR Manager");

        panelRoot.setLayout(new java.awt.CardLayout());

        panelMain.setLayout(new java.awt.BorderLayout());

        panelMainTabs.setLayout(new java.awt.GridLayout(1, 0));

        buttonGroup1.add(tabButtonMain);
        tabButtonMain.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        tabButtonMain.setSelected(true);
        tabButtonMain.setText("Dashboard");
        tabButtonMain.setMargin(new java.awt.Insets(12, 14, 13, 14));
        tabButtonMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabButtonMainActionPerformed(evt);
            }
        });
        panelMainTabs.add(tabButtonMain);

        buttonGroup1.add(tabButtonConfigure);
        tabButtonConfigure.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        tabButtonConfigure.setText("Configuration");
        tabButtonConfigure.setMargin(new java.awt.Insets(12, 14, 13, 14));
        tabButtonConfigure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabButtonConfigureActionPerformed(evt);
            }
        });
        panelMainTabs.add(tabButtonConfigure);

        panelMain.add(panelMainTabs, java.awt.BorderLayout.PAGE_START);

        panelMainContent.setLayout(new java.awt.CardLayout());

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jButton2.setText("START");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDashboardLayout = new javax.swing.GroupLayout(panelDashboard);
        panelDashboard.setLayout(panelDashboardLayout);
        panelDashboardLayout.setHorizontalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDashboardLayout.createSequentialGroup()
                .addGap(320, 320, 320)
                .addComponent(jButton2)
                .addContainerGap(366, Short.MAX_VALUE))
        );
        panelDashboardLayout.setVerticalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDashboardLayout.createSequentialGroup()
                .addContainerGap(379, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        panelMainContent.add(panelDashboard, "dashboard");

        panelConfigure.setLayout(new java.awt.BorderLayout());

        panelModules.setLayout(new java.awt.BorderLayout());

        labelModules.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        labelModules.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelModules.setText("Modules");
        panelModules.add(labelModules, java.awt.BorderLayout.PAGE_START);

        scrollableModules.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelModulesListContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelModulesListContainer.setLayout(new java.awt.BorderLayout());

        panelModulesList.setLayout(new java.awt.GridLayout(0, 1));
        panelModulesListContainer.add(panelModulesList, java.awt.BorderLayout.PAGE_START);

        scrollableModules.setViewportView(panelModulesListContainer);

        panelModules.add(scrollableModules, java.awt.BorderLayout.CENTER);

        panelConfigure.add(panelModules, java.awt.BorderLayout.CENTER);

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setText("Auto-config");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        panelConfigure.add(jButton1, java.awt.BorderLayout.PAGE_END);

        panelMainContent.add(panelConfigure, "configure");

        panelMain.add(panelMainContent, java.awt.BorderLayout.CENTER);

        panelRoot.add(panelMain, "main");

        panelAutoconfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panelAutoconfig.setLayout(new java.awt.BorderLayout());

        labelAutoconfigTitle.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        labelAutoconfigTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelAutoconfigTitle.setText("Welcome to VR Manager!");
        panelAutoconfig.add(labelAutoconfigTitle, java.awt.BorderLayout.PAGE_START);

        panelAutoconfigButtons.setLayout(new java.awt.GridLayout(1, 0, 50, 50));

        buttonAutoconfigSkip.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        buttonAutoconfigSkip.setText("Manual Configuration");
        buttonAutoconfigSkip.setMargin(new java.awt.Insets(22, 14, 23, 14));
        buttonAutoconfigSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAutoconfigSkipActionPerformed(evt);
            }
        });
        panelAutoconfigButtons.add(buttonAutoconfigSkip);

        buttonAutoconfigConfirm.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        buttonAutoconfigConfirm.setText("Auto-Configure");
        buttonAutoconfigConfirm.setMargin(new java.awt.Insets(22, 14, 23, 14));
        buttonAutoconfigConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAutoconfigConfirmActionPerformed(evt);
            }
        });
        panelAutoconfigButtons.add(buttonAutoconfigConfirm);

        panelAutoconfig.add(panelAutoconfigButtons, java.awt.BorderLayout.PAGE_END);

        scrollableAutoconfig.setBorder(null);

        labelAutoconfig.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        labelAutoconfig.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelAutoconfig.setText("""
            <html>
            <div style="text-align: center; text-height: 240px">
            Would you like to automatically configure VR Manager?
            <br>
            <br>
            This will attempt to auto-detect installed software and configure the reccommended settings for them in VR Manager.
            <br>
            <br>
            Standalone software, such as simple OSC programs, must be located in the same folder as VR Manager, or in a subfolder.
            </div>
            </html>
            """);
            scrollableAutoconfig.setViewportView(labelAutoconfig);

            panelAutoconfig.add(scrollableAutoconfig, java.awt.BorderLayout.CENTER);

            panelRoot.add(panelAutoconfig, "autoconfig");

            getContentPane().add(panelRoot, java.awt.BorderLayout.CENTER);

            pack();
        }// </editor-fold>//GEN-END:initComponents
    private void buttonAutoconfigConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAutoconfigConfirmActionPerformed
        if(generatedConfig==null){
            labelAutoconfig.setHorizontalAlignment(SwingConstants.LEFT);
            labelAutoconfig.setVerticalAlignment(SwingConstants.TOP);
            buttonAutoconfigSkip.setEnabled(false);
            buttonAutoconfigConfirm.setEnabled(false);
            labelAutoconfig.setText("<html>");
            generatedConfig = VRManager.autoConfig((str) -> labelAutoconfig.setText(labelAutoconfig.getText()+"\n<br>"+str));
            buttonAutoconfigConfirm.setText("Use Configuration");
            buttonAutoconfigSkip.setText("Discard Configuration");
            buttonAutoconfigSkip.setEnabled(true);
            buttonAutoconfigConfirm.setEnabled(true);
        }else{
            labelAutoconfig.setHorizontalAlignment(SwingConstants.CENTER);
            labelAutoconfig.setVerticalAlignment(SwingConstants.CENTER);
            VRManager.configuration = generatedConfig;
            VRManager.saveConfig();
            generatedConfig = null;
            ((CardLayout)panelRoot.getLayout()).show(panelRoot, "main");
            ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "configure");
            tabButtonConfigure.setSelected(true);
            refreshModules();
        }
    }//GEN-LAST:event_buttonAutoconfigConfirmActionPerformed
    private void buttonAutoconfigSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAutoconfigSkipActionPerformed
        labelAutoconfig.setHorizontalAlignment(SwingConstants.CENTER);
        labelAutoconfig.setVerticalAlignment(SwingConstants.CENTER);
        generatedConfig = null;
        ((CardLayout)panelRoot.getLayout()).show(panelRoot, "main");
        ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "configure");
        tabButtonConfigure.setSelected(true);
    }//GEN-LAST:event_buttonAutoconfigSkipActionPerformed
    private void tabButtonMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabButtonMainActionPerformed
        ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "dashboard");
    }//GEN-LAST:event_tabButtonMainActionPerformed
    private void tabButtonConfigureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabButtonConfigureActionPerformed
        ((CardLayout)panelMainContent.getLayout()).show(panelMainContent, "configure");
    }//GEN-LAST:event_tabButtonConfigureActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Automatically generate configuration?\nWARNING: current settings will be lost!", "Autoconfig", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null)!=JOptionPane.YES_OPTION)return;
        VRManager.configuration = VRManager.autoConfig(null);
        VRManager.saveConfig();
        refreshModules();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        VRManager.init();
    }//GEN-LAST:event_jButton2ActionPerformed
    public static void start(){
        String[] preferredLookAndFeels = new String[]{"Windows", "Nimbus"};
        String[] classNames = new String[preferredLookAndFeels.length];
        for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
            for(int i = 0; i<preferredLookAndFeels.length; i++){
                if(preferredLookAndFeels[i].equals(info.getName())){
                    classNames[i] = info.getClassName();
                }
            }
        }
        for(int i = 0; i<classNames.length; i++){
            if(classNames[i]!=null){
                try{
                    javax.swing.UIManager.setLookAndFeel(classNames[i]);
                    Logger.info("Loaded look and feel: "+preferredLookAndFeels[i]);
                }catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex){
                    Logger.warn("Failed to load look and feel: "+preferredLookAndFeels[i], ex);
                }
                break;
            }
        }
        java.awt.EventQueue.invokeLater(() -> {
            instance = new ManagerGUI();
            instance.setVisible(true);
        });
    }
    public static void stop(){
        if(instance!=null)instance.dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAutoconfigConfirm;
    private javax.swing.JButton buttonAutoconfigSkip;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel labelAutoconfig;
    private javax.swing.JLabel labelAutoconfigTitle;
    private javax.swing.JLabel labelModules;
    private javax.swing.JPanel panelAutoconfig;
    private javax.swing.JPanel panelAutoconfigButtons;
    private javax.swing.JPanel panelConfigure;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelMainContent;
    private javax.swing.JPanel panelMainTabs;
    private javax.swing.JPanel panelModules;
    private javax.swing.JPanel panelModulesList;
    private javax.swing.JPanel panelModulesListContainer;
    private javax.swing.JPanel panelRoot;
    private javax.swing.JScrollPane scrollableAutoconfig;
    private javax.swing.JScrollPane scrollableModules;
    private javax.swing.JToggleButton tabButtonConfigure;
    private javax.swing.JToggleButton tabButtonMain;
    // End of variables declaration//GEN-END:variables
}
