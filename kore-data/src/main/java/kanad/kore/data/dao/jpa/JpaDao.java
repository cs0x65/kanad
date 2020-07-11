package kanad.kore.data.dao.jpa;

import kanad.kore.data.dao.Dao;
import kanad.kore.data.entity.KEntity;

import javax.persistence.EntityManager;

public interface JpaDao<T> extends Dao<EntityManager> {
	/**
	 * 
	 * @param t in the current persistent context, the state of which needs to be refreshed/reconciled from DB.
	 * Refreshes the state of the entity instance in the current persistent context from the database.
	 */
	void refresh(T t);
}
