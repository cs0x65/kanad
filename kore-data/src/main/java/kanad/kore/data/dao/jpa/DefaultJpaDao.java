package kanad.kore.data.dao.jpa;

import kanad.kore.data.entity.jpa.AbstractEntity;
import kanad.kore.data.entity.jpa.Remark;

import java.util.Collection;
import java.util.List;


/**
 * This JPA DAO is a generic one i.e. - interface lays down the contract for all the data access operations that are
 * commonly expected while building any CRUD application. All the concrete, purpose specific DAOs in the given
 * application will either implement this interface or extend from a class which provides the implementation of this
 * interface.
 * 
 * Let's say- there's an application for trip management module/microservice, then:
 * - all Trip Management concrete DAOs will either implement this interface or
 * - extend from a class, say - TripMgmtJpaDao which provides an implementation of this interface.
 */
public interface DefaultJpaDao<T> extends JpaDao<T>{
	//TODO: Dont make  getById and getBySecureId parameterized; instead have them return AbstractEntity and
	// keep rest of the methods parameterized.
	
	/**
	 * 
	 * @param id of the entity in the underlying DB.
	 * @return returns an entity matching the given id. 
	 */
	public AbstractEntity getById(long id);
	
	/**
	 * 
	 * @param secureId
	 * @return returns an entity matching the given secureID.
	 */
	public AbstractEntity getBySecureId(String secureId);
	
	/**
	 * 
	 * @param entity to be persisted/added into DB.
	 */
	public void add(T entity);
	
	/**
	 * 
	 * @param entities the collection containing all the entities to be persisted.
	 */
	public void addAll(Collection<T> entities);
	
	/**
	 * 
	 * @param entity to be updated and merged with the underlying entity in the DB.
	 * @return merged entity post updation.
	 */
	public T update(T entity);
	
	/**
	 * 
	 * @param entity entity to be soft deleted.
	 */
	public void delete(T entity);
	
	
	/**
	 * 
	 * @param entity entity to be hard deleted.
	 */
	public void deleteHard(T entity);
	
	
	/**
	 * Delete all the entities of <T> from DB.
	 */
	public void deleteAll(boolean hardDelete);
	
	/**
	 * 
	 * @param orgId 
	 * @return all the entities (obviously concrete subclass instances) corresponding to a given org id.
	 * The concrete subclasses shall override this method and return all entities pertaining to a concrete subclasses.
	 */	
	public List<T> getAll(int orgId);
	
	
	/**
	 * 
	 * @param remark the remark to persist to the DB.
	 * Use this method only when adding new remark on an existing entity which is being updated/merged.
	 */
	public void addRemark(Remark remark);
}
