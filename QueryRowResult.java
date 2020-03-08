import java.util.LinkedList;

public class QueryRowResult{
	
	private LinkedList<String> column = new LinkedList<String>();
	public void addColumn(String result){
		column.add(result);
	}
	public String getColumn(int columnNum) {
		return column.get(columnNum -1 );
	}
	
	public void setColumn(int columnIndex, String value) {
		column.set(columnIndex -1, value);
	}
	public int getColumnCount() {
		return column.size();
	}
	public void printRow() {
		for(int i = 0; i < column.size(); i ++) {
			System.out.print(column.get(i) + " ");
		}
		System.out.println(" ");
	}
}
