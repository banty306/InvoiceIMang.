package com.crudApp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/Delete")
public class Delete extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Setting response headers
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		String body = InvoicesDAO.getBody(request);
//		System.out.println("body: "+body);
		
		// converting to an array of integers
		String[] splitString = body.split(",");
		
		int[] idArr = new int[splitString.length-1];
//		System.out.println("length: "+splitString.length);
		for(int i=0;i<splitString.length;i++) {
				System.out.println("splitString: "+splitString[i]);	
				if(i==0) {
					// since the first parameter comes with a opening quotem we split it to get the number
					String[] temp = splitString[i].split("\"");
//					System.out.println("YO: "+temp[i+1]);
					idArr[i] = Integer.parseInt(temp[i+1]);
//					System.out.println("idArr: "+idArr[i]);				
				}else if(i!=splitString.length-1) {
					// since the last element is a double quote we skip that iteration
					idArr[i] = Integer.parseInt(splitString[i]);
//					System.out.println("idArr: "+idArr[i]);				
				}
		}
		
		// calling delete function using for loop
		int status = 0;
		for(int i=0;i<idArr.length;i++) {
			status = InvoicesDAO.deleteInvoice(idArr[i]); 			
		}
        
        Gson gson = new Gson();
        
        if(status>0){   
       	 String message = "{\"message\":\"Data deleted Successfully\",\"status\":\"200\"}";
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
