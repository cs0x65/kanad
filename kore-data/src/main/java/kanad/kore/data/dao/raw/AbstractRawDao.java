package kanad.kore.data.dao.raw;

import org.apache.logging.log4j.LogManager;

import java.sql.Connection;
import java.sql.SQLException;

public class AbstractRawDao<T> implements RawDao<T> {
	protected Connection connection;
	private boolean txnInProgress;

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#set(java.lang.Object)
	 */
	@Override
	public void set(Connection connection) {
		this.connection = connection;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#get()
	 */
	@Override
	public Connection get() {
		return connection;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#close()
	 */
	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Not able to close JDBC connection: "+connection);
			LogManager.getLogger().error("Exception : "+e);
		}finally{
			connection = null;
		}
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#beginTransaction()
	 * If underlying connection's auto commit mode is set to true, then this method is a no-op.
	 * @see java.sql.Connection for more details.
	 */
	@Override
	public void beginTransaction() {
		try{
			if(!connection.getAutoCommit()){
				txnInProgress = true;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			txnInProgress = false;
			LogManager.getLogger().error("Can't being transaction on connection: "+connection);
			LogManager.getLogger().error("Exception : "+e);
		}
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#commitTransaction()
	 * If underlying connection's auto commit mode is set to true, then this method is a no-op.
	 * @see java.sql.Connection for more details.
	 */
	@Override
	public void commitTransaction() {
		try{
			if(!isAutoCommit()){
				connection.commit();
			}
		}catch (SQLException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't commit ongoing transaction on connection: "+connection);
			LogManager.getLogger().error("Exception : "+e);
			LogManager.getLogger().error("Rolling back ongoing transaction... ");
			rollbackTransaction();
		}finally{
			txnInProgress = false;
		}
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#rollbackTransaction()
	 * If underlying connection's auto commit mode is set to true, then this method is a no-op.
	 * @see java.sql.Connection for more details.
	 */
	@Override
	public void rollbackTransaction() {
		try{
			if(!isAutoCommit()){
				connection.rollback();
			}
		}catch (SQLException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't rollback ongoing transaction on connection: "+connection);
			LogManager.getLogger().error("Exception : "+e);
		}finally{
			txnInProgress = false;
		}
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#hasActiveTransaction()
	 * If underlying connection's auto commit mode is set to true, then this method is irrelevant and will always return false.
	 * @see java.sql.Connection for more details.
	 */
	@Override
	public boolean hasActiveTransaction() {
		return txnInProgress;
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#setAutoCommit(boolean)
	 * If underlying connection's auto commit mode was off before calling this method and if the mode is requested to be turned on, then this method will
	 * first commit the changes pending for the ongoing transaction and subsequent call to hasActiveTransaction() will return false.
	 * If the requested mode and existing mode are unchanged, this method is no-op.
	 * @see java.sql.Connection#setAutoCommit for more details.
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) {
		try {
			if(isAutoCommit() == autoCommit){
				//no-op
				return;
			}
			//connection was not in autocommit mode earlier and now turning it on
			if(!isAutoCommit() && hasActiveTransaction()){
				//Make sure you commit ongoing transaction 1st
				LogManager.getLogger().warn("Reuest to enable auto-commit mode;commiting ongoing transaction first...");
				commitTransaction();
			}
			connection.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Can't change auto commit mode on underlying JDBC connection: "+connection);
			LogManager.getLogger().error("Exception : "+e);
		}
	}

	/* (non-Javadoc)
	 * kanad.kore.data.dao.Dao#getAutoCommit()
	 */
	@Override
	public boolean isAutoCommit() {
		boolean autoCommit = false;
		try {
			autoCommit = connection.getAutoCommit();
		} catch (SQLException e) {
			e.printStackTrace();
			LogManager.getLogger().error("Unable to get auto commit mode on underlying JDBC connection: "+connection);
			LogManager.getLogger().error("Exception : "+e);
		}
		return autoCommit;
	}

	@Override
	public void refresh(T t) {

	}
}
