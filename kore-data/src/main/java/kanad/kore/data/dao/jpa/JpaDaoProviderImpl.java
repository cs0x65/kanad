package kanad.kore.data.dao.jpa;

import kanad.kore.data.dao.ThreadBoundPersistentContext;
import org.apache.logging.log4j.LogManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JpaDaoProviderImpl<T> implements JpaDaoProvider<T> {
	private final EntityManagerFactory factory;
	//DAO base package name
	private final String packageName;
	private final Strategy strategy;
	private final ThreadLocal<ThreadBoundPersistentContext<EntityManager>> threadLocalManagedContext =
			new ThreadLocal<>();
	
	public JpaDaoProviderImpl(String persistentUnit, String packageName){
		this(persistentUnit, packageName, Strategy.PER_INSTANCE);
	}
	
	public JpaDaoProviderImpl(String persistentUnit, String packageName, Strategy strategy){
		factory = Persistence.createEntityManagerFactory(persistentUnit);
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
		return getDAO(daoClassname, null, null);
	}

	@Override
	public JpaDao<? extends T> getDAO(String daoClassname, JpaDao<? extends T> existingDao) {
		LogManager.getLogger().info("Building DAO using classname:"+daoClassname+" & existing DAO: "+ existingDao);
		return getDAO(daoClassname, existingDao, null);
	}
	
	@Override
	public JpaDao<? extends T> getDAO(String daoClassname, Class<? extends T> parameterizedClass) {
		LogManager.getLogger().info("Building DAO using classname:"+daoClassname+" & parameterizedClass: "+
				parameterizedClass);
		return getDAO(daoClassname, null, parameterizedClass);
	}
	
	@Override
	public JpaDao<? extends T> getDAO(Class<? extends JpaDao<? extends T>> daoClass) {
		LogManager.getLogger().info("Building DAO using class:"+daoClass);
		return getDAO(daoClass, null, null);
	}
	
	@Override
	public JpaDao<? extends T> getDAO(Class<? extends JpaDao<? extends T>> daoClass, JpaDao<? extends T> existingDao) {
		LogManager.getLogger().info("Building DAO using class:"+daoClass+" & existing DAO: "+ existingDao);
		return getDAO(daoClass, existingDao, null);
	}
	
	@Override
	public JpaDao<? extends T> getDAO(Class<? extends JpaDao<? extends T>> daoClass,
									  Class<? extends T> parameterizedClass) {
		LogManager.getLogger().info("Building DAO using class:"+daoClass+" & parameterizedClass: "+
				parameterizedClass);
		return getDAO(daoClass, null, parameterizedClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public JpaDao<? extends T> getDAO(String daoClassname, JpaDao<? extends T> existingDAO,
													 Class<? extends T> parameterizedClass){
		JpaDao<? extends T> dao = null;
		try {
			if(packageName != null){
				//resolve classname w.r.t the base package name provided else use the fully qualified classname.
				daoClassname = packageName+"."+daoClassname;
			}
			Class<? extends JpaDao<? extends T>> daoClass =
					(Class<? extends JpaDao<? extends T>>) Class.forName(daoClassname);
			dao = getDAO(daoClass, existingDAO, parameterizedClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		}
		return dao;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JpaDao<? extends T> getDAO(Class<? extends JpaDao<? extends T>> daoClass,
												 JpaDao<? extends T> existingDAO,
												 Class<? extends T> parameterizedClass){
		LogManager.getLogger().info("Returning the DAO for: "+daoClass.getName());

		JpaDao<? extends T> dao = null;
		try {
			if(parameterizedClass != null){
				LogManager.getLogger().info("Creating DAO instance using parameterized constructor...");
				Constructor<? extends JpaDao<? extends T>> constructor = daoClass.getConstructor(Class.class);
				LogManager.getLogger().info("Parameterized constructor = "+constructor);
				if(!constructor.isAccessible()){
					constructor.setAccessible(true);
				}
				dao = constructor.newInstance(parameterizedClass);
			}else{
				dao = daoClass.newInstance();
			}
			
			EntityManager entityManager;
			if(strategy == Strategy.PER_THREAD){
				LogManager.getLogger().warn("Current strategy is: PER_THREAD: one Entity Manager instance across " +
						"all DAOs in same thread.");
				
				if(threadLocalManagedContext.get() == null){
					LogManager.getLogger().warn("No Thread Local Context available! Building new one...");
					ThreadBoundPersistentContext<EntityManager> threadBoundPersistentContext =
							new ThreadBoundPersistentContext<>();
					entityManager = factory.createEntityManager();
					threadBoundPersistentContext.setPersistentContext(entityManager);
					threadLocalManagedContext.set(threadBoundPersistentContext);
				}else{
					LogManager.getLogger().info("Leveraging existing Entity Manager from Managed Thread Local " +
							"Context");
					entityManager = threadLocalManagedContext.get().getPersistentContext();
				}
				
				int emCount = threadLocalManagedContext.get().getPersistentContextCount();
				threadLocalManagedContext.get().setPersistentContextCount(++emCount);
				LogManager.getLogger().info("Total Ref Count for Thread Local EM: "+emCount);
			}else{
				//per instance
				if(existingDAO != null){
					LogManager.getLogger().info("Current strategy is: PER_INSTANCE: Reusing existing " +
							"DAO/Entity Manager...");
					entityManager = existingDAO.get();
				}else{
					//No existing DAO passed and additionally, the strategy is per instance.
					LogManager.getLogger().warn("No existing DAO received; Current strategy is: PER_INSTANCE; " +
							"one Entity Manager instance per DAO.");
					entityManager = factory.createEntityManager();
				}
			}
			
			dao.set(entityManager);
			((AbstractJpaDao<T>)dao).setJpaDaoProvider(this);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException |
				IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		}
		return dao;
	}

	@Override
	public void close() {
		if(factory.isOpen()){
			LogManager.getLogger().info("Closing EntityManagerFactory...");
			factory.close();
		}
	}

	@Override
	public void closePersistentContext(EntityManager entityManager){
		LogManager.getLogger().info("Closing EntityManager...");
		if (strategy == Strategy.PER_THREAD){
			LogManager.getLogger().info("Closing Thread Local Managed Entity Manager...");
			int emCount = threadLocalManagedContext.get().getPersistentContextCount();
			threadLocalManagedContext.get().setPersistentContextCount(--emCount);
			LogManager.getLogger().info("Total Ref Count for Thread Local EM: "+emCount);
			if(emCount == 0){
				LogManager.getLogger().info("No more active refs to Thread Local Managed Entity Manager; " +
						"Cleaning up Thread Local Managed Context");
				entityManager = threadLocalManagedContext.get().getPersistentContext();
				closeEntityManager(entityManager);
				threadLocalManagedContext.remove();
			}
		}else {
			closeEntityManager(entityManager);
		}
	}

	private void closeEntityManager(EntityManager entityManager){
		if(entityManager != null && entityManager.isOpen()){
			entityManager.close();
			entityManager = null;
		}
	}
}
