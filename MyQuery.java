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
    		//System.out.println(namesIds.getRow(1).getColumn(3));
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
		   System.out.println("");
		   QueryTable q1 = findGPAInfo();
		   q1.printTable();
		   System.out.println("");
		 
    }
    

    public QueryTable findMorningCourses() throws SQLException{
    	String morningSectionsQ = "select distinct course_id, sec_id, title, semester, year, instructor.name from takes join section using (course_id, sec_id, semester, year) join time_slot using (time_slot_id)\n" + 
    			"join teaches using (course_id, sec_id, semester, year) join instructor on teaches.ID = instructor.ID join course using (course_id)\n" + 
    			"\n" + 
    			"where start_hr <=12 order by course_id";
    	resultSet = statement.executeQuery(morningSectionsQ);
    	QueryTable morningSectionsTable = new QueryTable(resultSet);
    	
    	String takesQ = "select course_id, sec_id, semester, year from takes";
    	resultSet = statement.executeQuery(takesQ);
    	QueryTable takesTable = new QueryTable(resultSet);
    	//takesTable.printTable();
    	//morningSectionsTable.printTable();
    	
    	for(int i = 0; i < morningSectionsTable.getRowCount(); i ++) {
    		int enrollments = 0;
    		for(int j = 0; j < takesTable.getRowCount(); j++) {
    			
    			String course_id1 = morningSectionsTable.getRow(i).getColumn(1);
    			String sec_id1 = morningSectionsTable.getRow(i).getColumn(2);
    			String semester1 = morningSectionsTable.getRow(i).getColumn(4);
    			String year1 = morningSectionsTable.getRow(i).getColumn(5);
    			
    			String course_id2 = takesTable.getRow(j).getColumn(1);
    			String sec_id2 = takesTable.getRow(j).getColumn(2);
    			String semester2 = takesTable.getRow(j).getColumn(3);
    			String year2 = takesTable.getRow(j).getColumn(4);
    			
    			if(course_id1.equals(course_id2) && sec_id1.equals(sec_id2) && semester1.equals(semester2) && year1.equals(year2)) {
    				enrollments ++;
    			}
    	
    		
    		}
    		morningSectionsTable.getRow(i).addColumn(Integer.toString(enrollments));
    	}
    	return morningSectionsTable;
    	
    }
    

    public void printMorningCourses() throws IOException, SQLException{
	   	System.out.println("******** Query 2 ********");
	   	System.out.println("");
	   	QueryTable q2 = findMorningCourses();
	   	System.out.printf("%-9s %-8s %-30s %-8s %-5s %-10s %-2s %n", "COURSE_ID", "SEC_ID", "TITLE", "QUARTER", "YEAR", "INSTRUCTOR", "ENROLLMENTS");
	   	System.out.println("---------------------------------------------------------------------------------------");
	   	for(int i = 0; i < q2.getRowCount(); i ++) {
	   		
	   		System.out.printf("%-9s %-8s %-30s %-8s %-5s %-10s %-2s %n", q2.getRow(i).getColumn(1), q2.getRow(i).getColumn(2), q2.getRow(i).getColumn(3), q2.getRow(i).getColumn(4),q2.getRow(i).getColumn(5), q2.getRow(i).getColumn(6), q2.getRow(i).getColumn(7));
	   	}
    }

    public QueryTable findBusyInstructor() throws SQLException{
    	String query = "select count(*), ID from teaches join instructor using (ID) group by ID";
    	resultSet = statement.executeQuery(query);
    	QueryTable table = new QueryTable(resultSet);
    	
    	QueryTable busiest = new QueryTable();
    	
    	//find most taught number
    	int max = 0;
    	for(int i = 0; i < table.getRowCount(); i ++) {
    		if(Integer.valueOf(table.getRow(i).getColumn(1)) > max) {
    			max = Integer.valueOf(table.getRow(i).getColumn(1));
    		}
    	}
    	
    	for(int i = 0; i < table.getRowCount(); i++) {
    		if(Integer.valueOf(table.getRow(i).getColumn(1)) == max) {
    			QueryRowResult temp = new QueryRowResult();
    			temp.addColumn(table.getRow(i).getColumn(2));
    			busiest.add(temp);
    		}
    	}
    	//busiest.printTable();
    	query = "select ID, name from instructor";
    	resultSet = statement.executeQuery(query);
    	QueryTable instructors = new QueryTable(resultSet);
    	//instructors.printTable();
    	
    	for(int i = 0; i < busiest.getRowCount(); i ++) {
    		for(int j = 0; j < instructors.getRowCount(); j++) {
    			if(busiest.getRow(i).getColumn(1).equals(instructors.getRow(j).getColumn(1))) {
    				busiest.getRow(i).addColumn(instructors.getRow(j).getColumn(2));
    				busiest.getRow(i).deleteColumn(1);
    			}
    		}
    	}
    	return busiest;
    }

    public void printBusyInstructor() throws IOException, SQLException{
		   System.out.println("******** Query 3 ********");
		   System.out.println("");
		   System.out.println("NAME");
		   System.out.println("----------");
		   QueryTable q3 = findBusyInstructor();
		   q3.printTable();
		   System.out.println("");
    }

    public QueryTable findPrereq() throws SQLException{
    	String query = "SELECT title, prereq_id \n" + 
    			"FROM course left outer join prereq using (course_id)";
    	resultSet = statement.executeQuery(query);
    	
    	QueryTable table = new QueryTable(resultSet);
    	
    	query = "select course_id, title from course";
    	resultSet = statement.executeQuery(query);
    	QueryTable table2 = new QueryTable(resultSet);
    	
    	
    	for(int i = 0; i < table.getRowCount(); i++) {
    		for(int j = 0; j < table2.getRowCount(); j++) {
    			if(table.getRow(i).getColumn(2) != null && table.getRow(i).getColumn(2).equals(table2.getRow(j).getColumn(1)) ) {
    					table.getRow(i).setColumn(2, table2.getRow(j).getColumn(2));
    			}
    		}
    	}
    	
    	return table;
    }

    public void printPrereq() throws IOException, SQLException{
		   System.out.println("******** Query 4 ********");
		   System.out.println("");
		   QueryTable q4 = findPrereq();
		
		   System.out.printf("%-30s %-20s %n", "COURSE", "PREREQ");
		   System.out.println("---------------------------------------------------------");
		   
		   for(int i = 0; i < q4.getRowCount(); i ++) {
			   if(q4.getRow(i).getColumn(2) == null) {
				   System.out.println(q4.getRow(i).getColumn(1));
			   }
			   else {
			   System.out.printf( "%-30s %-20s %n", q4.getRow(i).getColumn(1), q4.getRow(i).getColumn(2));
			   }
			   }
		   System.out.println("");
    }

    public void updateTable() throws SQLException{
    	String query = "DROP TEMPORARY TABLE if exists updateTable";
    			statement.execute(query);
    	
    	query = "create TEMPORARY table updateTable select * from student";
    	
    	statement.execute(query);
    	
    	
    	query =	"UPDATE updateTable T1 SET tot_cred = (SELECT SUM(credits) FROM takes T2 JOIN course USING (course_id) WHERE T1.ID = T2.ID AND grade <> 'F');"; 
    	statement.execute(query);
    	
    	query = "UPDATE updateTable SET tot_cred = 0 WHERE tot_cred IS NULL";
    	statement.execute(query);
    	
    	resultSet = statement.executeQuery("select * from updateTable");
    	
    	
    	

    }

    public void printUpdatedTable() throws IOException, SQLException{
		   System.out.println("******** Query 5 ********");
		   System.out.println("");
		   System.out.printf("%-6s %-10s %-12s %-4s %n", "ID", "NAME", "DEPARTMENT", "CREDITS");
		   System.out.println("-------------------------------------------");
		   while(resultSet.next()) {
		   System.out.printf("%-6s %-10s %-12s %-4s %n", resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
		   }
		   System.out.println("");
		   
    }
	
	 public void findHeadCounts() throws SQLException{
		  System.out.println("******** Query 6 ********");	
		  Scanner input = new Scanner(System.in);
		  System.out.println("Please enter the department name: ");
		  String department = input.nextLine();// <--------Oh this little line almost drove me crazy.. Had .next()
		  
		  String s = "\"" + department + "\"";
		  String query = "CALL getNumbers("+ s + ",@student_count, @instructor_count)";
		  
		  statement.executeQuery(query);
		  
		  query = "SELECT @student_count";
		  resultSet = statement.executeQuery(query);
		  resultSet.next();
		  
		  String studentCount = resultSet.getString(1);
		  query = "SELECT @instructor_count";
		  resultSet = statement.executeQuery(query);
		  resultSet.next();
		  
		  String instructorCount = resultSet.getString(1);
		  
		  System.out.println(department + "has " + instructorCount + " instructors.");
		  System.out.println(department + "has " + studentCount + " students.");
		  
		//  String studentCount = 
		 
	 }
    
    
    // extra credit
    public void findFirstLastSemester() throws SQLException{
 
    }

    public void printFirstLastSemester() throws IOException, SQLException{
        System.out.println("******** Query 7 ********");
    }

}
