package in.cozynest.cozyapis.dao;
import java.util.ArrayList;

import in.cozynest.cozyapis.model.Package;


public interface IPackageDao {
	
	
	public boolean exists(int pk);

	public long count();

	public Package create(Package pakage);

	public Package update(Package pakage);

	public void delete(Package pakage);

	public ArrayList<Package> findAll();

	public Package findById(int id);

}
