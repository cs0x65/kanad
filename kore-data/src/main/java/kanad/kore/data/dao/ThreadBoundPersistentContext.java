package kanad.kore.data.dao;

/**
 * This class captures the per thread persistent context(and other relevant data) when DAO framework is setup/used in
 * PER_THREAD mode. The parameterized type I can be either {@link javax.persistence.EntityManager} or
 * {@link java.sql.Connection}.
 * The respective DaoProvider implementation (be it Raw one or JPA based), shall hold and provide a distinct instance
 * of this class per thread. Typically this is achieved by using ThreadLocal storage.
 * The current DaoProvider implementations use this design while implementing PER_THREAD strategy.
 * Iy shall be noted that the DAO consumer or client code must make sure that every call to get a new DAO is matched by
 * a corresponding call to DAO.close(). In the end, number of get new DAO calls shall match number of DAO.close() calls;
 * otherwise the DaoProvider implementation is vulnerable to Connection Leaks.
 */
public class ThreadBoundPersistentContext<I> {
	private I persistentContext;
	private int persistentContextCount = 0;
	
	public I getPersistentContext() {
		return persistentContext;
	}
	
	public void setPersistentContext(I persistentContext) {
		this.persistentContext = persistentContext;
	}
	
	public int getPersistentContextCount() {
		return persistentContextCount;
	}
	
	public void setPersistentContextCount(int persistentContextCount) {
		this.persistentContextCount = persistentContextCount;
	}
}
