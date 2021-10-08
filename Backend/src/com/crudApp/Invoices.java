package com.crudApp;

import java.sql.Date;

class Invoices { 
	private int id;
//	private int keyId; 
	private String customerNo; 
	private String customerName; 
	private Date dueDate; 
	private float totalAmount;
	private String invoiceID; 	
	private Date predictedPaymentDate; 
	private String notes;
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id=id;
	}
//	public int getKeyID() { 
//		return keyId; 
//	} 
//	public void setKeyID(int keyID) { 
//		this.keyId = keyID; 
//	} 
	public String getCustomerNo() { 
		return customerNo; 
	} 
	public void setCustomerNo(String customerNo) { 
		this.customerNo = customerNo; 
	} 
	public String getCustomerName() { 
		return customerName; 
	} 
	public void setCustomerName(String customerName) { 
		this.customerName = customerName; 
	} 
	public Date getDueDate() { 
		return dueDate; 
	} 
	public void setDueDate(Date date) { 
		this.dueDate = date; 
	} 
	public float getTotalAmount() { 
		return totalAmount; 
	} 
	public void setTotalAmount(float totalAmount) { 
		this.totalAmount = totalAmount; 
	} 
	public String getInvoiceID() { 
		return invoiceID; 
	} 
	public void setInvoiceID(String invoiceID) { 
		this.invoiceID = invoiceID; 
	} 
	public Date getPredictedPaymentDate() {
		return predictedPaymentDate;
	}
	public void setPredictedPaymentDate(Date predictedPaymentDate) {
		this.predictedPaymentDate = predictedPaymentDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes=notes;
	}
}