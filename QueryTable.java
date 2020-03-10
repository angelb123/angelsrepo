//ANGEL BERMUDEZ
//CSCD 327 WINTER 2020
//DR. LI



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import com.mysql.cj.jdbc.result.ResultSetMetaData;


public class QueryTable {
	
	private LinkedList<QueryRowResult> results = new LinkedList<QueryRowResult>();
	private int columnCount;
	private int rowCount = 0;
	
	public QueryTable() {
		
	}
	
	public void add(QueryRowResult row) {
		this.columnCount = row.getColumnCount();
		results.add(row);
		this.rowCount ++;
	}
	
	public QueryTable(ResultSet setResult) throws SQLException{
		
		ResultSetMetaData metaData = (ResultSetMetaData) setResult.getMetaData();
		
    	//System.out.println("number off rows is: " + metaData.getColumnCount());
    	columnCount = metaData.getColumnCount();
    	
    	while(setResult.next()) {
    		rowCount++;
    		QueryRowResult temp = new QueryRowResult();
    		for(int j = 1; j <= columnCount; j++) {
    			temp.addColumn(setResult.getString(j));
    			
    		}
    		results.add(temp);
    	}
	}
	
	public QueryRowResult getRow(int index) {
		return results.get(index);
	}
	
	public void printTable() {
		if(this.rowCount != 0) {
			this.columnCount = results.get(0).getColumnCount();
		}
		
		for(int i = 0; i < rowCount; i ++) {
			for(int j = 1; j <= columnCount; j++) {
				
				System.out.print(results.get(i).getColumn(j) + " ");
			}
			System.out.println("");
		}
	}
	
	public void printTableNoNull() {
		if(this.rowCount != 0) {
			this.columnCount = results.get(0).getColumnCount();
		}
		
		for(int i = 0; i < rowCount; i ++) {
			for(int j = 1; j <= columnCount; j++) {
				if(results.get(i).getColumn(j) == null) {
					System.out.print(" ");
				}
				else {
				System.out.print(results.get(i).getColumn(j) + " ");
				}
			}
			System.out.println("");
		}
	}
	
	public int getRowCount() {
		return this.rowCount;
	}
	

}
