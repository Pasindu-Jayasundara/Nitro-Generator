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
import model.TopCustomerBean;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class SL_and_SM_and_TC_and_PL_Report extends javax.swing.JPanel {

    private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

    public SL_and_SM_and_TC_and_PL_Report() {
        initComponents();
        setUpBg();
    }

    private void setUpBg() {
        fromDateChooser.setDate(null);
        toDateChooser.setDate(null);
    }

    private void generateStockLevelReport(String fromDate, String toDate) {

        String query = "SELECT * FROM `stock` "
                + "INNER JOIN `product` ON `product`.`id`=`stock`.`product_id` "
                + "INNER JOIN `category` ON `category`.`id`=`product`.`category_id` "
                + "INNER JOIN `brand` ON `brand`.`id`=`product`.`brand_id` "
                + "INNER JOIN `grn_item` ON `stock`.`id`=`grn_item`.`stock_id` "
                + "INNER JOIN `grn` ON `grn_item`.`grn_id`=`grn`.`id` "
                + "INNER JOIN `supplier` ON `supplier`.`mobile`=`grn`.`supplier_mobile` "
                + "WHERE `stock`.`add_date` BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY `stock`.`id` ASC";

        try {
            ResultSet rs = MySQL.execute(query);

            DefaultTableModel dtm = new DefaultTableModel();

            dtm.addColumn("Stock_Id");
            dtm.addColumn("Item");
            dtm.addColumn("Brand");
            dtm.addColumn("Category");
            dtm.addColumn("Supplier");
            dtm.addColumn("SuplierContact");
            dtm.addColumn("AvaliableStock");

            dtm.setRowCount(0);
            int totalStocks = 0;

            while (rs.next()) {
                Vector<String> v = new Vector();

                v.add(rs.getString("stock.id"));
                v.add(rs.getString("product.name"));
                v.add(rs.getString("brand.brand"));
                v.add(rs.getString("category.category"));
                v.add(rs.getString("supplier.company"));
                v.add(rs.getString("supplier.mobile"));
                v.add(rs.getString("stock.qty"));

                dtm.addRow(v);

                totalStocks++;
            }

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("From", fromDate);
            parameters.put("To", toDate);
            parameters.put("TotalStok", String.valueOf(totalStocks));

            URL imagePathUrl = getClass().getResource("/images/");
            if (imagePathUrl != null) {
                String imagePath = imagePathUrl.toString();
                parameters.put("IMAGE_PATH", imagePath);
            } else {
                throw new RuntimeException("Image path not found");
            }

            JRTableModelDataSource tmd = new JRTableModelDataSource(dtm);

            try {
                JasperPrint jasperPrint = JasperFillManager.fillReport(getClass().getResourceAsStream("/report/stock_level.jasper"), parameters, tmd);
                JasperViewer.viewReport(jasperPrint, false);
            } catch (JRException ex) {
                System.out.println("Couldnot Load Data"+ex);
            }

        } catch (Exception ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

    }

    private void generateStockMovementReport(String fromDate, String toDate) {

        String query = "SELECT * FROM `stock` "
                + "INNER JOIN `product` ON `product`.`id`=`stock`.`product_id` "
                + "INNER JOIN `grn_item` ON `stock`.`id`=`grn_item`.`stock_id` "
                + "INNER JOIN `grn` ON `grn_item`.`grn_id`=`grn`.`id` "
                + "INNER JOIN `dam_stock` ON `dam_stock`.`stock_id`=`stock`.`id` "
                + "WHERE `stock`.`add_date` BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY `stock`.`id` ASC";

        try {
            ResultSet rs = MySQL.execute(query);
            ResultSet rs2 = rs;

            DefaultTableModel dtm = new DefaultTableModel();

            dtm.addColumn("Stock_Id");
            dtm.addColumn("Item");
            dtm.addColumn("QtyReceived");
            dtm.addColumn("ReceivedUnitPrice");
            dtm.addColumn("SoldQty");
            dtm.addColumn("SoldUnitPrice");
            dtm.addColumn("DamageQty");

            dtm.setRowCount(0);
            int totalStocks = 0;

            while (rs.next()) {
                Vector<String> v = new Vector();

                v.add(rs.getString("stock.id"));
                v.add(rs.getString("product.name"));

                String stockId = "";
                int receivedQty = 0;
                boolean addReceivedQty = false;

                while (rs2.next()) {

                    if (!rs2.getString("stock.id").equals(stockId)) {

                        v.add(String.valueOf(receivedQty));//received qty
                        addReceivedQty = true;
                        break;
                    } else {
                        receivedQty += rs2.getInt("grn_item.qty");
                    }
                }

                if (!addReceivedQty) {
                    v.add(rs.getString("grn_item.qty"));//received qty
                    receivedQty = rs.getInt("grn_item.qty");
                }

                v.add(rs.getString("grn_item.buying_price"));//unit price
                v.add(String.valueOf(receivedQty - rs.getInt("stock.qty")));//sold qty
                v.add(rs.getString("stock.selling_price"));//sold unit price
                v.add(rs.getString("dam_stock.qty"));//damage qty

                dtm.addRow(v);

                totalStocks++;
            }

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("From", fromDate);
            parameters.put("To", toDate);
            parameters.put("TotalStok", String.valueOf(totalStocks));

            URL imagePathUrl = getClass().getResource("/images/");
            if (imagePathUrl != null) {
                String imagePath = imagePathUrl.toString();
                parameters.put("IMAGE_PATH", imagePath);
            } else {
                throw new RuntimeException("Image path not found");
            }

            JRTableModelDataSource tmd = new JRTableModelDataSource(dtm);

            try {
                JasperPrint jasperPrint = JasperFillManager.fillReport(getClass().getResourceAsStream("/report/stock_movement.jasper"), parameters, tmd);
                JasperViewer.viewReport(jasperPrint, false);
            } catch (JRException ex) {
                System.out.println("Couldnot Load Data"+ex);
            }

        } catch (Exception ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

    }

    private void generateTopCustomersReport(String fromDate, String toDate) {

        String query = "SELECT * FROM `customer` INNER JOIN `invoice` ON `customer`.`mobile`=`invoice`.`customer_mobile` "
                + "WHERE `invoice`.`date` BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY `invoice`.`paid_amount` DESC";

        HashMap<String, TopCustomerBean> beanHash = new HashMap<>();
        int totalCustomers = 0;

        try {
            ResultSet rs = MySQL.execute(query);

            while (rs.next()) {

                String currentInvoiceId = rs.getString("invoice.id");

                if (!beanHash.containsKey(currentInvoiceId)) {//new

                    TopCustomerBean tcb = new TopCustomerBean();
                    tcb.setInvoiceId(currentInvoiceId);
                    tcb.setPayments(rs.getDouble("invoice.paid_amount"));
                    tcb.setTransactions(1);
                    tcb.setMobile(rs.getString("customer.mobile"));
                    tcb.setName(rs.getString("customer.fname"));

                    beanHash.put(currentInvoiceId, tcb);
                    totalCustomers++;

                } else { //already in
                    TopCustomerBean getBean = beanHash.get(currentInvoiceId);
                    getBean.setPayments(getBean.getPayments() + rs.getDouble("invoice.paid_amount"));
                    getBean.setTransactions(getBean.getTransactions() + 1);
                }

            }
        } catch (Exception e) {
            System.out.println("Couldnot Load Data"+e);
        }

        DefaultTableModel dtm = new DefaultTableModel();

        dtm.addColumn("CustomerId");
        dtm.addColumn("Name");
        dtm.addColumn("Contact");
        dtm.addColumn("TotalPurchase");
        dtm.addColumn("NoOfTransactions");

        dtm.setRowCount(0);

        for (TopCustomerBean bean : beanHash.values()) {

            Vector<String> v = new Vector<>();
            v.add(bean.getMobile());
            v.add(bean.getName());
            v.add(bean.getMobile());
            v.add(String.valueOf(bean.getPayments()));
            v.add(String.valueOf(bean.getTransactions()));

            dtm.addRow(v);

        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("From", String.valueOf(fromDate));
        parameters.put("To", String.valueOf(toDate));
        parameters.put("NoRegisteredCustomers", String.valueOf(totalCustomers));

        URL imagePathUrl = getClass().getResource("/images/");
        if (imagePathUrl != null) {
            String imagePath = imagePathUrl.toString();
            parameters.put("IMAGE_PATH", imagePath);
        } else {
            throw new RuntimeException("Image path not found");
        }

        JRTableModelDataSource tmd = new JRTableModelDataSource(dtm);

        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(getClass().getResourceAsStream("/report/top_customers.jasper"), parameters, tmd);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

    }

    private void generateProfitAndLossReport(String fromDate, String toDate) {

        double totalRevenue = 0.0;
        double totalCost = 0.0;
        double profit = 0.0;

        String revenue = "SELECT SUM(`invoice`.`paid_amount`) AS `totalRevenue` FROM `invoice` "
                + "WHERE `invoice`.`date` BETWEEN '" + fromDate + "' AND '" + toDate + "'";
        try {
            ResultSet revenueRs = MySQL.execute(revenue);
            if (revenueRs.next()) {
                totalRevenue = revenueRs.getDouble("totalRevenue");
            }
        } catch (Exception ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

        String cost = "SELECT SUM(`grn`.`paid_amount`) AS `totalCost` FROM `grn` "
                + "WHERE `grn`.`date` BETWEEN '" + fromDate + "' AND '" + toDate + "'";
        try {
            ResultSet costRs = MySQL.execute(cost);
            if (costRs.next()) {
                totalCost = costRs.getDouble("totalCost");
            }
        } catch (Exception ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

        profit = totalRevenue - totalCost;

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("From", String.valueOf(fromDate));
        parameters.put("To", String.valueOf(toDate));
        parameters.put("Total", String.valueOf(totalRevenue));
        parameters.put("Cost", String.valueOf(totalCost));
        parameters.put("Profit", String.valueOf(profit));
        parameters.put("CompanyName", "nexGen");

        URL imagePathUrl = getClass().getResource("/images/");
        if (imagePathUrl != null) {
            String imagePath = imagePathUrl.toString();
            parameters.put("IMAGE_PATH", imagePath);
        } else {
            throw new RuntimeException("Image path not found");
        }

        try {
            JREmptyDataSource eds = new JREmptyDataSource();
                    
            JasperPrint jasperPrint = JasperFillManager.fillReport(getClass().getResourceAsStream("/report/profit_and_loss_report.jasper"), parameters,eds);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            System.out.println("Couldnot Load Data"+ex);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fromDateChooser = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        toDateChooser = new com.toedter.calendar.JDateChooser();

        fromDateChooser.setDateFormatString("yyyy-MM-dd");

        jButton1.setText("Generate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Select Time Period :");

        jLabel2.setText("From :");

        jLabel3.setText("To :");

        toDateChooser.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fromDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fromDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(toDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addGap(3, 3, 3)))
                        .addGap(3, 3, 3)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Date fromDate = fromDateChooser.getDate();
        Date toDate = toDateChooser.getDate();

        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this, "Please Select Date Range", "Missing Date Range", JOptionPane.WARNING_MESSAGE);
        } else if (toDate.after(new Date()) || fromDate.after(new Date())) {
            JOptionPane.showMessageDialog(this, "Dates cannot be from tommorow", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        } else if (!toDate.after(fromDate)) {
            JOptionPane.showMessageDialog(this, "To date need to be after From date", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        } else {

            String formattedFromDate = sdf.format(fromDate);
            String formattedToDate = sdf.format(toDate);

            int reportChoice = ReportPanel.reportChoiceIndex;
            switch (reportChoice) {
                case 4://stock level
                    generateStockLevelReport(formattedFromDate, formattedToDate);
                    break;
                case 5://stock movement
                    generateStockMovementReport(formattedFromDate, formattedToDate);
                    break;
                case 6://top customer
                    generateTopCustomersReport(formattedFromDate, formattedToDate);
                    break;
                case 7://finance
                    generateProfitAndLossReport(formattedFromDate, formattedToDate);
                    break;
            }

        }

    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser fromDateChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private com.toedter.calendar.JDateChooser toDateChooser;
    // End of variables declaration//GEN-END:variables
}
