package jsonparse;

import java.util.List;


public class QueryJsonParse<T> extends BaseJsonParse{
	
	private String pageno;
	private String pgaesize;
	private String totalrows;
	private List<T> rows;
	
	
	public String getPageno() {
		return pageno;
	}
	public void setPageno(String pageno) {
		this.pageno = pageno;
	}
	public String getPgaesize() {
		return pgaesize;
	}
	public void setPgaesize(String pgaesize) {
		this.pgaesize = pgaesize;
	}
	public String getTotalrows() {
		return totalrows;
	}
	public void setTotalrows(String totalrows) {
		this.totalrows = totalrows;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
	

}
