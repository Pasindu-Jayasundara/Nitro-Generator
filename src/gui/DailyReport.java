package gui;

import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import net.sf.jasperreports.engine.JRException;
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

        String bestSellingProduct = "", bestSellingProductCategory = "", invoiceId = "";
        int bestSellingProductQty = 0, transactionCount = 0;
        double total = 0.0;

        DefaultTableModel dtm = new DefaultTableModel();

        dtm.addColumn("Invoice_Id");
        dtm.addColumn("Item");
        dtm.addColumn("Cashier");
        dtm.addColumn("Qty");
        dtm.addColumn("Unit_Price");
        dtm.addColumn("Discount");
        dtm.addColumn("NetPrice");

        dtm.setRowCount(0);

        try {
            String query = "SELECT "
                    + "`invoice`.`id` AS `id`,"
                    + "`product`.`name` AS `name`,"
                    + "`user`.`fname` AS `fname`,"
                    + "`invoice_item`.`qty` AS `qty`,"
                    + "`stock`.`selling_price` AS `selling_price`,"
                    + "`category`.`category` AS `category`,"
                    + "`invoice`.`paid_amount` AS `paid_amount`"
                    + " FROM `invoice` INNER JOIN `invoice_item` ON `invoice`.`id`=`invoice_item`.`invoice_id` "
                    + "INNER JOIN `stock` ON `stock`.`id`=`invoice_item`.`stock_id` "
                    + "INNER JOIN `product` ON `product`.`id`=`stock`.`product_id` "
                    + "INNER JOIN `user` ON `user`.`id`=`invoice`.`user_id` "
                    + "INNER JOIN `category` ON `category`.`id`=`product`.`category_id` "
                    + "WHERE `invoice`.`date`='" + reportDate + "' ORDER BY `invoice`.`id` ASC";
            ResultSet rs = MySQL.execute(query);

            while (rs.next()) {
                Vector<String> v = new Vector<>();

                v.add(rs.getString("id"));
                v.add(rs.getString("name"));
                v.add(rs.getString("fname"));
                v.add(rs.getString("qty"));
                v.add(rs.getString("selling_price"));
                v.add("0.00");
                v.add(String.valueOf(rs.getDouble("selling_price") * rs.getInt("qty")));

                dtm.addRow(v);

                if (bestSellingProductQty < rs.getInt("qty")) {
                    bestSellingProductQty = rs.getInt("qty");
                    bestSellingProduct = rs.getString("name");
                    bestSellingProductCategory = rs.getString("category");
                }

                if (!invoiceId.equals(rs.getString("id"))) {
                    transactionCount++;
                }

                total += rs.getDouble("paid_amount");
            }

        } catch (Exception ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("numberOfDays", "01");
        parameters.put("Employee", "All");
        parameters.put("Transactions", String.valueOf(transactionCount));
        parameters.put("Total", String.valueOf(total));
        parameters.put("BestSellingProduct", bestSellingProduct);
        parameters.put("BestSellingCategory", bestSellingProductCategory);
        URL imagePathUrl = getClass().getResource("/images/");
        if (imagePathUrl != null) {
            String imagePath = imagePathUrl.toString();
            parameters.put("IMAGE_PATH", imagePath);
        } else {
            throw new RuntimeException("Image path not found");
        }

        JRTableModelDataSource tmd = new JRTableModelDataSource(dtm);

        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(getClass().getResourceAsStream("/report/daily_report.jasper"), parameters, tmd);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            System.out.println("Couldnot Load Data"+ex);
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
        } else if (reportDate.after(new Date())) {
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
