package gui;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

public class WeeklyReport extends javax.swing.JPanel {

    private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

    public WeeklyReport() {
        initComponents();
        setUpBg();
    }

    private void setUpBg(){
        jDateChooser1.setDate(null);
    }
    private void generateWeeklyReport(String reportDate) {

        try {
            JasperCompileManager.compileReport("/Report/weekly_report.jrxml");
        } catch (JRException ex) {
            System.out.println("model.ReportPanel.generateReport()");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        LocalDate currentDate = LocalDate.parse(reportDate, dtf);
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        String query = "SELECT * FROM `invoice` INNER JOIN `invoice_item` ON `invoice`.`id`=`invoice_item`.`invoice_id` "
                + "INNER JOIN `stock` ON `stock`.`id`=`invoice_item`.`stock_id` "
                + "INNER JOIN `product` ON `product`.`id`=`stock`.`product_id` "
                + "INNER JOIN `user` ON `user`.`id`=`invoice`.`user_id` "
                + "INNER JOIN `category` ON `category`.`id`=`product`.`category_id` "
                + "WHERE `invoice`.`date` BETWEEN ? AND ? ORDER BY `invoice`.`id` ASC";

        String bestSellingProduct = "", bestSellingProductCategory = "", invoiceId = "";
        int bestSellingProductQty = 0, transactionCount = 0;
        double total = 0.0;

        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setRowCount(0);
        try {
            ResultSet rs = MySQL.executeSearch(query, this.sdf.format(startOfWeek), this.sdf.format(endOfWeek));

            while (rs.next()) {
                Vector<String> v = new Vector();

                v.add(rs.getString("invoice.id"));
                v.add(rs.getString("product.name"));
                v.add(rs.getString("user.fname"));
                v.add(rs.getString("invoice_item.qty"));
                v.add(rs.getString("stock.selling_price"));
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
            Logger.getLogger(ReportPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("numberOfDays", "[07] " + startOfWeek + " " + endOfWeek);
        parameters.put("Employee", "All");
        parameters.put("Transactions", String.valueOf(transactionCount));
        parameters.put("Total", String.valueOf(total));
        parameters.put("BestSellingProduct", bestSellingProduct);
        parameters.put("BestSellingCategory", bestSellingProductCategory);

        parameters.put("day_01_date", String.valueOf(startOfWeek));
        parameters.put("day_02_date", String.valueOf(startOfWeek.plusDays(1)));
        parameters.put("day_03_date", String.valueOf(startOfWeek.plusDays(2)));
        parameters.put("day_04_date", String.valueOf(startOfWeek.plusDays(3)));
        parameters.put("day_05_date", String.valueOf(startOfWeek.plusDays(4)));
        parameters.put("day_06_date", String.valueOf(startOfWeek.plusDays(5)));
        parameters.put("day_07_date", String.valueOf(endOfWeek));

        int[] dayTransactions = new int[7];
        double[] dayIncomes = new double[7];

        for (int i = 0; i < 7; i++) {
            String queryForDay = "SELECT * FROM `invoice` WHERE `invoice`.`date`=?";
            try {
                ResultSet rs = MySQL.executeSearch(queryForDay, this.sdf.format(startOfWeek.plusDays(i)));
                while (rs.next()) {
                    dayIncomes[i] = rs.getDouble("invoice.paid_amount");
                    dayTransactions[i] = rs.getInt("tcount");
                }

            } catch (Exception ex) {
                Logger.getLogger(ReportPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        parameters.put("day_01_transactions", String.valueOf(dayTransactions[0]));
        parameters.put("day_02_transactions", String.valueOf(dayTransactions[1]));
        parameters.put("day_03_transactions", String.valueOf(dayTransactions[2]));
        parameters.put("day_04_transactions", String.valueOf(dayTransactions[3]));
        parameters.put("day_05_transactions", String.valueOf(dayTransactions[4]));
        parameters.put("day_06_transactions", String.valueOf(dayTransactions[5]));
        parameters.put("day_07_transactions", String.valueOf(dayTransactions[6]));

        parameters.put("Income_01", String.valueOf(dayIncomes[0]));
        parameters.put("Income_02", String.valueOf(dayIncomes[1]));
        parameters.put("Income_03", String.valueOf(dayIncomes[2]));
        parameters.put("Income_04", String.valueOf(dayIncomes[3]));
        parameters.put("Income_05", String.valueOf(dayIncomes[4]));
        parameters.put("Income_06", String.valueOf(dayIncomes[5]));
        parameters.put("Income_07", String.valueOf(dayIncomes[6]));

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

        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jDateChooser1.setDateFormatString("yyyy-MM-dd");

        jButton1.setText("Generate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Select Date Within Week :");

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Date reportDate = jDateChooser1.getDate();
        if (reportDate == null) {
            JOptionPane.showMessageDialog(this, "Please Select Date", "Missing Date", JOptionPane.WARNING_MESSAGE);
        } else if (reportDate.after(new Date())) {
            JOptionPane.showMessageDialog(this, "Please Select Valid Date", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        } else {
            generateWeeklyReport(sdf.format(reportDate));
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
