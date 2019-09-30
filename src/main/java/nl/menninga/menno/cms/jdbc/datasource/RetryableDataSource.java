package nl.menninga.menno.cms.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * Robust Datasource reference. 
 * Application keeps retrying to connect with database and does not stop after failing to connect
 *
 */
public class RetryableDataSource extends HikariDataSource {
	private DataSource delegate;
	public RetryableDataSource(DataSource delegate) {
		this.delegate = delegate;
	}
	@Override
	@Retryable(maxAttempts=100, backoff=@Backoff(delay=5000, multiplier=2.3, maxDelay=60000))
	public Connection getConnection() throws SQLException {
		return delegate.getConnection();
	}
	@Override
	@Retryable(maxAttempts=100, backoff=@Backoff(delay=5000, multiplier=2.3, maxDelay=60000))
	public Connection getConnection(String username, String password)
			throws SQLException {
		return delegate.getConnection(username, password);
	}
	
} 
