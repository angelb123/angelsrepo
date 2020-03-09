/*****************************
Query the University Database
*****************************/
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.util.*;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.lang.String;



public class MyQuery {
	
     private Connection conn = null;
	 private Statement statement = null;
	 private ResultSet resultSet = null;
    
    public MyQuery(Connection c)throws SQLException{
        conn = c;
        // Statements allow to issue SQL queries to the database
        statement = conn.createStatement();
    }
    
    public void findFall2009Students() throws SQLException{
        String query  = "select distinct name from student natural join takes where semester = \'Fall\' and year = 2009;";

        resultSet = statement.executeQuery(query);
    }
    
    public void printFall2009Students() throws IOException, SQLException {
	      System.out.println("******** Query 0 ********");
         System.out.println("name");
         while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number which starts at 1
			String name = resultSet.getString(1);
         System.out.println(name);
   		}        
    }
    
    public QueryTable findGPAInfo() throws SQLException{
    	String query = "Select ID, grade, credits from course join takes using (course_ID)";
    	resultSet = statement.executeQuery(query);
    	
    	QueryTable table1 = new QueryTable(resultSet);
    	
    	
    	//step 1: convert letter grade to numeric grade
    	for(int i = 0; i < table1.getRowCount(); i++) {
    		if(table1.getRow(i).getColumn(2) != null) {
    			String tempGrade = table1.getRow(i).getColumn(2);
    			table1.getRow(i).setColumn(2, Double.toString(convertGrade(tempGrade)));
    			
    		}	
    	}
    //	table1.printTable();
    	//****************************************************************
    	
    	//step 2: calculate GPA
    	
    	LinkedList<String> distinctStudents = new LinkedList<String>();
    	
    	query = "SELECT DISTINCT ID from student JOIN takes USING (ID)";
    	resultSet = statement.executeQuery(query);
    	while(resultSet.next()) {
    		distinctStudents.add(resultSet.getString(1));
    	}
    	 
    	
    	//For each distinct student, 
    	
    	//GPA = sum(numerical grade * credits) / sum(credits)
    	
    	QueryTable gpas = new QueryTable();
    	
    	for(int i = 0; i < distinctStudents.size(); i ++) {
    		double sumCredits = 0;
    		double sumNumCred = 0;
    		
    		for(int j = 0; j < table1.getRowCount(); j ++) {
    			
    			if(distinctStudents.get(i).equals(table1.getRow(j).getColumn(1)) && table1.getRow(j).getColumn(2) != null) {
    				sumCredits += Double.valueOf(table1.getRow(j).getColumn(3));
    				sumNumCred += Double.valueOf(table1.getRow(j).getColumn(2)) * Double.valueOf(table1.getRow(j).getColumn(3));
    			}	
    		}
    		
    		double gpa = sumNumCred/sumCredits;
    		String student = distinctStudents.get(i);
    		
    		//System.out.println(student + " " + gpa);
    		
    		QueryRowResult row = new QueryRowResult();
    		row.addColumn(student);
    		row.addColumn(Double.toString(gpa));
    		gpas.add(row);
    		
    	}
    		query = "SELECT DISTINCT ID, name from student join takes using (ID)";
    		
    		resultSet = statement.executeQuery(query);
    		QueryTable namesIds = new QueryTable(resultSet);
    		//namesIds.printTable();
    		
    		for(int i = 0; i < namesIds.getRowCount(); i ++) {
    			for(int j = 0; j < gpas.getRowCount(); j++) {
    				if(namesIds.getRow(i).getColumn(1).equals(gpas.getRow(j).getColumn(1))) {
    					
    					String tmpGPA = gpas.getRow(j).getColumn(2);
    					double d = Double.valueOf(tmpGPA);
    					String s = String.format("%f", d);
    					
    					namesIds.getRow(i).addColumn(s);
    					
    				//	System.out.println(namesIds.getRow(j).getColumn(3));

    				}
    			}
    		}
    		System.out.println(namesIds.getRow(1).getColumn(3));
    		return namesIds;
    }
    
    public double convertGrade(String grade) {
    	if(grade.equals("A")) {
    		return 4.0;
    	}
    	else if(grade.equals("A-")) {
    		return 3.67;
    	}
    	else if(grade.equals("B+")) {
    		return 3.33;
    	}
    	else if(grade.equals("B")) {
    		return 3.0;
    	}
    	else if(grade.equals("B-")) {
    		return 2.67;
    	}
    	else if(grade.equals("C+")) {
    		return 2.33;
    	}
    	else if(grade.equals("C")) {
    		return 2.00;
    	}
    	else if(grade.equals("C-")) {
    		return 1.67;
    	}
    	else if(grade.equals("D+")) {
    		return 1.33;
    	}
    	else if(grade.equals("D")) {
    		return 1.0;
    	}
    	else if(grade.equals("D-")) {
    		return 0.67;
    	}
    	else if(grade.equals("F")) {
    		return 0.0;
    	}
    	return 0;
    }
    
    public void printGPAInfo() throws IOException, SQLException{
		   System.out.println("******** Query 1 ********");
		   QueryTable q1 = findGPAInfo();
		   q1.printTable();
		 
    }
    

    public void findMorningCourses() throws SQLException{

    }

    public void printMorningCourses() throws IOException, SQLException{
	   	System.out.println("******** Query 2 ********");
    }

    public void findBusyInstructor() throws SQLException{
 
    }

    public void printBusyInstructor() throws IOException, SQLException{
		   System.out.println("******** Query 3 ********");
    }

    public void findPrereq() throws SQLException{

    }

    public void printPrereq() throws IOException, SQLException{
		   System.out.println("******** Query 4 ********");
    }

    public void updateTable() throws SQLException{

    }

    public void printUpdatedTable() throws IOException, SQLException{
		   System.out.println("******** Query 5 ********");
    }
	
	 public void findHeadCounts() throws SQLException{
		  System.out.println("******** Query 6 ********");	
	 }
    
    
    // extra credit
    public void findFirstLastSemester() throws SQLException{
 
    }

    public void printFirstLastSemester() throws IOException, SQLException{
        System.out.println("******** Query 7 ********");
    }

}
