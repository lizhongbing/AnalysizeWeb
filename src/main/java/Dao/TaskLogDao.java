package Dao;


//增删改查tasklog dao层接口
public interface TaskLogDao extends BaseDao{
	boolean insert(String[] database_paras,String sql);
	boolean delete(String[] database_paras,String sql);
	boolean update(String[] database_paras,String sql);
    void findList(String[] database_paras,String sql, int pageno, int pagesize,int totalrows);
}
