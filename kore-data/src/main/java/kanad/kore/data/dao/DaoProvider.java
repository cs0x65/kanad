package kanad.kore.data.dao;

import kanad.kore.data.dao.DaoProviderFactory.DaoImplementationStrategy;

public interface DaoProvider<DAO, T> {
	/**
	 * 
	 * @param daoClassname the name of the DAO class
	 * @return the DAO instance for the corresponding class.
	 * The implementation shall provide the DAO instance with a new instance of EntityManager or JDBC connection.
	 */
	DAO getDAO(String daoClassname);
	
	
	/**
	 * 
	 * @param daoClassname. Please make sure you provide the fully qualified classname if the base package name
	 * is not set on the underlying DAO provider implementation; otherwise a simple classname suffices.
	 * @param existingDAO
	 * @return  the DAO instance for the corresponding class constructed using the
	 * an existing instance of underlying DB machanism present in existing DAO.
	 * 
	 * Use this variant when you want to provide an existing instance of underlying
	 * DB machanism (e.g. EntityManager in case of JpaDao or JDBC Connection in case of RawDao) 
	 * to the new DAO. You may typically need this kind of scenario in case of transactions that
	 * span across multiple DAOs. Use this version only if necessary, default one will suffice
	 * for most of the purposes.
	 */
	DAO getDAO(String daoClassname, DAO existingDAO);

	/**
	 * 
	 * @param daoClassname the name of the DAO class
	 * @param parameterizedClass the class name of the parameterized type required by the corresponding DAO class.
	 * @return
	 */
	DAO getDAO(String daoClassname, Class<? extends T> parameterizedClass);
	
	
	/**
	 * 
	 * @param daoClass the DAO class
	 * @return the DAO instance for the corresponding class.
	 * The implementation shall provide the DAO instance with a new instance of EntityManager or JDBC connection.
	 */
	DAO getDAO(Class<? extends DAO> daoClass);

	/**
	 * 
	 * @param daoClass
	 * @param existingDAO
	 * @return refer getDAO(String daoClassname, DAO existingDAO)
	 */
	DAO getDAO(Class<? extends DAO> daoClass, DAO existingDAO);

	/**
	 * 
	 * @param daoClass the DAO class
	 * @param parameterizedClass the class of the parameterized type required by the corresponding DAO class.
	 * @return
	 */
	DAO getDAO(Class<? extends DAO> daoClass, Class<? extends T> parameterizedClass);
	
	
	/**
	 * Optional
	 * @return the base package where all the DAOs are implemented. 
	 * This method will return non null package name only when the underlying provider implementation allows to
	 * configure one. When this value is set, getDAO() method will try to locate the DAO implementation class identified
	 * by fully qualified classname: packageName.classname and return it's instance.
	 *
	 * This is just a convenient way to avoid passing fully qualified DAO implementation classname when all DAO
	 * classes are located in a single base package; otherwise one shall pass the fully qualified classname.
	 */
	String getPackageName();
	
	DaoImplementationStrategy getImplementationStrategy();
	
	void close();
}
