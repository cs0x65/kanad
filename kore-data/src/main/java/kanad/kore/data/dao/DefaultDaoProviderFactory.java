package kanad.kore.data.dao;

import kanad.kore.data.dao.jpa.JpaDao;
import kanad.kore.data.dao.jpa.JpaDaoProvider;
import kanad.kore.data.dao.jpa.JpaDaoProviderImpl;
import kanad.kore.data.dao.raw.RawDao;
import kanad.kore.data.dao.raw.RawDaoProvider;
import kanad.kore.data.dao.raw.RawDaoProviderImpl;
import kanad.kore.data.entity.KEntity;

import java.util.Properties;

public class DefaultDaoProviderFactory {

	/**
	 * 
	 * @param persistentUnit
	 * @return an implementation of JpaDaoProvider
	 * This type of DAO provider shall be used with JpaDao where the data access is done using
	 * the JPA and the corresponding EntityManager/EntityManagerFactory.
	 */
	public static JpaDaoProvider<JpaDao<KEntity>> create(String persistentUnit){
		return new JpaDaoProviderImpl<>(persistentUnit, null);
	}
	
	/**
	 * 
	 * @param persistentUnit
	 * @param strategy
	 * @return an implementation of JpaDaoProvider
	 * This type of DAO provider shall be used with JpaDao where the data access is done using
	 * the JPA and the corresponding EntityManager/ EntityManagerFactory.
	 */
	public static JpaDaoProvider<JpaDao<KEntity>> create(String persistentUnit, DaoProvider.Strategy strategy){
		return new JpaDaoProviderImpl<>(persistentUnit, null, strategy);
	}
	
	/**
	 * 
	 * @param persistentUnit
	 * @param daoPackageName
	 * @return an implementation of JpaDaoProvider
	 * This type of DAO provider shall be used with JpaDao where the
	 * data access is done using the JPA and the corresponding EntityManager/
	 * EntityManagerFactory.
	 */
	public static JpaDaoProvider<JpaDao<KEntity>> create(String persistentUnit, String daoPackageName){
		return new JpaDaoProviderImpl<>(persistentUnit, daoPackageName);
	}
	
	/**
	 * 
	 * @param persistentUnit
	 * @param daoPackageName
	 * @param strategy the strategy to be used by implementation for managing underlying EntityManager.
	 * @return an implementation of JpaDaoProvider
	 * This type of DAO provider shall be used with JpaDao where the data access is done using
	 * the JPA and the corresponding EntityManager/EntityManagerFactory.
	 */
	public static JpaDaoProvider<JpaDao<KEntity>> create(String persistentUnit, String daoPackageName,
										DaoProvider.Strategy strategy){
		return new JpaDaoProviderImpl<>(persistentUnit, daoPackageName, strategy);
	}

	/**
	 * @return an implementation of RawDaoProvider
	 * This type of DAO provider shall be used with RawDao where the
	 * data access is done directly over the native JDBC connection.
	 */
	public static RawDaoProvider<RawDao<KEntity>> create(Properties connProperties){
		return new RawDaoProviderImpl<>(null, connProperties);
	}
	
	/**
	 * 
	 * @param connProperties
	 * @param strategy
	 * @return an implementation of RawDaoProvider
	 * This type of DAO provider shall be used with RawDao where the
	 * data access is done directly over the native JDBC connection.
	 * 
	 */
	public static RawDaoProvider<RawDao<KEntity>> create(Properties connProperties, DaoProvider.Strategy strategy){
		return new RawDaoProviderImpl<>(null, connProperties, strategy);
	}
	
	/**
	 * 
	 * @param daoPackageName
	 * @return an implementation of RawDaoProvider
	 * This type of DAO provider shall be used with RawDao where the
	 * data access is done directly over the native JDBC connection.
	 */
	public static RawDaoProvider<RawDao<KEntity>> create(String daoPackageName, Properties connProperties){
		return new RawDaoProviderImpl<>(daoPackageName, connProperties);
	}

	/**
	 * 
	 * @param daoPackageName
	 * @return an implementation of RawDaoProvider
	 * @param strategy the strategy to be used by implementation for managing underlying Connection.
	 * This type of DAO provider shall be used with RawDao where the
	 * data access is done directly over the native JDBC connection.
	 */
	public static RawDaoProvider<RawDao<KEntity>> create(String daoPackageName, Properties connProperties,
										DaoProvider.Strategy strategy){
		return new RawDaoProviderImpl<>(daoPackageName, connProperties, strategy);
	}
}