package kanad.kore.data.dao.jpa;

import org.apache.logging.log4j.LogManager;

import javax.persistence.EntityManager;
import java.lang.ref.WeakReference;

public abstract class AbstractJpaDao<T> implements JpaDao<T> {
	protected EntityManager entityManager;
	private WeakReference<JpaDaoProvider<T>> providerRef;
	
	public AbstractJpaDao(){
	}

	protected void setJpaDaoProvider(JpaDaoProvider<T> jpaDaoProvider){
		providerRef = new WeakReference<>(jpaDaoProvider);
	}
	
	public void close() {
		LogManager.getLogger().info("Closing entity manager...");
		providerRef.get().closePersistentContext(entityManager);
	}
	
	public void beginTransaction() {
		entityManager.getTransaction().begin();
		LogManager.getLogger().info("Transaction started");
	}
	
	public void commitTransaction() {
		entityManager.getTransaction().commit();
		LogManager.getLogger().info("Transaction committed");
	}
	
	public void rollbackTransaction() {
		LogManager.getLogger().info("Rolling back transaction");
		entityManager.getTransaction().rollback();
	}
	
	public boolean hasActiveTransaction() {
		return entityManager.getTransaction().isActive();
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#set(java.lang.Object)
	 */
	@Override
	public void set(EntityManager em) {
		entityManager = em;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#get()
	 */
	@Override
	public EntityManager get() {
		return entityManager;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#setAutoCommit(boolean)
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) {
		//Doesn't make sense for JPA based DAOs.
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#isAutoCommit()
	 */
	@Override
	public boolean isAutoCommit() {
		return false;//always
	}
}
