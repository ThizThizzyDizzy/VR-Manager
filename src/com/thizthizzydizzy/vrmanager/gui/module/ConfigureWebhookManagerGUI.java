package com.thizthizzydizzy.vrmanager.gui.module;
import com.thizthizzydizzy.vrmanager.VRManager;
import com.thizthizzydizzy.vrmanager.config.module.WebhookManagerConfiguration;
import javax.swing.DefaultListModel;
public class ConfigureWebhookManagerGUI extends javax.swing.JDialog{
    private WebhookManagerConfiguration.WebhookConfiguration selectedWebhook;
    public ConfigureWebhookManagerGUI(java.awt.Frame parent){
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
        buttonAddWebhook = new javax.swing.JButton();
        buttonRemoveWebhook = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listWebhooks = new javax.swing.JList<>();
        panelSettings = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        textFieldTitle = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        textFieldStartup = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        textFieldShutdown = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Webhook Manager Configuration");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Webhooks");
        jPanel2.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        buttonAddWebhook.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonAddWebhook.setText("Add Webhook");
        buttonAddWebhook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddWebhookActionPerformed(evt);
            }
        });
        jPanel6.add(buttonAddWebhook);

        buttonRemoveWebhook.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buttonRemoveWebhook.setText("Remove Webhook");
        buttonRemoveWebhook.setEnabled(false);
        buttonRemoveWebhook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveWebhookActionPerformed(evt);
            }
        });
        jPanel6.add(buttonRemoveWebhook);

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        listWebhooks.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        listWebhooks.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listWebhooksValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listWebhooks);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.WEST);

        panelSettings.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Webhook Settings");
        panelSettings.add(jLabel1);

        jPanel7.setLayout(new java.awt.BorderLayout());

        textFieldTitle.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel7.add(textFieldTitle, java.awt.BorderLayout.CENTER);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText(" Title ");
        jPanel7.add(jLabel4, java.awt.BorderLayout.LINE_START);

        panelSettings.add(jPanel7);

        jPanel4.setLayout(new java.awt.BorderLayout());

        textFieldStartup.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel4.add(textFieldStartup, java.awt.BorderLayout.CENTER);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText(" Startup ");
        jPanel4.add(jLabel3, java.awt.BorderLayout.LINE_START);

        panelSettings.add(jPanel4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText(" Shutdown ");
        jPanel5.add(jLabel7, java.awt.BorderLayout.LINE_START);

        textFieldShutdown.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        textFieldShutdown.setText("                                                                                                                        ");
        jPanel5.add(textFieldShutdown, java.awt.BorderLayout.CENTER);

        panelSettings.add(jPanel5);

        getContentPane().add(panelSettings, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void buttonAddWebhookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddWebhookActionPerformed
        VRManager.configuration.webhookManager.webhooks.add(new WebhookManagerConfiguration.WebhookConfiguration());
        VRManager.saveConfig();
        refreshList();
    }//GEN-LAST:event_buttonAddWebhookActionPerformed
    private void buttonRemoveWebhookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveWebhookActionPerformed
        VRManager.configuration.webhookManager.webhooks.remove(listWebhooks.getSelectedIndex());
        VRManager.saveConfig();
        refreshList();
    }//GEN-LAST:event_buttonRemoveWebhookActionPerformed
    private void listWebhooksValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listWebhooksValueChanged
        saveSettings();
        selectedWebhook = listWebhooks.getSelectedIndex()<0?null:VRManager.configuration.webhookManager.webhooks.get(listWebhooks.getSelectedIndex());
        buttonRemoveWebhook.setEnabled(selectedWebhook!=null);
        refresh();
    }//GEN-LAST:event_listWebhooksValueChanged
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        saveSettings();
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddWebhook;
    private javax.swing.JButton buttonRemoveWebhook;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> listWebhooks;
    private javax.swing.JPanel panelSettings;
    private javax.swing.JTextField textFieldShutdown;
    private javax.swing.JTextField textFieldStartup;
    private javax.swing.JTextField textFieldTitle;
    // End of variables declaration//GEN-END:variables
    private void saveSettings(){
        if(selectedWebhook==null)return;
        String s = selectedWebhook.toString();
        selectedWebhook.title = null;
        if(!textFieldTitle.getText().isBlank())selectedWebhook.title = textFieldTitle.getText();
        selectedWebhook.target = null;
        if(!textFieldStartup.getText().isBlank())selectedWebhook.target = textFieldStartup.getText();
        selectedWebhook.shutdownTarget = null;
        if(!textFieldShutdown.getText().isBlank())selectedWebhook.shutdownTarget = textFieldShutdown.getText();
        if(!selectedWebhook.toString().equals(s))refreshList();//fix the name
    }
    private void refreshList(){
        var listModel = new DefaultListModel<String>();
        for(var webhook : VRManager.configuration.webhookManager.webhooks){
            listModel.addElement(webhook.toString());
        }
        listWebhooks.setModel(listModel);
    }
    private void refresh(){
        panelSettings.setVisible(selectedWebhook!=null);
        if(selectedWebhook==null)return;
        textFieldTitle.setText(selectedWebhook.title);
        textFieldStartup.setText(selectedWebhook.target);
        textFieldShutdown.setText(selectedWebhook.shutdownTarget);
    }
}
