package Dao;

import com.proweb.mysqlobject.mysqlObject;

public class TaskNameDaoImpl implements TaskNameDao{


	@Override
	public boolean insert(String[] database_paras, String sql) {
		return mysqlObject.ExeSql(sql);
	}

	@Override
	public boolean delete(String[] database_paras, String sql) {
		return mysqlObject.ExeSql(sql);
	}

	@Override
	public boolean update(String[] database_paras, String sql) {
		return mysqlObject.ExeSql(sql);
	}
	
	@Override
	public void findList(String[] database_paras, String sql, int pageno,
			int pagesize, int totalrows) {
		
	}

	
	
    
}
