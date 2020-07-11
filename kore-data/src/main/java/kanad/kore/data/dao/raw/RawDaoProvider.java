package kanad.kore.data.dao.raw;

import kanad.kore.data.dao.DaoProvider;

public interface RawDaoProvider<D, T> extends DaoProvider<D, T> {
	public static final String CONN_URL="conn.url";
	public static final String CONN_USERNAME="conn.username";
	public static final String CONN_PASSWORD="conn.password";
	public static final String CONN_DRIVER="conn.driver";
}
