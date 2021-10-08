package com.crudApp;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


@WebServlet("/Read")
public class Read extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// setting response headers
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");   
		
		String body = InvoicesDAO.getBody(request);
		int pageNo = Integer.parseInt(body);
		
//      Implemented using getParameters
		
//		String currentPage = request.getParameter("page");
//		System.out.println("currentPage is: "+currentPage);
//		int pageNo = Integer.parseInt(currentPage);
		
	    Gson gson = new Gson();
        
        try {
			 List<Invoices> invoices = InvoicesDAO.getAllInvoices(pageNo);
			 String data = gson.toJson(invoices);
	   		 out.print(data);
	   		 System.out.println("Data is: "+data);
	   		 out.flush();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		
	}

}
