package Service;

//service层，增删改查接口
public interface BaseService{
    public boolean insert(boolean islocal, Object object);
    public boolean delete(boolean islocal, Object object);
    public boolean update(boolean islocal, Object object);
    public void findList(boolean islocal, Object object);
   
}
