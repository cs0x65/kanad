package kanad.kore.data.dao;

import kanad.kore.data.dao.jpa.JpaDaoProvider;
import kanad.kore.data.dao.jpa.JpaDaoProviderImpl;
import kanad.kore.data.dao.raw.RawDaoProvider;
import kanad.kore.data.dao.raw.RawDaoProviderImpl;

import java.util.Properties;

public class DaoProviderFactory {
	public enum DaoImplementationStrategy{
		/**
		 * With per instance strategy, there is one distinct Connection or EntityManager instance 
		 * per DAO instance.
		 * For multi DAO scoped transactions, the same underlying Connection or EntityManager needs to be shared amongst
		 * multiple DAOs. In such a case, make sure you use getDAO(String classname, DAO existingDAO) method of
		 * DaoProvider which propagates the same instance of underlying Connection or EntityManager across all the DAOs
		 * involved in a transaction.
		 * Default is PER_INSTANCE.
		 */
		PER_INSTANCE,
		/**
		 * With per thread strategy, there is one distinct Connection or EntityManager instance 
		 * for all the DAOs instances in the scope of a single thread.
		 * This sort of strategy is generally used in places like requests made to web app or RESTful APIs,
		 * where the Connection or EntityManager instance is REQUEST scoped.
		 */
		PER_THREAD,
	}

	/**
	 * 
	 * @param persistentUnit
	 * @return an implementation of JpaDaoProvider
	 * This type of DAO provider shall be used with JpaDao where the data access is done using
	 * the JPA and the corresponding EntityManager/EntityManagerFactory.
	 */
	public static JpaDaoProvider create(String persistentUnit){
		return new JpaDaoProviderImpl(persistentUnit, null);
	}
	
	/**
	 * 
	 * @param persistentUnit
	 * @param strategy
	 * @return an implementation of JpaDaoProvider
	 * This type of DAO provider shall be used with JpaDao where the data access is done using
	 * the JPA and the corresponding EntityManager/ EntityManagerFactory.
	 */
	public static JpaDaoProvider create(String persistentUnit, DaoImplementationStrategy strategy){
		return new JpaDaoProviderImpl(persistentUnit, null, strategy);
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
	public static JpaDaoProvider create(String persistentUnit, String daoPackageName){
		return new JpaDaoProviderImpl(persistentUnit, daoPackageName);
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
	public static JpaDaoProvider create(String persistentUnit, String daoPackageName,
										DaoImplementationStrategy strategy){
		return new JpaDaoProviderImpl(persistentUnit, daoPackageName, strategy);
	}

	/**
	 * @return an implementation of RawDaoProvider
	 * This type of DAO provider shall be used with RawDao where the
	 * data access is done directly over the native JDBC connection.
	 */
	public static RawDaoProvider create(Properties connProperties){
		return new RawDaoProviderImpl(null, connProperties);
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
	public static RawDaoProvider create(Properties connProperties, DaoImplementationStrategy strategy){
		return new RawDaoProviderImpl(null, connProperties, strategy);
	}
	
	/**
	 * 
	 * @param daoPackageName
	 * @return an implementation of RawDaoProvider
	 * This type of DAO provider shall be used with RawDao where the
	 * data access is done directly over the native JDBC connection.
	 */
	public static RawDaoProvider create(String daoPackageName, Properties connProperties){
		return new RawDaoProviderImpl(daoPackageName, connProperties);
	}

	/**
	 * 
	 * @param daoPackageName
	 * @return an implementation of RawDaoProvider
	 * @param strategy the strategy to be used by implementation for managing underlying Connection.
	 * This type of DAO provider shall be used with RawDao where the
	 * data access is done directly over the native JDBC connection.
	 */
	public static RawDaoProvider create(String daoPackageName, Properties connProperties,
										DaoImplementationStrategy strategy){
		return new RawDaoProviderImpl(daoPackageName, connProperties, strategy);
	}
}
