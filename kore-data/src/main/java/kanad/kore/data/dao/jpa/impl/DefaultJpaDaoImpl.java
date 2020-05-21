package kanad.kore.data.dao.jpa.impl;

import kanad.kore.data.dao.jpa.AbstractJpaDao;
import kanad.kore.data.dao.jpa.DefaultJpaDao;
import kanad.kore.data.entity.jpa.AbstractEntity;
import kanad.kore.data.entity.jpa.KEntity;
import kanad.kore.data.entity.jpa.Remark;
import org.apache.logging.log4j.LogManager;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultJpaDaoImpl<T extends KEntity> extends AbstractJpaDao implements DefaultJpaDao<T> {
	private Class<T> clazz;
	
	public DefaultJpaDaoImpl() {

	}
	
	public DefaultJpaDaoImpl(Class<T> clazz) {
		this.clazz = clazz;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.DefaultJpaDao#getEntityById(long)
	 */
	@Override
	public AbstractEntity getById(long id) {
		AbstractEntity absEntity = null;
		try {
			absEntity = entityManager.find(AbstractEntity.class, id);
			if (absEntity != null) {
				LogManager.getLogger().info("abs entity retrieved Id : " + absEntity.getId());
			}
		} catch (IllegalArgumentException ie) {
			LogManager.getLogger().error("specified id or class for find not valid", ie);
		} catch (Exception e) {
			LogManager.getLogger().error("Exception !", e);
		}
		return absEntity;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.DefaultJpaDao#getEntityBySecureId(java.lang.String)
	 */
	@Override
	public AbstractEntity getBySecureId(String secureId) {
		AbstractEntity absEntity = null;	
		try {
			absEntity = entityManager
					.createQuery("from AbstractEntity where secureID.sekID = :secureID and deleted = :isDeleted",
							AbstractEntity.class)
					.setParameter("secureID", secureId).setParameter("isDeleted", 0).getSingleResult();
		} catch (IllegalArgumentException ie) {
			LogManager.getLogger().error("specified id or class for find not valid", ie);
		} catch (NoResultException ne) {
			LogManager.getLogger().info("No Records Found");
		} catch (Exception e) {
			LogManager.getLogger().error("Exception !", e);
		}
		return absEntity;
	}
	
	
	/* (non-Javadoc)
	 * kanad.kore.data.dao.jpa.DefaultJpaDao#add(kanad.kore.data.entity.AbstractEntity)
	 */
	@Override
	public void add(T entity) {
		LogManager.getLogger().info("Adding a generic/abstract entity...");
		if(!hasActiveTransaction()){
			LogManager.getLogger().info("No Active Txn for now; Txn Active Status = "+hasActiveTransaction());
			//This is a local/single DAO scoped transaction.
			beginTransaction();
			entityManager.persist(entity);
			commitTransaction();
			//Haa, finally could find a way! This is the real trick :|
			//Because the SecureID instance is not managed i.e. not the in persistence context, so calling getSecureID
			//on any AbstractEntity instance is futile - it would return null.
			//The SecureID records are managed by native DB trigger.
			//Refer: generating_secure_ids_for_the_entities_for_use_in_the_web_services in the docs or
			//SupplementaryDAOImpl.createSecureIDTrigger()
			//So, to force AbstractEntity instance to reconcile and load the related SecureID in the current persistent
			//context, we call refresh. From this point onwards, SecureID becomes managed.
			//Need to follow this pattern and start using secure ids in the web service request/response.
			refresh(entity);
		}else{
			//This is multi DAO scope transaction.
			LogManager.getLogger().info("Ongoing Active Txn; Txn Active Status = "+hasActiveTransaction());
			entityManager.persist(entity);
		}
	}
	
	/* (non-Javadoc)
	 * @see kanad.kore.data.dao.jpa.DefaultJpaDao#addAll(java.util.Collection)
	 */
	@Override
	public void addAll(Collection<T> entities) {
		LogManager.getLogger().info("Adding all generic/abstract entities...");
		if(!hasActiveTransaction()){
			beginTransaction();
			for(T entity: entities){
				add(entity);
			}
			commitTransaction();
		}else{
			for(T entity: entities){
				add(entity);
			}
		}
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.jpa.DefaultJpaDao#update(kanad.kore.data.entity.AbstractEntity)
	 */
	@Override
	public T update(T entity) {
		LogManager.getLogger().info("Updating a generic/abstract entity...");
		T mergedEntity = null;
		
		if(!hasActiveTransaction()){
			beginTransaction();
			mergedEntity = entityManager.merge(entity);
			commitTransaction(); 
			refresh(mergedEntity);
		}else{
			mergedEntity = entityManager.merge(entity);
		}
		
		return mergedEntity;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.jpa.DefaultJpaDao#delete(kanad.kore.data.entity.AbstractEntity)
	 */
	@Override
	public void delete(T entity) {
		//Soft delete
		if(entity instanceof AbstractEntity){
			((AbstractEntity)entity).setDeleted(1);
		}
		
		if(!hasActiveTransaction()){
			beginTransaction();	
			entityManager.merge(entity);
			commitTransaction(); 
		}else{
			entityManager.merge(entity);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see kanad.kore.data.dao.jpa.DefaultJpaDao#deleteHard(kanad.kore.data.entity.AbstractEntity)
	 */
	@Override
	public void deleteHard(T entity) {
		if(!hasActiveTransaction()){
			beginTransaction();	
			entityManager.remove(entity);
			commitTransaction(); 
		}else{
			entityManager.remove(entity);
		}
		
	}

	/* (non-Javadoc)
	 * @see kanad.kore.data.dao.jpa.DefaultJpaDao#deleteAll(java.lang.boolean)
	 */
	@Override
	public void deleteAll(boolean hardDelete) {
		LogManager.getLogger().info("Deleting all generic/abstract entities...");
		
		Query query = null;
		if(hardDelete){
			query = entityManager.createQuery("delete from "+clazz.getSimpleName());
		}else{
			query = entityManager.createQuery("update "+clazz.getSimpleName()+" set deleted = 1");
		}
		
		if(!hasActiveTransaction()){
			beginTransaction();
			query.executeUpdate();
			commitTransaction();
		}else{
			query.executeUpdate();
		}
	}

	
	/* (non-Javadoc)
	 * kanad.kore.data.dao.jpa.DefaultJpaDao#getAll()
	 */
	@Override
	public List<T> getAll(int orgId) {
		//unsupported: subclasses shall have their specific implementation.
		LogManager.getLogger(this.getClass().getName()).info("Returning entity list...");
		
		List<T> list = new ArrayList<>();
		try {
			list = entityManager.createQuery(
					"from "+clazz.getSimpleName()+
					" where deleted = :isDeleted and orgId = :orgId ORDER BY updatedOn DESC",
					clazz)
					.setParameter("isDeleted", 0)
					.setParameter("orgId", orgId)
					.getResultList();
		} catch (NoResultException ne) {
			LogManager.getLogger(this.getClass().getName()).info("No records found");
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * kanad.kore.data.dao.DefaultJpaDao#addRemark(kanad.kore.data.entity.jpa.Remark)
	 */
	@Override
	public void addRemark(Remark remark){
		LogManager.getLogger().info("Adding remark...");
		beginTransaction();
		entityManager.persist(remark);
		commitTransaction();
		refresh(remark);
		LogManager.getLogger().info("Done persisting remarks!");
	}
	
}
