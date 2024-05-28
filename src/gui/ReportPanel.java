package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import java.sql.ResultSet;
import java.util.Vector;
import net.sf.jasperreports.view.JasperViewer;

public class ReportPanel extends javax.swing.JDialog {

    static int reportChoiceIndex;
    private final SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

    public ReportPanel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setUpBg();
    }

    private void setUpBg() {
        jPanel5.setVisible(false);
        monthlyReport1.setVisible(false);
        dailyReport1.setVisible(false);
        weeklyReport1.setVisible(false);
        sL_and_SM_and_TC_and_PL_Report1.setVisible(false);
        jButton1.setVisible(false);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMonthChooser2 = new com.toedter.calendar.JMonthChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        monthlyReport1 = new gui.MonthlyReport();
        dailyReport1 = new gui.DailyReport();
        weeklyReport1 = new gui.WeeklyReport();
        sL_and_SM_and_TC_and_PL_Report1 = new gui.SL_and_SM_and_TC_and_PL_Report();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("GENERATE REPORTS");

        jLabel2.setText("Select Report Type :");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Choose --", "Daily Report", "Weekly Report", "Monthly Report", "Stock Level Report", "Stock Movement Report", "Top Customers Report", "Finance Report" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jButton1.setText("Generate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jPanel5.setLayout(new java.awt.CardLayout());
        jPanel5.add(monthlyReport1, "card2");
        jPanel5.add(dailyReport1, "card3");
        jPanel5.add(weeklyReport1, "card4");
        jPanel5.add(sL_and_SM_and_TC_and_PL_Report1, "card5");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged

        reportChoiceIndex = jComboBox1.getSelectedIndex();
        switch (reportChoiceIndex) {
            case 1://daily
                jPanel5.setVisible(true);

                weeklyReport1.setVisible(false);
                monthlyReport1.setVisible(false);
                sL_and_SM_and_TC_and_PL_Report1.setVisible(false);
                jButton1.setVisible(false);
                dailyReport1.setVisible(true);
                break;
            case 2://weekly
                jPanel5.setVisible(true);

                dailyReport1.setVisible(false);
                monthlyReport1.setVisible(false);
                sL_and_SM_and_TC_and_PL_Report1.setVisible(false);
                jButton1.setVisible(false);
                weeklyReport1.setVisible(true);
                break;
            case 3://monthly
                jPanel5.setVisible(true);

                weeklyReport1.setVisible(false);
                dailyReport1.setVisible(false);
                sL_and_SM_and_TC_and_PL_Report1.setVisible(false);
                jButton1.setVisible(false);
                monthlyReport1.setVisible(true);
                break;
            case 4://stock level
            case 5://stock movement
            case 6://top customer
            case 7://finance
                jPanel5.setVisible(true);
                weeklyReport1.setVisible(false);
                dailyReport1.setVisible(false);
                monthlyReport1.setVisible(false);
                jButton1.setVisible(false);
                sL_and_SM_and_TC_and_PL_Report1.setVisible(true);
                break;
            case 8:
                jPanel5.setVisible(false);
                weeklyReport1.setVisible(false);
                dailyReport1.setVisible(false);
                monthlyReport1.setVisible(false);
                sL_and_SM_and_TC_and_PL_Report1.setVisible(false);
                jButton1.setVisible(true);
                break;
            default:
                break;
        }

    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        int totalEmployee=0;
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setRowCount(0);
        
        String query = "SELECT * FROM `user` INNER JOIN `type` ON `user`.`user_type_id`=`type`.`id` "
                + "INNER JOIN `status` ON `user`.`status_id`=`status`.`id` "
                + "ORDER BY `user`.`id` ASC";
        try {
            ResultSet rs = MySQL.execute(query);
            
            while(rs.next()){
                
                Vector<String> v = new Vector();

                v.add(rs.getString("user.id"));
                v.add(rs.getString("user.fname"));
                v.add(rs.getString("user.lname"));
                v.add(rs.getString("user.mobile"));
                v.add(rs.getString("type.type"));
                v.add(rs.getString("status.status"));

                dtm.addRow(v);
                
                totalEmployee++;
            }
            
        } catch (Exception ex) {
            System.out.println("Couldnot Load Data"+ex);
        }
        
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("TotalEmployee", String.valueOf(totalEmployee));
        
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(getClass().getResourceAsStream("/Report/AllEmployeeReport.jasper"), parameters);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {

        FlatDarkLaf.setup();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReportPanel dialog = new ReportPanel(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gui.DailyReport dailyReport1;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private com.toedter.calendar.JMonthChooser jMonthChooser2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private gui.MonthlyReport monthlyReport1;
    private gui.SL_and_SM_and_TC_and_PL_Report sL_and_SM_and_TC_and_PL_Report1;
    private gui.WeeklyReport weeklyReport1;
    // End of variables declaration//GEN-END:variables
}
