package com.thizthizzydizzy.vrmanager.gui.module;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.config.module.ProcessManagerConfiguration;
import java.util.Arrays;
import javax.swing.DefaultListModel;
public class ConfigureProcessManagerGUI extends javax.swing.JDialog{
    private ProcessManagerConfiguration.ProcessConfiguration selectedProcess;
    public ConfigureProcessManagerGUI(java.awt.Frame parent){
        super(parent, true);
        initComponents();
        panelSettings.setVisible(false);
        refreshList();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        buttonAddProcess = new javax.swing.JButton();
        buttonRemoveProcess = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listProcesses = new javax.swing.JList<>();
        panelSettings = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        textFieldTitle = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        textFieldTarget = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        textFieldStartLocation = new javax.swing.JTextField();
        checkBoxStartIndirect = new javax.swing.JCheckBox();
        checkBoxForceShutdown = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        textFieldLaunchArguments = new javax.swing.JTextField();
        checkBoxIsWindowsApp = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        textFieldExeName = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Process Manager Configuration");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Processes");
        jPanel2.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        buttonAddProcess.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonAddProcess.setText("Add Process");
        buttonAddProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddProcessActionPerformed(evt);
            }
        });
        jPanel6.add(buttonAddProcess);

        buttonRemoveProcess.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonRemoveProcess.setText("Remove Process");
        buttonRemoveProcess.setEnabled(false);
        buttonRemoveProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveProcessActionPerformed(evt);
            }
        });
        jPanel6.add(buttonRemoveProcess);

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        listProcesses.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        listProcesses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listProcessesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listProcesses);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.WEST);

        panelSettings.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Process Settings");
        panelSettings.add(jLabel1);

        jPanel4.setLayout(new java.awt.BorderLayout());

        textFieldTitle.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel4.add(textFieldTitle, java.awt.BorderLayout.CENTER);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText(" Title ");
        jPanel4.add(jLabel3, java.awt.BorderLayout.LINE_START);

        panelSettings.add(jPanel4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText(" Target ");
        jPanel5.add(jLabel7, java.awt.BorderLayout.LINE_START);

        textFieldTarget.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        textFieldTarget.setText("                                                                                                                        ");
        jPanel5.add(textFieldTarget, java.awt.BorderLayout.CENTER);

        panelSettings.add(jPanel5);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText(" Start Location ");
        jPanel7.add(jLabel6, java.awt.BorderLayout.LINE_START);

        textFieldStartLocation.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel7.add(textFieldStartLocation, java.awt.BorderLayout.CENTER);

        panelSettings.add(jPanel7);

        checkBoxStartIndirect.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        checkBoxStartIndirect.setText("Start Indirectly");
        panelSettings.add(checkBoxStartIndirect);

        checkBoxForceShutdown.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        checkBoxForceShutdown.setText("Force Shutdown");
        panelSettings.add(checkBoxForceShutdown);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText(" Launch Arguments ");
        jPanel8.add(jLabel5, java.awt.BorderLayout.LINE_START);

        textFieldLaunchArguments.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        textFieldLaunchArguments.setText("                                          ");
        jPanel8.add(textFieldLaunchArguments, java.awt.BorderLayout.CENTER);

        panelSettings.add(jPanel8);

        checkBoxIsWindowsApp.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        checkBoxIsWindowsApp.setText("Is Windows App");
        panelSettings.add(checkBoxIsWindowsApp);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText(" exe Name ");
        jPanel9.add(jLabel4, java.awt.BorderLayout.LINE_START);

        textFieldExeName.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel9.add(textFieldExeName, java.awt.BorderLayout.CENTER);

        panelSettings.add(jPanel9);

        getContentPane().add(panelSettings, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void buttonAddProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddProcessActionPerformed
        VRManager.configuration.processManager.processes.add(new ProcessManagerConfiguration.ProcessConfiguration());
        VRManager.saveConfig();
        refreshList();
    }//GEN-LAST:event_buttonAddProcessActionPerformed
    private void buttonRemoveProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveProcessActionPerformed
        VRManager.configuration.processManager.processes.remove(listProcesses.getSelectedIndex());
        VRManager.saveConfig();
        refreshList();
    }//GEN-LAST:event_buttonRemoveProcessActionPerformed
    private void listProcessesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listProcessesValueChanged
        saveSettings();
        selectedProcess = listProcesses.getSelectedIndex()<0?null:VRManager.configuration.processManager.processes.get(listProcesses.getSelectedIndex());
        buttonRemoveProcess.setEnabled(selectedProcess!=null);
        refresh();
    }//GEN-LAST:event_listProcessesValueChanged
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        saveSettings();
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddProcess;
    private javax.swing.JButton buttonRemoveProcess;
    private javax.swing.JCheckBox checkBoxForceShutdown;
    private javax.swing.JCheckBox checkBoxIsWindowsApp;
    private javax.swing.JCheckBox checkBoxStartIndirect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> listProcesses;
    private javax.swing.JPanel panelSettings;
    private javax.swing.JTextField textFieldExeName;
    private javax.swing.JTextField textFieldLaunchArguments;
    private javax.swing.JTextField textFieldStartLocation;
    private javax.swing.JTextField textFieldTarget;
    private javax.swing.JTextField textFieldTitle;
    // End of variables declaration//GEN-END:variables
    private void saveSettings(){
        if(selectedProcess==null)return;
        String s = selectedProcess.toString();
        selectedProcess.title = null;
        if(!textFieldTitle.getText().isBlank())selectedProcess.title = textFieldTitle.getText();
        selectedProcess.target = null;
        if(!textFieldTarget.getText().isBlank())selectedProcess.target = textFieldTarget.getText();
        selectedProcess.startLocation = null;
        if(!textFieldStartLocation.getText().isBlank())selectedProcess.startLocation = textFieldStartLocation.getText();
        selectedProcess.startIndirect = checkBoxStartIndirect.isSelected();
        selectedProcess.forceShutdown = checkBoxForceShutdown.isSelected();
        selectedProcess.arguments.clear();
        if(!textFieldLaunchArguments.getText().isBlank())selectedProcess.arguments.addAll(Arrays.asList(textFieldLaunchArguments.getText().replaceAll("(?:(?<= |^)(?:([^\\\" ]+)|\\\"(.+?)\\\")(?: |$))", "$1$2\n").split("\n")));
        selectedProcess.isWindowsApp = checkBoxIsWindowsApp.isSelected();
        selectedProcess.exeName = null;
        if(!textFieldExeName.getText().isBlank())selectedProcess.exeName = textFieldExeName.getText();
        if(!selectedProcess.toString().equals(s))refreshList();//fix the name
    }
    private void refreshList(){
        var listModel = new DefaultListModel<String>();
        for(var process : VRManager.configuration.processManager.processes){
            listModel.addElement(process.toString());
        }
        listProcesses.setModel(listModel);
    }
    private void refresh(){
        panelSettings.setVisible(selectedProcess!=null);
        if(selectedProcess==null)return;
        textFieldTitle.setText(selectedProcess.title);
        textFieldTarget.setText(selectedProcess.target);
        textFieldStartLocation.setText(selectedProcess.startLocation);
        checkBoxStartIndirect.setSelected(selectedProcess.startIndirect);
        checkBoxForceShutdown.setSelected(selectedProcess.forceShutdown);
        String args = "";
        for(String s : selectedProcess.arguments){
            if(s.contains(" "))s = "\""+s+"\"";
            args += " "+s;
        }
        textFieldLaunchArguments.setText(args.trim());
        checkBoxIsWindowsApp.setSelected(selectedProcess.isWindowsApp);
        textFieldExeName.setText(selectedProcess.exeName);
    }
}
