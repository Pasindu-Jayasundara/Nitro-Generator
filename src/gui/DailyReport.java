package gui;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class DailyReport extends javax.swing.JPanel {

    private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

    public DailyReport() {
        initComponents();
        setUpBg();
    }

    private void setUpBg() {
        jLabel1.setVisible(true);
        jDateChooser1.setVisible(true);
        jButton1.setVisible(true);
        jDateChooser1.setDate(null);
    }

    private void generateDailyReport(String reportDate) {

        try {
            JasperCompileManager.compileReport("/Report/daily_report.jrxml");
        } catch (JRException ex) {
            System.out.println("model.ReportPanel.generateReport()");
        }

        String bestSellingProduct = "", bestSellingProductCategory = "", invoiceId = "";
        int bestSellingProductQty = 0, transactionCount = 0;
        double total = 0.0;

        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setRowCount(0);

        try {
            String query = "SELECT * FROM `invoice` INNER JOIN `invoice_item` ON `invoice`.`id`=`invoice_item`.`invoice_id` "
                    + "INNER JOIN `stock` ON `stock`.`id`=`invoice_item`.`stock_id` "
                    + "INNER JOIN `product` ON `product`.`id`=`stock`.`product_id` "
                    + "INNER JOIN `user` ON `user`.`id`=`invoice`.`user_id` "
                    + "INNER JOIN `category` ON `category`.`id`=`product`.`category_id` "
                    + "WHERE `invoice`.`date`=? ORDER BY `invoice`.`id` ASC";
            ResultSet rs = MySQL.executeSearch(query, reportDate);

            while (rs.next()) {
                Vector<String> v = new Vector();

                v.add(rs.getString("invoice.id"));
                v.add(rs.getString("product.name"));
                v.add(rs.getString("user.fname"));
                v.add(rs.getString("invoice_item.qty"));
                v.add(rs.getString("stock.selling_price"));
                v.add(rs.getString("0.00"));
                v.add(String.valueOf(rs.getDouble("stock.selling_price") * rs.getInt("invoice_item.qty")));

                dtm.addRow(v);

                if (bestSellingProductQty < rs.getInt("invoice_item.qty")) {
                    bestSellingProductQty = rs.getInt("invoice_item.qty");
                    bestSellingProduct = rs.getString("product.name");
                    bestSellingProductCategory = rs.getString("category.category");
                }

                if (!invoiceId.equals(rs.getString("invoice.id"))) {
                    transactionCount++;
                }

                total += rs.getDouble("invoice.paid_amount");
            }

        } catch (Exception ex) {
            System.out.println("gui.ReportPanel.generateReport()");
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("numberOfDays", "01");
        parameters.put("Employee", "All");
        parameters.put("Transactions", String.valueOf(transactionCount));
        parameters.put("Total", String.valueOf(total));
        parameters.put("BestSellingProduct", bestSellingProduct);
        parameters.put("BestSellingCategory", bestSellingProductCategory);

        JRTableModelDataSource tmd = new JRTableModelDataSource(dtm);

        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport("/Report/daily_report.jasper", parameters, tmd);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            Logger.getLogger(ReportPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();

        jLabel1.setText("Select Date :");

        jButton1.setText("Generate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jDateChooser1.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(7, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Date reportDate = jDateChooser1.getDate();
        if (reportDate == null) {
            JOptionPane.showMessageDialog(this, "Please Select Date", "Missing Date", JOptionPane.WARNING_MESSAGE);
        }else if (reportDate.after(new Date())) {
            JOptionPane.showMessageDialog(this, "Please Select Valid Date", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        } else {
            generateDailyReport(sdf.format(reportDate));
        }

    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
