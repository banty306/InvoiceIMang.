package com.crudApp;

import java.io.IOException;
import java.io.PrintWriter;
// import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/Insert")
public class Insert extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
        // setting response headers
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // used gsonbuilder to create gson object and to set date format
        GsonBuilder builder = new GsonBuilder(); 
        builder.setDateFormat("yyyy-mm-dd");
        // used to print object in pretty json format
        builder.setPrettyPrinting();
        // creating gson object
        Gson gson = builder.create();
        
        // calling function to get data from body
		String result = InvoicesDAO.getBody(request);
		
		//printing body
		System.out.println("Result: "+result);


        // Retreiving data from above JSONstring and storing it in POJOClass instance
        Invoices i = gson.fromJson(result, Invoices.class); 
        System.out.println("JSON Deserialization"+i);   
        
        // Setting data and sending to add function
        Invoices invoice = new Invoices();
        invoice.setCustomerName(i.getCustomerName());  
        invoice.setCustomerNo(i.getCustomerNo());  
        invoice.setInvoiceID(i.getInvoiceID());          
        invoice.setTotalAmount(i.getTotalAmount()); 
        invoice.setDueDate(i.getDueDate());
        invoice.setPredictedPaymentDate(i.getPredictedPaymentDate());
        invoice.setNotes(i.getNotes());
        // calling create invoice to create a new instance of invoice class and add to DB;
        int status=InvoicesDAO.createInvoice(invoice);  
        System.out.println("Status: "+status); 
               
//		Implemented the same using getParameters
        
//      String customerName = request.getParameter("customerName");  
//      String customerNumber = request.getParameter("customerNo");  
//      String invoiceNumber = request.getParameter("invoiceNo");  
//      String amount = request.getParameter("amount"); 
//      String dueDate = request.getParameter("dueDate");
//      String notes = request.getParameter("notes");
////      String predictedPaymentDate = request.getParameter("predictedPaymentDate");
//      System.out.println("Date is: "+dueDate);
//      float convertedAmount = Float.parseFloat(amount);
//      Date convertedDueDate = Date.valueOf(dueDate);
//      String paymentDate = null;
//      Date convertedPaymentDate = (predictedPaymentDate.length()==0)?null:Date.valueOf(predictedPaymentDate);
//      Invoices invoice=new Invoices();  
//      invoice.setCustomerName(customerName);  
//      invoice.setCustomerNo(customerNumber);  
//      invoice.setInvoiceID(invoiceNumber);  
//      invoice.setTotalAmount(convertedAmount); 
//      invoice.setDueDate(convertedDueDate);
//      invoice.setPredictedPaymentDate(convertedPaymentDate);
//      invoice.setNotes(notes);
//      int status=InvoicesDAO.createInvoice(invoice);                 
        
        if(status>0){   
        	 String message = "{\"message\":\"Data added Successfully\",\"status\":\"200\"}";
	   		 String res = gson.toJson(message);
	   		 out.print(res);
	   		 out.flush();
        }else{  
            String message="{\"message\":\"Please send data with proper parameter names, or recheck number of fields sent\",\"status\":\"502\"}";
            String res = gson.toJson(message);
            out.print(res);
            out.flush();
        }    
        out.close();  
        
	}	
}
