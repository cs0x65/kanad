package kanad.kore.data.dao.jpa;

import kanad.kore.data.dao.Dao;

import javax.persistence.EntityManager;

public interface JpaDao<T> extends Dao<EntityManager, T> {

}
