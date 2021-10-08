package com.crudApp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/Update")
public class Update extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        // Setting response headers
		PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); 
        
        // Creating gson object
        Gson gson = new Gson();
        
        // Reading from body
        String body = InvoicesDAO.getBody(request);
        System.out.println(body);
        
        Invoices i = gson.fromJson(body, Invoices.class);
        
        // creating instance of invoice class and using it to change db values
        Invoices invoice = new Invoices();
        
        invoice.setTotalAmount(i.getTotalAmount());
        invoice.setNotes(i.getNotes());
        invoice.setID(i.getID());
     
        // calling update function
        int status = InvoicesDAO.updateInvoice(invoice);
        
//		  Implemented using getParameter
        
//        String id = request.getParameter("id");
//        String amount = request.getParameter("totalAmount");
//        String notes = request.getParameter("notes");
//        
//        int ID = Integer.parseInt(id);
//        float floatAmount = Float.parseFloat(amount);
//        
//        Invoices invoice=new Invoices();  
//        invoice.setID(ID);
//        invoice.setTotalAmount(floatAmount); 
//        invoice.setNotes(notes);
//        int status=InvoicesDAO.updateInvoice(invoice); 
        
        if(status>0){   
       	 	 String message = "{\"message\":\"Data updated Successfully\",\"status\":\"200\"}";
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
