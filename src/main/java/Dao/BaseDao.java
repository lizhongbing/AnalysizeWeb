package Dao;

//增删改查dao层基本接口
public interface BaseDao {
	public boolean insert(String[] database_paras,String sql);
    public boolean delete(String[] database_paras,String sql);
    public boolean update(String[] database_paras,String sql);
    public void findList(String[] database_paras,String sql, int pageno, int pagesize,int totalrows);

}
