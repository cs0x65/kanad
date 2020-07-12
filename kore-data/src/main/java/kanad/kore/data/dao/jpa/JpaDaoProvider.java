package kanad.kore.data.dao.jpa;

import kanad.kore.data.dao.DaoProvider;

import javax.persistence.EntityManager;

public interface JpaDaoProvider<T> extends DaoProvider<EntityManager, T, JpaDao<? extends T>> {

}
