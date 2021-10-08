package com.crudApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;    

public class CSVReaderInJava { 
	private static String JDBC_URL = "jdbc:mysql://localhost:3306/highradius_project";
	private static String username = "root";
	private static String password = "rootpassword";
	public static final String createSQLTable = "create table invoices (\r\n" + 
			"  id  int(5) PRIMARY KEY NOT NULL AUTO_INCREMENT,\r\n" +
	        "  keyID int(5),\r\n" + 
	        "  customerNo varchar(20),\r\n" + 
	        "  customerName varchar(40),\r\n" + 
	        "  dueDate DATE,\r\n" + 
	        "  totalAmount int(10),\r\n" + 
	        "  invoiceID varchar(20),\r\n" +
	        "  predictedPaymentDate DATE,\r\n" + 
	        "  notes varchar(100)\r\n" +
	        "  );";
	public static final String INSERT_INVOICES_SQL = "INSERT INTO invoices" +
	        "  (id, keyID, customerNo, customerName, dueDate, totalAmount, invoiceID, predictedPaymentDate, notes) VALUES " +
	        " (?,?,?,?,?,?,?,?,?);";

/*              MAIN FUNCTION              */
	public static void main(String... args) throws SQLException{ 
		List<Invoice> invoices;
		try {
			
			// reading from CSV file
			invoices = readInvoicesFromCSV("D:\\HighRadius\\db_new.csv");
			
			// create Table in SQL using Code
			createSQLTable();
			
			// insert into Table using Code
			insertRecord(invoices);
			
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			System.out.println("End of Function");
		}
	} 
	
/*              CREATE SQL CONNECTION               */
	public static Connection getConnection() {
    	Connection connection = null;
    	try {
    		DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
    		connection = DriverManager.getConnection(JDBC_URL,username,password);
    		System.out.println("Establishing Connection: "+connection);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return connection;
    }
/*             PRINT SQL EXCEPTIONS                */ 
    public static void printSQLException(SQLException ex) {
    	for (Throwable e : ex) {
    		if (e instanceof SQLException) {
    			e.printStackTrace(System.err);
    			System.err.println("SQLState: " + ((SQLException) e).getSQLState());
    			System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
    			System.err.println("Message: " + e.getMessage());
    			Throwable t = ex.getCause();
    			while (t != null) {
    				System.out.println("Cause: " + t);
    				t = t.getCause();
    			}
    		}
    	}
    }
/*                       CREATING SQL TABLE                               */
    public static void createSQLTable() throws SQLException {

        System.out.println("Inside createTable Function: "+createSQLTable);
        try (
        	Connection connection = getConnection();
            Statement statement = connection.createStatement();
        	) {
            boolean isExcecuted = statement.execute(createSQLTable);
            System.out.printf("IsExcecuted: ",isExcecuted);
        } catch (SQLException e) {
        	printSQLException(e);
        }
        // try-with-resource statement will auto close the connection.
    }
    public static void insertRecord(List<Invoice> Arr) throws SQLException {
//        System.out.println(INSERT_INVOICES_SQL);

        try (
        		Connection connection = getConnection();
        		PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INVOICES_SQL);
        	){
        	// Statement is used for string based queries and PreparedStatements for Parameter based Queries.	
    		int count = 0;
            for (Invoice invoice : Arr) {
            	preparedStatement.setInt(1, count);
	        	preparedStatement.setInt(2, invoice.getKeyID());
	        	preparedStatement.setString(3, invoice.getCustomerNo());
	            preparedStatement.setString(4, invoice.getCustomerName());
	            preparedStatement.setDate(5, invoice.getDueDate());
	            preparedStatement.setFloat(6, invoice.getTotalAmount());
	            preparedStatement.setString(7, invoice.getInvoiceID());
	            preparedStatement.setDate(8, invoice.getPredictedPaymentDate());
	            preparedStatement.setString(9, invoice.getCustomerName()+", customer");
	            
	            // addBatch is used for adding multiple rows to a DB
                preparedStatement.addBatch();

                count++;
                // execute every 1000 rows or less
                if (count == Arr.size()-1) {
                    preparedStatement.executeBatch();
                }
            }   
        }catch (SQLException e) {
        	printSQLException(e);
        }
        // try-with-resource statement will auto close the connection.
    }
    
/*                       READING FROM CSV FILE                       */
	private static List<Invoice> readInvoicesFromCSV(String fileName) throws ParseException { 
		List<Invoice> invoices = new ArrayList<>(); 
		Path pathToFile = Paths.get(fileName); 
		// create an instance of BufferedReader 
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) { 
			// read the first line from the text file 
			String line = br.readLine(); 
			// loop until all lines are read 
			while (line != null) { 
				// use string.split to load a string array with the values from each line of the file, using a comma as the delimiter 
				String[] attributes = line.split(","); 
				Invoice invoice = createInvoice(attributes); 
				// adding book into ArrayList
				invoices.add(invoice); 
				// read next line before looping if end of file reached, line would be null 
				line = br.readLine(); 
			} 
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} 
		return invoices; 
	}
/*                            CREATING SINGLE INVOICE                    */
	private static Invoice createInvoice(String[] metadata) throws ParseException {
		int key_id = Integer.parseInt(metadata[0]); 
		String customerNo = metadata[2]; 
		String customerName = metadata[3];
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date parsed = format.parse(metadata[9]);
        java.sql.Date dueDate = new java.sql.Date(parsed.getTime());
		float totalAmount = Float.parseFloat(metadata[12]);
		String invoiceID = metadata[15];
		SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        Date parsed2 = format2.parse(metadata[9]);
        java.sql.Date predictedPaymentDate = new java.sql.Date(parsed2.getTime());
		return new Invoice(key_id, customerNo, customerName, dueDate, totalAmount, invoiceID, predictedPaymentDate); 
	} 
} 

/*                            INVOICE CLASS                         */
class Invoice { 
	private int keyId; 
	private String customerNo; 
	private String customerName; 
	private java.sql.Date dueDate; 
	private float totalAmount;
	private String invoiceID; 	
	private java.sql.Date predictedPaymentDate; 
	public Invoice(int keyId, String customerNo, String customerName, java.sql.Date dueDate, float totalAmount, String invoiceID, java.sql.Date predictedPaymentDate) { 
		this.keyId=keyId;
		this.customerNo=customerNo;
		this.customerName=customerName;
		this.dueDate=dueDate;
		this.totalAmount=totalAmount;
		this.invoiceID=invoiceID;
		this.predictedPaymentDate=predictedPaymentDate;
	}
	public int getKeyID() { 
		return keyId; 
	} 
	public void setKeyID(int keyID) { 
		this.keyId = keyID; 
	} 
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
	public java.sql.Date getDueDate() { 
		return dueDate; 
	} 
	public void setDueDate(java.sql.Date dueDate) { 
		this.dueDate = dueDate; 
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
	public java.sql.Date getPredictedPaymentDate() {
		return predictedPaymentDate;
	}
	public void setPredictedPaymentDate(java.sql.Date predictedPaymentDate) {
		this.predictedPaymentDate = predictedPaymentDate;
	}
//    public String toString() { 
////    	return "\n 1. Key_ID: "+key_id+"\n 2. Customer Number: "+customerNo+"\n 3. Customer Name: "+customerName+"\n 4. Due Date: "+dueDate+"\n 5. Total Amount: "+totalAmount+"\n 6. Invoice ID: "+invoiceID+"\n 7. Predicted Payment Date: "+predictedPaymentDate; 
//    	return key_id+","+customerNo+","+customerName+","+dueDate+","+totalAmount+","+invoiceID+","+predictedPaymentDate; 
//    }
    
}