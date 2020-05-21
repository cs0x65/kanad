package kanad.kore.data.dao;

/**
 * @param <T> can be either javax.persistence.EntityManager or java.sql.Connection.
 * Based on this, the Dao will be either Jpa based i.e. JpaDao or JDBC based raw one i.e. RawDao.
 * There are corresponding parameterized DAO providers that return the respective Dao variants. 
 */
public interface Dao<T> {
	void set(T connOrEntMgr);
	
	T get();
	
	void close();
	
	void beginTransaction();
	
	void commitTransaction();
	
	void rollbackTransaction();
	
	boolean hasActiveTransaction();
	
	public void setAutoCommit(boolean autoCommit);
	
	public boolean isAutoCommit();
}
