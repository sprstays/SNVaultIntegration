package com.snresolver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import oracle.jdbc.OracleDriver;

public class ProxySQLDriver implements Driver {

    private static final Logger logger = Logger.getLogger(ProxySQLDriver.class.getName());

    private final OracleDriver delegate = new OracleDriver();

    private static final String CUSTOM_PREFIX = "jdbc:customoracle:";
    private static final String ORACLE_PREFIX = "jdbc:oracle:";

    private static final String AGENT_ADRESS = "http://127.0.0.1:9200"; // TODO: Use midserver properties.


    static {
        try {
            DriverManager.registerDriver(new ProxySQLDriver());

        } catch (SQLException e) {
            throw new RuntimeException("ProxySQLDriver: Failed to register ProxyOracleDriver", e);
        }
    }


    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith(CUSTOM_PREFIX);
    }


    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) return null;

        // Convert custom URL to standard Oracle URL
        String realUrl = convertToOracleUrl(url);

        // Clone and prepare properties
        Properties sanitizedProps = new Properties();
        sanitizedProps.putAll(info);

        String passwordField = info.getProperty("password");
        String vaultPass;

        if (passwordField != null && passwordField.startsWith("data/")) {

            HashiCorpVaultResolver resolver = new HashiCorpVaultResolver(AGENT_ADRESS);
            try {
                vaultPass = resolver.getPasswordById(passwordField);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            vaultPass = passwordField;
        }

        sanitizedProps.setProperty("password", vaultPass);

        return delegate.connect(realUrl, sanitizedProps);
    }

    private String convertToOracleUrl(String customUrl) {
        // Just replace the prefix, rest stays the same
        return customUrl.replaceFirst(CUSTOM_PREFIX, ORACLE_PREFIX);
    }


    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return delegate.getPropertyInfo(url, info);
    }


    public int getMajorVersion() {
        return delegate.getMajorVersion();
    }


    public int getMinorVersion() {
        return delegate.getMinorVersion();
    }


    public boolean jdbcCompliant() {
        return delegate.jdbcCompliant();
    }


    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegate.getParentLogger();
    }
}
