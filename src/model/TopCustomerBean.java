package model;

public class TopCustomerBean {
    
    private String InvoiceId;
    private int transactions;
    private double payments;
    private String mobile;
    private String name;
            

    public String getInvoiceId() {
        return InvoiceId;
    }

    public void setInvoiceId(String InvoiceId) {
        this.InvoiceId = InvoiceId;
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }

    public double getPayments() {
        return payments;
    }

    public void setPayments(double payments) {
        this.payments = payments;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
