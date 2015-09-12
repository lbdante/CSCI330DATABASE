//CSCI330 spring 2013
// Bu Lu (W01086483)
// Assignment 3

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;


public class connector2 {
	
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
		           conn = DriverManager.getConnection (dbAccess, user, password);  
	        	  // String dbAccess2 = "jdbc:mysql://db.cs.wwu.edu/lub";
		          // String user2 = "lub";
		         // password2 = "Fk5CD6nS";
		             //conn refers to "connection"
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
	            			  // String create_temp = ("CREATE TEMPORARY TABLE tmptable (Ticker CHAR(12) NOT NULL, TransDate CHAR(12) NOT NULL, OpenPrice CHAR(12) NULL, HighPrice CHAR(12) NULL, LowPrice CHAR(12) NULL, ClosePrice CHAR(12) NULL, Volume CHAR(12) NULL, AdjustedClose CHAR(12) NULL)");
	            			   
	            			   PreparedStatement split_data = conn.prepareStatement(split_select);
	            			   Result = split_data.executeQuery();           //make it as an array of data which will be refered later
	            			   //Result.next();
	            			   //String insert_temp = ("INSERT INTO tmptable (Ticker, TransDate,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,AdjustedClose) VALUES (Result.getString(1), Result.getString(2),Result.getString(3),Result.getString(4),Result.getString(5),Result.getString(6),Result.getString(7),Result.getString(8))");
	            			   //String drop_temp = ("DROP TABLE tmptable");
	            			   // String tmp_select = ("SELECT * FROM tmptable");
	            			 
	            				
	   /*         			   double openprice = 1.0;
	            			 
	            			   String date = " ";//Result.getNString(1);
	            			   Integer Split_count = 0;
	            			   //double factor = 1.0;
	            			   
	            			   while (Result.next()){        //check next one is not null then keep outputing data, math belows
	            				   date = Result.getString(2);	   double closeprice = Result.getDouble(6);
	            				  
	            				
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
	            			   System.out.println("Splits : " + Split_count);                                 */
	            			   //int mycount = 0;
	            			   ArrayList<TickerData> data = new ArrayList<TickerData>();
	            				double factor = 1.0;
	            				DecimalFormat decimal = new DecimalFormat();
	            				if (Result.next()) {
	            					TickerData o = new TickerData(); //o=open
	            					boolean found = false;	
	            					while (!found) {
	            						TickerData c = new TickerData(); //c=close
	            						c.TD = Result.getString(2);
	            						c.OP = Double.parseDouble(decimal.format(Double.parseDouble(Result.getString(3))));
	            						c.CP = Double.parseDouble(decimal.format(Double.parseDouble(Result.getString(6))));
	            						factor = Splits(c,o,factor);
	            						// Assign open and close
	            						o.CP = c.CP;
	            						o.OP = c.OP;
	            						o.TD = c.TD;
	            						// Factor the prices
	            						c.CP = c.CP*factor;
	            						c.OP = c.OP*factor;
	            						data.add(0, c);
	            						
	            						if (!Result.next()){
	            							found = true;
	            						}
	            					}
	            					System.out.println();
	            					if (data.size()<51) { 
	            						System.out.println("Executing investment strategy");
	            						System.out.println("Transactions executed: 0");
	            						System.out.println("Net gain: 0.00");
	            					}else{
	            						int trans = 0, shares = 0;
	            						double cash = 0;
	            						
	            						for (int i=50;i<data.size()-2;i+=1) {
	            							double avg = 0;
	            							for (int j=i-50; j<i; j+=1){
	            								avg+=data.get(j).CP;
	            							}
	            							avg = avg/50;
	            							double nextopen = data.get(i+1).OP; //next day openprice
	            							double open = data.get(i).OP, close = data.get(i).CP; //current openprice and closeprice
	            							double prevclose = data.get(i-1).CP; //previous day closeprice
	            							
	            							if (close < avg && (((close-open)/open) <= -0.03)) {
	            								shares+=100;
	            								cash = cash - 100*nextopen-8.00;
	            								trans+=1;
	            							} else if (shares >= 100 && open>avg && (((open-prevclose)/prevclose) >= 0.01)) {
	            								shares-=100;
	            								double avgprice = (open+close); 
	            								cash = cash + 50*avgprice-8.00;
	            								trans+=1;
	            							}
	            						}
	            						double net = (cash+shares*data.get(data.size()-1).OP); //equation for net gain
	            						System.out.println("Executing investment strategy");
	            						System.out.println("Transactions executed: " + trans);
	            						System.out.printf("Net gain: %3.2f\n", net);
	            						System.out.println();
	            						break;
	            					}
	            				}
	            			   /*conn = DriverManager.getConnection (dbAccess2, user2, password2);
	            			   PreparedStatement temp =conn.prepareStatement(create_temp);
	            			   ResultSet Result2 = temp.executeQuery();
	            			   Result2.next();
	            			   PreparedStatement ins_tmp =conn.prepareStatement(insert_temp);
	            			   ResultSet Result3 = ins_tmp.executeQuery();
	            			   Result3.next();
	            			   
	            			  
	            			   PreparedStatement select_temp =conn.prepareStatement(tmp_select);
	            			   ResultSet Result5 = select_temp.executeQuery();
	            			   Result5.next();
	            			   PreparedStatement drop =conn.prepareStatement(drop_temp);
	            			   ResultSet Result4 = drop.executeQuery();
	            			   Result4.next();*/
	            			   /*while (mycount < 51){
	            				   System.out.println(date + "; " + " --> " + openprice);
	            				   mycount = mycount + 1;
	            			   }*/
	            				
	            			  // break;
	            			}catch (SQLException ex2){                  // 2 exceptions handlers below
	            			   System.out.println("Found no such ticker symbol in the database, please try again");
	            			   System.out.print( "Enter your Ticker Symbol: ");
	                    	   Ticker_symbol = User_input.nextLine();
	            		   }
	            		   User_input.close();
	            	   }
	               }   
	           } catch (SQLException ex) {
	        	   // handle errors if there is any
	        	   System.out.println("database connection failed");
	        	   System.out.println("SQLException: " + ex.getMessage());
	        	   System.out.println( "SQLState: " + ex.getSQLState());
	        	   System.out.println( "VendorError: "  + ex.getErrorCode());}
	           } 
			
	   // close user input
	           private static double Splits(TickerData c, TickerData o, double factoring) {
	       		double factor = factoring;
	       		if (Math.abs((c.CP/o.OP)-2) < 0.13) { //equation to accommodate 2:1 split
	       			System.out.printf("2:1 split on " + c.TD + "; " + c.CP + " --> %3.2f\n", o.OP); 
	       			factor = (factor/2.0);
	       		}
	       		if (Math.abs((c.CP/o.OP)-3) < 0.13) { //equation to accommodate 3:1 split
	       			System.out.printf("3:1 split on " + c.TD + "; " + c.CP + " --> %3.2f\n", o.OP); 
	       			factor = (factor/3.0);
	       		}
	       		if (Math.abs((c.CP/o.OP)-1.5) < 0.13) { //equation to accommodate 3:2 split
	       			System.out.printf("3:2 split on " + c.TD + "; " + c.CP + " --> %3.2f\n", o.OP);
	       			factor = (factor/1.5);	
	       		}
	       		return factor;
	       	}
	         
	}

