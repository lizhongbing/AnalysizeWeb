package Service;

//服务层，taskname增删改查接口
public interface TaskNameService extends BaseService{
	boolean insert(boolean islocal, Object taskname);
	boolean delete(boolean islocal,Object taskname);
	boolean update(boolean islocal, Object taskname);
    void findList(boolean islocal, Object taskname);
}
