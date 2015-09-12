//CSCI330 spring 2013
// Bu Lu (W01086483)
// Assignment 2

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class connector {
	
	public static void main (String [] args) {
		 
			   Connection conn = null;
			   
			   try {
		            // The newInstance() call is a work around for some
		            // broken Java implementations
		            Class.forName("com.mysql.jdbc.Driver").newInstance();  
		        } catch (Exception ex) {
		            // handle the error  	
		        }
			   Scanner User_input = new Scanner(System.in);	   
	           try{
	        	   String dbAccess = "jdbc:mysql://db.cs.wwu.edu/CS330_201320";
		           String user = "lub_reader";
		           String password = "Pm4qC67td";
		           conn = DriverManager.getConnection (dbAccess, user, password);        //conn refers to "connection"
		           System.out.println("database connection established");
		           String Ticker_symbol;
            	   System.out.print( "Enter your Ticker Symbol: ");
            	   Ticker_symbol = User_input.nextLine();                           //ask for input from user
	               while (conn != null)  {                                          //loop when connected	            	  	            	      	   
	            	   if (Ticker_symbol.trim().equals("")|| Ticker_symbol == null){     //check if ticker symbol is empty string
	            		   System.out.println("Empty String! Bye ");
	            		   break;                                                         //terminate it if so
	            	   }else{
	            		   try{                                                   //select names from company table as long as the input ticker symbol is in the database
	            			   PreparedStatement Ticker_Select =conn.prepareStatement("Select name From company Where Ticker = \"" + Ticker_symbol + "\"");
	            			   ResultSet Result = Ticker_Select.executeQuery();
	            			   Result.next();
	            			   System.out.println(Result.getString(1));
	            			   System.out.print("Processing ");
	            			   System.out.print(Ticker_symbol);
	            			   System.out.println(" ...");
	        			                              //  select date, openprice, closeprice to calculate the input
	            			   String split_select = ("SELECT *FROM pricevolume WHERE Ticker = \"" + Ticker_symbol + "\" ORDER BY TransDate DESC");
	            			   PreparedStatement split_data = conn.prepareStatement(split_select);
	            			   Result = split_data.executeQuery();           //make it as an array of data which will be refered later
	            			   //Result.next();
	        			   
	            			  double openprice = 1.0;
	            			   
	            			   String date = " ";//Result.getNString(1);
	            			   Integer Split_count = 0;
	            			   
	            			   while (Result.next()){        //check next one is not null then keep outputing data, math belows
	            				   double closeprice = Result.getDouble(6);
	            				   //double closeprice = Result.getDouble(3);
	            				
		            			   double do_math = closeprice/openprice;
	            				   if (Math.abs(do_math - 2.0)<0.13){
	            					   System.out.println("2:1 split on " + date + "; " + closeprice + " --> " + openprice);
	            					   Split_count = Split_count +1;
	            				   }
	            				   if(Math.abs(do_math-3.0) < 0.13) {
	            					   System.out.println("3:1 split on " + date + "; " + closeprice + " --> " + openprice);
	            					   Split_count = Split_count +1;
	            				   }
	            				   if(Math.abs(do_math-1.5) < 0.13) {
	            					   System.out.println("3:2 split on " + date + "; " + closeprice + " --> " + openprice);
	            					   Split_count = Split_count +1;
	            				   }
	            				   date = Result.getString(2);
	            				   openprice = Result.getDouble(3);
	            				   
	            			   }	   
	            			   System.out.println("Splits : " + Split_count);
	            			   break;
	            		   }catch (SQLException ex2){                  // 2 exceptions handlers below
	            			   System.out.println("Found no such ticker symbol in the database, please try again");
	            			   System.out.print( "Enter your Ticker Symbol: ");
	                    	   Ticker_symbol = User_input.nextLine();
	            		   }   
	            	   }
	               }   
	           } catch (SQLException ex) {
	        	   // handle errors if there is any
	        	   System.out.println("database connection failed");
	        	   System.out.println("SQLException: " + ex.getMessage());
	        	   System.out.println( "SQLState: " + ex.getSQLState());
	        	   System.out.println( "VendorError: "  + ex.getErrorCode());
	           } 
	           User_input.close();  // close user input
	}
}
