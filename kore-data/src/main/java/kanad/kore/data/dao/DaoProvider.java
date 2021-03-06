package kanad.kore.data.dao;

public interface DaoProvider<I, T, D extends Dao<I, ? extends T>> {
	enum Strategy {
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
	 * @param daoClassname the name of the DAO class
	 * @return the Dao instance for the corresponding class.
	 * The implementation shall provide the Dao instance with a new instance of EntityManager or JDBC connection.
	 */
	D getDAO(String daoClassname);
	
	
	/**
	 * 
	 * @param daoClassname Please make sure you provide the fully qualified classname if the base package name
	 * is not set on the underlying Dao provider implementation; otherwise a simple classname suffices.
	 * @param existingDao an existing instance of the DAO
	 * @return  the Dao instance for the corresponding class constructed using
	 * an existing instance of underlying DB machanism present in existing DAO.
	 *
	 * Typically, this is needed in case of transaction(s) that span across multiple DAOs.
	 * Use this version only if necessary, default one will suffice for most of the purposes.
	 * This variant uses an existing instance of an underlying DB mechanism (e.g. EntityManager in case of JpaDao or
	 * JDBC Connection in case of RawDao) when creating the new DAO.
	 */
	D getDAO(String daoClassname, D existingDao);

	/**
	 * 
	 * @param daoClassname the name of the DAO class
	 * @param parameterizedClass the class name of the parameterized type required by the corresponding DAO class.
	 * @return
	 */
	D getDAO(String daoClassname, Class<? extends T> parameterizedClass);

	/**
	 *
	 * @param daoClassname the name of the DAO class
	 * @param existingDao
	 * @param parameterizedClass the class name of the parameterized type required by the corresponding DAO class.
	 * @return
	 */
	D getDAO(String daoClassname, D existingDao, Class<? extends T> parameterizedClass);
	
	
	/**
	 * 
	 * @param daoClass the DAO class
	 * @return the DAO instance for the corresponding class.
	 * The implementation shall provide the DAO instance with a new instance of EntityManager or JDBC connection.
	 */
	D getDAO(Class<? extends D> daoClass);

	/**
	 * 
	 * @param daoClass
	 * @param existingDao
	 * @return refer getDAO(String daoClassname, D existingDao)
	 */
	D getDAO(Class<? extends D> daoClass, D existingDao);

	/**
	 * 
	 * @param daoClass the DAO class
	 * @param parameterizedClass the class of the parameterized type required by the corresponding DAO class.
	 * @return
	 */
	D getDAO(Class<? extends D> daoClass, Class<? extends T> parameterizedClass);

	/**
	 *
	 * @param daoClass the DAO class
	 * @param existingDao
	 * @param parameterizedClass the class name of the parameterized type required by the corresponding DAO class.
	 * @return
	 */
	D getDAO(Class<? extends D> daoClass, D existingDao, Class<? extends T> parameterizedClass);
	
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
	
	Strategy getStrategy();

	/**
	 * Closes the provider.
	 */
	void close();

	/**
	 * This method acts like a listener/observer- when the Dao is being closed, it shall invoke this method
	 * and give chance to the provider to undertake it's clean up and housekeeping related tasks.
	 * @param persistentContext close the EntityManager or the Connection provided.
	 */
	void closePersistentContext(I persistentContext);
}
