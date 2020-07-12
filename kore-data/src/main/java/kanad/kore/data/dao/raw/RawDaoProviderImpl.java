package kanad.kore.data.dao.raw;

import kanad.kore.data.dao.ThreadBoundPersistentContext;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

//TODO: Provide one more variant for DataSource based connection pool compliant JDBC connection management or
// accommodate it in the same class.
//Refer:
//c3p0
//http://www.mchange.com/projects/c3p0
//https://github.com/swaldman/c3p0
//Apache DBCP
//http://commons.apache.org/proper/commons-dbcp/

public class RawDaoProviderImpl<T> implements RawDaoProvider<T> {
	//DAO base package name
	private final String packageName;
	private final Properties connProperties;
	private boolean open;
	private final Strategy strategy;
	private final ThreadLocal<ThreadBoundPersistentContext<Connection>> threadLocalManagedContext = new ThreadLocal<>();
	
	public RawDaoProviderImpl(String packageName, Properties connProperties) {
		this(packageName, connProperties, Strategy.PER_INSTANCE);
	}
	
	public RawDaoProviderImpl(String packageName, Properties connProperties, Strategy strategy) {
		this.packageName = packageName;
		this.connProperties = connProperties;
		this.strategy = strategy;
		open = true;
	}
	
	
	public Strategy getStrategy(){
		return strategy;
	}
	
	
//	@Override
//	public RawDao getDAO(String classname) {
//		return getDAO(classname, null);
//	}
//	
//	
//	/* (non-Javadoc)
//	 * kanad.kore.data.dao.DaoProvider#getDAO(java.lang.String, kanad.kore.data.dao.Dao)
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public RawDao getDAO(String classname, RawDao existingDAO) {
//		RawDao kanad.kore.data.dao = null;
//		if(!isOpen()){
//			return null;
//		}
//		
//		if(packageName != null){
//			//resolve classname w.r.t the base package name provided else use the fully qualified classname.
//			classname = packageName+"."+classname;
//		}
//		LogManager.getLogger().info("Returning the DAO for: "+classname);
//		
//		try {
//			Class<? extends RawDao> daoClass = (Class<? extends RawDao>) Class.forName(classname);
//			kanad.kore.data.dao = daoClass.newInstance();
//			
//			if(existingDAO != null){
//				LogManager.getLogger().info("Reusing existing DAO/Connection...");
//				kanad.kore.data.dao.set(((RawDao)existingDAO).get());
//			}else{
//				if(strategy == Strategy.PER_THREAD){
//					LogManager.getLogger().warn("Current strategy is: PER_THREAD, creating a new Connection per DAO is same as PER_INSTANCE; make sure you pass existing DAO to achieve the expected behavior!");
//				}
//				Connection conn = DriverManager.getConnection(connProperties.getProperty(CONN_URL), connProperties.getProperty(CONN_USERNAME), connProperties.getProperty(CONN_PASSWORD));
//				kanad.kore.data.dao.set(conn);
//			}
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			LogManager.getLogger().error("Can't create DAO!");
//			LogManager.getLogger().error("Exception : "+e);
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//			LogManager.getLogger().error("Can't create DAO!");
//			LogManager.getLogger().error("Exception : "+e);
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//			LogManager.getLogger().error("Can't create DAO!");
//			LogManager.getLogger().error("Exception : "+e);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			LogManager.getLogger().error("Can't create DAO: not able to create a JDBC connection!");
//			LogManager.getLogger().error("Exception : "+e);
//		}
//		
//		return kanad.kore.data.dao;
//	}


	@Override
	public RawDao<? extends T> getDAO(String daoClassname) {
		return getDAO(daoClassname, null, null);
	}

	@Override
	public RawDao<? extends T> getDAO(String daoClassname, RawDao<? extends T> existingDao) {
		return getDAO(daoClassname, existingDao, null);
	}

	@Override
	public RawDao<? extends T> getDAO(String daoClassname, Class<? extends T> parameterizedClass) {
		return getDAO(daoClassname, null, parameterizedClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RawDao<? extends T> getDAO(String daoClassname, RawDao<? extends T> existingDao,
									  Class<? extends T> parameterizedClass) {
		RawDao<? extends T> dao = null;
		try {
			if(packageName != null){
				//resolve classname w.r.t the base package name provided else use the fully qualified classname.
				daoClassname = packageName+"."+daoClassname;
			}
			Class<? extends RawDao<? extends T>> daoClass =
					(Class<? extends RawDao<? extends T>>) Class.forName(daoClassname);
			dao = getDAO(daoClass, existingDao, parameterizedClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		}
		return dao;
	}

	@Override
	public RawDao<? extends T> getDAO(Class<? extends RawDao<? extends T>> daoClass) {
		return getDAO(daoClass, null, null);
	}

	@Override
	public RawDao<? extends T> getDAO(Class<? extends RawDao<? extends T>> daoClass, RawDao<? extends T> existingDao) {
		return getDAO(daoClass, existingDao, null);
	}

	@Override
	public RawDao<? extends T> getDAO(Class<? extends RawDao<? extends T>> daoClass,
									  Class<? extends T> parameterizedClass) {
		return getDAO(daoClass, null, parameterizedClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RawDao<? extends T> getDAO(Class<? extends RawDao<? extends T>> daoClass, RawDao<? extends T> existingDao,
									  Class<? extends T> parameterizedClass) {
		LogManager.getLogger().info("Returning the DAO for: "+daoClass.getName());

		RawDao<? extends T> dao = null;
		try {
			if(parameterizedClass != null){
				LogManager.getLogger().info("Creating DAO instance using parameterized constructor...");
				Constructor<? extends RawDao<? extends T>> constructor = daoClass.getConstructor(Class.class);
				LogManager.getLogger().info("Parameterized constructor = "+constructor);
				if(!constructor.isAccessible()){
					constructor.setAccessible(true);
				}
				dao = constructor.newInstance(parameterizedClass);
			}else{
				dao = daoClass.newInstance();
			}

			Connection connection;
			if(strategy == Strategy.PER_THREAD){
				LogManager.getLogger().warn("Current strategy is: PER_THREAD: one Connection instance across " +
						"all DAOs in same thread.");

				if(threadLocalManagedContext.get() == null){
					LogManager.getLogger().warn("No Thread Local Context available! Building new one...");
					ThreadBoundPersistentContext<Connection> threadBoundPersistentContext =
							new ThreadBoundPersistentContext<>();
					String username = connProperties.getProperty(CONN_USERNAME);
					String password = connProperties.getProperty(CONN_PASSWORD);
					if (username != null && password != null)
						connection = DriverManager.getConnection(connProperties.getProperty(CONN_URL),
								username,
								password);
					else
						connection = DriverManager.getConnection(connProperties.getProperty(CONN_URL));
					threadBoundPersistentContext.setPersistentContext(connection);
					threadLocalManagedContext.set(threadBoundPersistentContext);
				}else{
					LogManager.getLogger().info("Leveraging existing Connection from Managed Thread Local " +
							"Context");
					connection = threadLocalManagedContext.get().getPersistentContext();
				}

				int connectionCount = threadLocalManagedContext.get().getPersistentContextCount();
				threadLocalManagedContext.get().setPersistentContextCount(++connectionCount);
				LogManager.getLogger().info("Total Ref Count for Thread Local Connection: "+connectionCount);
			}else{
				//per instance
				if(existingDao != null){
					LogManager.getLogger().info("Current strategy is: PER_INSTANCE: Reusing existing " +
							"DAO/Connection...");
					connection = existingDao.get();
				}else{
					//No existing DAO passed and additionally, the strategy is per instance.
					LogManager.getLogger().warn("No existing DAO received; Current strategy is: PER_INSTANCE; " +
							"one Connection instance per DAO.");
					connection = DriverManager.getConnection(connProperties.getProperty(CONN_URL),
							connProperties.getProperty(CONN_USERNAME),
							connProperties.getProperty(CONN_PASSWORD));
				}
			}

			dao.set(connection);
			((AbstractRawDao<T>)dao).setProviderRef(this);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException |
				IllegalArgumentException | InvocationTargetException | SQLException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't create DAO!");
			LogManager.getLogger().error("Exception : "+e);
		}
		return dao;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}
	
	public boolean isOpen(){
		return open;
	}

	@Override
	public void close() {
		if(isOpen()){
			open = false;
			//TODO: close all connections? May be possible with ConnectionPool but what if connection pooling it not
			// used
		}
	}

	@Override
	public void closePersistentContext(Connection connection){
		LogManager.getLogger().info("Closing Connection...");
		if (strategy == Strategy.PER_THREAD){
			int connectionCount = threadLocalManagedContext.get().getPersistentContextCount();
			threadLocalManagedContext.get().setPersistentContextCount(--connectionCount);
			LogManager.getLogger().info("Total Ref Count for Thread Local EM: "+connectionCount);
			if(connectionCount == 0){
				LogManager.getLogger().info("No more active refs to Thread Local Managed Entity Manager; " +
						"Cleaning up Thread Local Managed Context");
				connection = threadLocalManagedContext.get().getPersistentContext();
				closeConnection(connection);
				threadLocalManagedContext.remove();
			}
		}else {
			closeConnection(connection);
		}
	}

	private void closeConnection(Connection connection){
		try {
			if (connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Not able to close JDBC connection: "+connection);
			LogManager.getLogger().error("Exception : "+e);
		}finally{
			connection = null;
		}
	}
}
