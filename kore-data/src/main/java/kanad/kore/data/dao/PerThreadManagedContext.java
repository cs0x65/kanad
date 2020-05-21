package kanad.kore.data.dao;

import javax.persistence.EntityManager;

/**
 * This class captures the per thread managed context/storage when DAO framework is setup/used in PER_THREAD mode.
 * The respective DaoProvider implementation (be it Raw one or JPA based), shall hold and provide a distinct instance of this class per thread.
 * Typically this is achieved by using ThreadLocal storage. The current DaoProvider implementations use this design while implementing PER_THREAD
 * strategy. 
 * One shall note that the DAO consumer or client code must make sure that every call to get a new DAO is matched by a corresponding call to
 * DAO.close(). In the end, number of get new DAO calls shall match number of DAO.close() calls; otherwise the DaoProvider implementation is
 * vulnerable to Connection Leaks.  
 */
public class PerThreadManagedContext {
	private EntityManager entityManager;
	private int entityManagerCount = 0;
	
	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * @param entityManager the entityManager to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	/**
	 * @return the entityManagerCount
	 */
	public int getEntityManagerCount() {
		return entityManagerCount;
	}
	
	/**
	 * @param entityManagerCount the entityManagerCount to set
	 */
	public void setEntityManagerCount(int entityManagerCount) {
		this.entityManagerCount = entityManagerCount;
	}
}
