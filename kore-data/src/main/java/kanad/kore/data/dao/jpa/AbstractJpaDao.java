package kanad.kore.data.dao.jpa;

import kanad.kore.data.dao.DaoProviderFactory.DaoImplementationStrategy;
import kanad.kore.data.entity.jpa.KEntity;
import org.apache.logging.log4j.LogManager;

import javax.persistence.EntityManager;
import java.lang.ref.WeakReference;

public abstract class AbstractJpaDao implements JpaDao {
	protected EntityManager entityManager;
	private WeakReference<JpaDaoProviderImpl> providerRef;
	
	public AbstractJpaDao(){
	}
	
	
	protected void setJpaDaoProvider(JpaDaoProviderImpl jpaDaoProviderImpl){
		providerRef = new WeakReference<JpaDaoProviderImpl>(jpaDaoProviderImpl);
	}
	
	public void close() {
		LogManager.getLogger().info("Closing entity manager...");
		if(providerRef.get().getImplementationStrategy() == DaoImplementationStrategy.PER_THREAD){
			providerRef.get().closeThreadLocalManagedEntityManager();
		}else if(entityManager != null && entityManager.isOpen()){
			entityManager.close();
		}
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

	/* (non-Javadoc)
	 * kanad.kore.data.dao.jpa.JpaDao#refresh(java.lang.Object)
	 */
	@Override
	//One more option and which seems better one is to have cascade refresh rather than calling refresh
	//on each embedded entity field on the given entity.
	//Refer: http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#pc-cascade-refresh
	public void refresh(KEntity entity) {
		entityManager.refresh(entity);
	}
}
