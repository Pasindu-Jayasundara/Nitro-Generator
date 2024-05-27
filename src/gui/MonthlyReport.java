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

public class MonthlyReport extends javax.swing.JPanel {

    private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

    public MonthlyReport() {
        initComponents();
        setUpBg();
    }

    private void setUpBg() {
        jMonthChooser1.setMonth(0);
        jYearChooser1.setYear(0);
    }

    private void generateMonthlyReport(String startDateOfMonth, String endDateOfMonth, String weekFromArr[], String weekToArr[]) {

        try {
            JasperCompileManager.compileReport("/Report/monthly_report.jrxml");
        } catch (JRException ex) {
            System.out.println("model.ReportPanel.generateReport()");
        }

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
            ResultSet rs = MySQL.executeSearch(query, startDateOfMonth, endDateOfMonth);

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
        parameters.put("numberOfDays", startDateOfMonth + " - " + endDateOfMonth);
        parameters.put("Employee", "All");
        parameters.put("Transactions", String.valueOf(transactionCount));
        parameters.put("Total", String.valueOf(total));
        parameters.put("BestSellingProduct", bestSellingProduct);
        parameters.put("BestSellingCategory", bestSellingProductCategory);

        for (int i = 0; i < 4; i++) {
            parameters.put("Week_" + String.format("%02d", i + 1) + "_From", weekFromArr[i]);
            parameters.put("Week_" + String.format("%02d", i + 1) + "_To", weekToArr[i]);
        }

        int[] weekTransactions = new int[4];
        double[] weekIncome = new double[4];
        String queryDate = "";
        double income = 0.0;
        int transactions = 0;

        for (int i = 0; i < 4; i++) {
            String queryForWeek = "SELECT * FROM `invoice` WHERE `invoice`.`date` BETWEEN ? AND ? ORDER BY `invoice`.`date` ASC";
            try {
                ResultSet rs = MySQL.executeSearch(queryForWeek, weekFromArr[i], weekToArr[i]);
                while (rs.next()) {
                    if (!queryDate.equals(rs.getString("invoice.date"))) {
                        queryDate = rs.getString("invoice.date");

                        weekIncome[i] = income;
                        weekTransactions[i] = transactions;

                        income = 0.0;
                        transactions = 0;
                    }
                    income += rs.getDouble("invoice.paid_amount");
                    transactions++;
                }

            } catch (Exception ex) {
                Logger.getLogger(ReportPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        parameters.put("Week_01_Transactions", String.valueOf(weekTransactions[0]));
        parameters.put("Week_02_Transactions", String.valueOf(weekTransactions[1]));
        parameters.put("Week_03_Transactions", String.valueOf(weekTransactions[2]));
        parameters.put("Week_04_Transactions", String.valueOf(weekTransactions[3]));

        parameters.put("Income_01", String.valueOf(weekIncome[0]));
        parameters.put("Income_02", String.valueOf(weekIncome[1]));
        parameters.put("Income_03", String.valueOf(weekIncome[2]));
        parameters.put("Income_04", String.valueOf(weekIncome[3]));

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
        jMonthChooser1 = new com.toedter.calendar.JMonthChooser();
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        jButton1 = new javax.swing.JButton();

        jLabel1.setText("Select Year & Month :");

        jButton1.setText("Generate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jYearChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jMonthChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int reportYear = jYearChooser1.getYear();
        int reportMonth = jMonthChooser1.getMonth() + 1;

        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        if (reportYear > currentYear || reportYear == 0) {
            JOptionPane.showMessageDialog(this, "Please Select Valid Year", "Missing Year", JOptionPane.WARNING_MESSAGE);
        } else if (reportMonth > 12 || reportMonth < 1 || (reportYear == currentYear && reportMonth > currentMonth)) {
            JOptionPane.showMessageDialog(this, "Please Select Valid Month", "Invalid Month", JOptionPane.WARNING_MESSAGE);
        } else {
            LocalDate reportYearMonth = LocalDate.of(reportYear, reportMonth, 1);

            LocalDate startOfMonth = reportYearMonth.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endOfMonth = reportYearMonth.with(TemporalAdjusters.lastDayOfMonth());

            // Format the dates to yyyy-MM-dd format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedStartOfMonth = formatter.format(startOfMonth);
            String formattedEndOfMonth = formatter.format(endOfMonth);

            // get week start and end dates
            String[] weekFromArr = new String[4];
            String[] weekToArr = new String[4];
            LocalDate startOfWeek = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            LocalDate endOfWeek;
            int weekCount = 0;

            while (startOfWeek.isBefore(endOfMonth) || startOfWeek.isEqual(endOfMonth)) {
                endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
                if (endOfWeek.isAfter(endOfMonth)) {
                    endOfWeek = endOfMonth;
                }

                weekFromArr[weekCount] = startOfWeek.format(formatter);
                weekToArr[weekCount] = endOfWeek.format(formatter);

                // Move to the next week
                startOfWeek = endOfWeek.plusDays(1);
                weekCount++;
            }

            generateMonthlyReport(formattedStartOfMonth, formattedEndOfMonth, weekFromArr, weekToArr);

        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private com.toedter.calendar.JMonthChooser jMonthChooser1;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    // End of variables declaration//GEN-END:variables
}
