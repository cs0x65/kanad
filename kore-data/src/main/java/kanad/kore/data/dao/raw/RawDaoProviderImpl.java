package kanad.kore.data.dao.raw;


import kanad.kore.data.dao.DefaultDaoProviderFactory.DaoImplementationStrategy;

import java.util.Properties;

//TODO: Provide one more variant for DataSource based connection pool compliant JDBC connection management or
// accommodate it in the same class.
//Refer:
//c3p0
//http://www.mchange.com/projects/c3p0
//https://github.com/swaldman/c3p0
//Apache DBCP
//http://commons.apache.org/proper/commons-dbcp/

public class RawDaoProviderImpl<D extends RawDao, T> implements RawDaoProvider<D, T> {
	//DAO base package name
	private String packageName;
	private Properties connProperties;
	private boolean open;
	private DaoImplementationStrategy strategy;
	
	
	public RawDaoProviderImpl(String packageName, Properties connProperties) {
		this(packageName, connProperties, DaoImplementationStrategy.PER_INSTANCE);
	}
	
	
	public RawDaoProviderImpl(String packageName, Properties connProperties, DaoImplementationStrategy strategy) {
		this.packageName = packageName;
		this.connProperties = connProperties;
		this.strategy = strategy;
		open = true;
	}
	
	
	public DaoImplementationStrategy getImplementationStrategy(){
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
//				if(strategy == DaoImplementationStrategy.PER_THREAD){
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
	public D getDAO(String daoClassname) {
		return null;
	}

	@Override
	public D getDAO(String daoClassname, D existingDao) {
		return null;
	}

	@Override
	public D getDAO(String daoClassname, Class<? extends T> parameterizedClass) {
		return null;
	}

	@Override
	public D getDAO(String daoClassname, Class<? extends T> parameterizedClass, D existingDao) {
		return null;
	}

	@Override
	public D getDAO(Class<? extends D> daoClass) {
		return null;
	}

	@Override
	public D getDAO(Class<? extends D> daoClass, D existingDao) {
		return null;
	}

	@Override
	public D getDAO(Class<? extends D> daoClass, Class<? extends T> parameterizedClass) {
		return null;
	}

	@Override
	public D getDAO(Class<? extends D> daoClass, Class<? extends T> parameterizedClass, D existingDao) {
		return null;
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
		}
	}
}
