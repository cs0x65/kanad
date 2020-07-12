package kanad.kore.data.dao.jpa;

import kanad.kore.data.dao.PerThreadManagedContext;
import org.apache.logging.log4j.LogManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JpaDaoProviderImpl<T> implements JpaDaoProvider<T> {
	private EntityManagerFactory emf;
	//DAO base package name
	private String packageName;
	private Strategy strategy;
	private ThreadLocal<PerThreadManagedContext> threadLocalManagedContext = new ThreadLocal<>();
	
	public JpaDaoProviderImpl(String persistentUnit, String packageName){
		this(persistentUnit, packageName, Strategy.PER_INSTANCE);
	}
	
	public JpaDaoProviderImpl(String persistentUnit, String packageName, Strategy strategy){
		emf = Persistence.createEntityManagerFactory(persistentUnit);
		this.packageName = packageName;
		this.strategy = strategy;
	}
	
	
	public Strategy getStrategy(){
		return strategy;
	}
	
		
	public String getPackageName() {
		return packageName;
	}

	@Override
	public JpaDao<? extends T> getDAO(String daoClassname) {
		LogManager.getLogger().info("Building DAO using classname:"+daoClassname);
		return createDaoByClassname(daoClassname, null, null);
	}
	
	
	@Override
	public JpaDao<? extends T> getDAO(String daoClassname, JpaDao<? extends T> existingDao) {
		LogManager.getLogger().info("Building DAO using classname:"+daoClassname+" & existing DAO: "+ existingDao);
		return createDaoByClassname(daoClassname, existingDao, null);
	}
	
	
	/* (non-Javadoc)
	 * @see kanad.kore.data.dao.DaoProvider#getDAO(java.lang.String, java.lang.Class)
	 */
	@Override
	public JpaDao<? extends T> getDAO(String daoClassname, Class<? extends T> parameterizedClass) {
		LogManager.getLogger().info("Building DAO using classname:"+daoClassname+" & parameterizedClass: "+
				parameterizedClass);
		return createDaoByClassname(daoClassname, null, parameterizedClass);
	}
	
	
	/* (non-Javadoc)
	 * @see kanad.kore.data.dao.DaoProvider#getDAO(java.lang.Class)
	 */
	@Override
	public JpaDao<? extends T> getDAO(Class<? extends JpaDao<? extends T>> daoClass) {
		LogManager.getLogger().info("Building DAO using class:"+daoClass);
		return createDaoByClass(daoClass, null, null);
	}
	
	@Override
	public JpaDao<? extends T> getDAO(Class<? extends JpaDao<? extends T>> daoClass, JpaDao<? extends T> existingDao) {
		LogManager.getLogger().info("Building DAO using class:"+daoClass+" & existing DAO: "+ existingDao);
		return createDaoByClass(daoClass, existingDao, null);
	}
	
	@Override
	public JpaDao<? extends T> getDAO(Class<? extends JpaDao<? extends T>> daoClass,
									  Class<? extends T> parameterizedClass) {
		LogManager.getLogger().info("Building DAO using class:"+daoClass+" & parameterizedClass: "+
				parameterizedClass);
		return createDaoByClass(daoClass, null, parameterizedClass);
	}

	@Override
	public JpaDao<T> getDAO(String daoClassname, JpaDao<? extends T> existingDao,
							Class<? extends T> parameterizedClass ) {
		return null;
	}

	@Override
	public JpaDao<T> getDAO(Class<? extends JpaDao<? extends T>> daoClass, JpaDao<? extends T> existingDao,
									  Class<? extends T> parameterizedClass) {
		return null;
	}

	@SuppressWarnings("unchecked")
	private JpaDao<? extends T> createDaoByClassname(String daoClassname, JpaDao<? extends T> existingDAO,
													 Class<? extends T> parameterizedClass){
		JpaDao<? extends T> dao = null;
		try {
			if(packageName != null){
				//resolve classname w.r.t the base package name provided else use the fully qualified classname.
				daoClassname = packageName+"."+daoClassname;
			}
			Class<? extends JpaDao<? extends T>> daoClass =
					(Class<? extends JpaDao<? extends T>>) Class.forName(daoClassname);
			dao = createDaoByClass(daoClass, existingDAO, parameterizedClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		}
		return dao;
	}
	
	@SuppressWarnings("unchecked")
	private JpaDao<? extends T> createDaoByClass(Class<? extends JpaDao<? extends T>> daoClass,
												 JpaDao<? extends T> existingDAO,
												 Class<? extends T> parameterizedClass){
		LogManager.getLogger().info("Returning the DAO for: "+daoClass.getName());

		JpaDao<? extends T> dao = null;
		try {
			if(parameterizedClass != null){
				LogManager.getLogger().info("Creating DAO instance using parameterized constructor...");
				Constructor<? extends JpaDao<?>> constructor = daoClass.getConstructor(Class.class);
				LogManager.getLogger().info("Parameterized constructor = "+constructor);
				if(!constructor.isAccessible()){
					constructor.setAccessible(true);
				}
				dao = (JpaDao<? extends T>) constructor.newInstance(parameterizedClass);
			}else{
				dao = daoClass.newInstance();
			}
			
			EntityManager entityManager;
			if(strategy == Strategy.PER_THREAD){
				LogManager.getLogger().warn("Current strategy is: PER_THREAD: one Entity Manager instance across all DAOs in same thread.");
				
				if(threadLocalManagedContext.get() == null){
					LogManager.getLogger().warn("No Thread Local Context available! Building new one...");
					PerThreadManagedContext perThreadManagedContext = new PerThreadManagedContext();
					entityManager = emf.createEntityManager();
					perThreadManagedContext.setEntityManager(entityManager);
					threadLocalManagedContext.set(perThreadManagedContext);
				}else{
					LogManager.getLogger().info("Leveraging existing Entity Manager from Managed Thread Local Context");
					entityManager = threadLocalManagedContext.get().getEntityManager();
				}
				
				int emCount = threadLocalManagedContext.get().getEntityManagerCount();
				threadLocalManagedContext.get().setEntityManagerCount(++emCount);
				LogManager.getLogger().info("Total Ref Count for Thread Local EM: "+emCount);
			}else{
				//per instance
				if(existingDAO != null){
					LogManager.getLogger().info("Current strategy is: PER_INSTANCE: Reusing existing DAO/Entity Manager...");
					entityManager = existingDAO.get();
				}else{
					//No existing DAO passed and additionally, the strategy is per instance.
					LogManager.getLogger().warn("No existing DAO received; Current strategy is: PER_INSTANCE; one Entity Manager instance per DAO.");
					entityManager = emf.createEntityManager();
				}
			}
			
			dao.set(entityManager);
			((AbstractJpaDao<? extends T>)dao).setJpaDaoProvider(this);
		} catch (InstantiationException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		} catch (IllegalAccessException e) {
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		} catch (NoSuchMethodException e) {
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		} catch (SecurityException e) {
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		} catch (IllegalArgumentException e) {
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		} catch (InvocationTargetException e) {
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		}
		
		return dao;
	}

	protected void closeThreadLocalManagedEntityManager(){
		LogManager.getLogger().info("Closing Thread Local Managed Entity Manager...");
		int emCount = threadLocalManagedContext.get().getEntityManagerCount();
		threadLocalManagedContext.get().setEntityManagerCount(--emCount);
		LogManager.getLogger().info("Total Ref Count for Thread Local EM: "+emCount);
		if(emCount == 0){
			LogManager.getLogger().info("No more active refs to Thread Local Managed Entity Manager; Cleaning up Thread Local Managed Context");
			EntityManager entityManager = threadLocalManagedContext.get().getEntityManager();
			if(entityManager != null && entityManager.isOpen()){
				entityManager.close();
				entityManager = null;
			}
			threadLocalManagedContext.remove();
		}
	}

	public void close() {
		if(emf.isOpen()){
			LogManager.getLogger().info("Closing EntityManagerFactory...");
			emf.close();
		}
	}
}
