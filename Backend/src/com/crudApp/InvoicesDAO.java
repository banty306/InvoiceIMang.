package com.crudApp;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.*;

public class InvoicesDAO {
	private static String JDBC_URL = "jdbc:mysql://localhost:3306/highradius_project";
	private static String username = "root";
	private static String password = "rootpassword";
	public static Connection getConnection() {
		Connection connection = null;
		try {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
//				DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
				connection = DriverManager.getConnection(JDBC_URL,username,password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}catch(SQLException err) {
			err.printStackTrace();
		}
		return connection;
	}
	
	/*         GET ALL INVOICES         */
	public static List<Invoices> getAllInvoices(int pageNo) throws SQLException{
		Connection conn = getConnection();
		List<Invoices> invoiceList = new ArrayList<Invoices>();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT * FROM invoices ORDER BY id DESC LIMIT 11 OFFSET "+11*(pageNo)+" ;"
		);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			Invoices invoice = new Invoices();
			invoice.setID(rs.getInt(1));
//			invoice.setKeyID(rs.getInt(2));
			invoice.setCustomerNo(rs.getString(3));
			invoice.setCustomerName(rs.getString(4));
			invoice.setDueDate(rs.getDate(5));
			invoice.setTotalAmount(rs.getFloat(6));
			invoice.setInvoiceID(rs.getString(7));
			invoice.setPredictedPaymentDate(rs.getDate(8));
			invoice.setNotes(rs.getString(9));
			invoiceList.add(invoice);
//			System.out.println("Invoices in getAllInvoices are: "+invoice);
		}
		rs.close();
		ps.close();
		conn.close();
		return invoiceList;
	}
	/*          CREATE INVOICE          */
	public static int createInvoice(Invoices i){
		// If status is 0 then query didnt excecute
		int status=0;  
        try{  
            Connection con=InvoicesDAO.getConnection();  
            PreparedStatement ps=con.prepareStatement(  
             "INSERT INTO invoices(customerNo,customerName,dueDate,totalAmount,invoiceID,predictedPaymentDate,notes) values (?,?,?,?,?,?,?)"
            );  
//            ps.setInt(1,i.getKeyID());  
            ps.setString(1,i.getCustomerNo());  
            ps.setString(2,i.getCustomerName());  
            ps.setDate(3,i.getDueDate());  
            ps.setFloat(4,i.getTotalAmount());
            ps.setString(5,i.getInvoiceID());
            ps.setDate(6,i.getPredictedPaymentDate());
            ps.setString(7,i.getNotes());
              
            status=ps.executeUpdate();  
    		ps.close();
            con.close();  
        }catch(Exception ex){
        	ex.printStackTrace();
        }  
          
        return status;  
	}
	/*          UPDATE INVOICE       */
	public static int updateInvoice(Invoices i){  
		// If status is 0 then query didnt excecute
        int status=0;  
        try{  
            Connection con=InvoicesDAO.getConnection();  
            PreparedStatement ps=con.prepareStatement(  
              "UPDATE invoices SET totalAmount=?,notes=? where id=?"
            );  
            ps.setFloat(1,i.getTotalAmount());  
            ps.setString(2,i.getNotes());    
            ps.setInt(3,i.getID());  
              
            status=ps.executeUpdate();  
            ps.close();
            con.close();  
        }catch(Exception ex){
        	ex.printStackTrace();
        }   
        return status;  
	}
/*        DELETE INVOICES      */
   public static int deleteInvoice(int id){  
        int status=0;  
        try{  
            Connection con=InvoicesDAO.getConnection();  
            PreparedStatement ps=con.prepareStatement("DELETE FROM invoices WHERE id IN ( ? );");  
            ps.setInt(1, id);	
            status=ps.executeUpdate();  
            ps.close();
            con.close();  
        }catch(Exception e){
        	e.printStackTrace();
        }  
          
        return status;  
    }  
   
 /*               GET BODY FROM BUFFERREADER                */
   public static String getBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}
}
