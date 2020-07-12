package kanad.kore.data.dao;

/**
 * @param <I> can be either javax.persistence.EntityManager or java.sql.Connection.
 * Based on this, the Dao will be either Jpa based i.e. JpaDao or JDBC based raw one i.e. RawDao.
 * There are corresponding parameterized DAO providers that return the respective Dao variants. 
 */
public interface Dao<I, T> {
	void set(I connOrEntMgr);
	
	I get();
	
	void close();
	
	void beginTransaction();
	
	void commitTransaction();
	
	void rollbackTransaction();
	
	boolean hasActiveTransaction();
	
	public void setAutoCommit(boolean autoCommit);
	
	public boolean isAutoCommit();

	/**
	 *
	 * @param t in the current persistent context, the state of which needs to be refreshed/reconciled from DB.
	 * Refreshes the state of the entity instance in the current persistent context from the database.
	 */
	void refresh(T t);
}
