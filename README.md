HashiCorp Vault Credential Resolver for ServiceNow JDBC Datasources

This project provides a custom JDBC driver proxy for ServiceNow that enables secure retrieval of database passwords from HashiCorp Vault at runtime.

Overview

Overrides the JDBC driver's connect() method to intercept connection requests.

Supports a custom URL scheme that converts to a standard Oracle JDBC URL.

Reads the database password from HashiCorp Vault dynamically, using Vault's HTTP API.

Accepts Vault secret path in the JDBC datasource password field (only if starting with data/).

Automatically fetches and injects the decrypted password before establishing the DB connection.

Integrates with Vault Agent running locally (e.g., on MID Server) to transparently forward authentication tokens.

As of now it supports only Oracle JDBC connections. It can be easily extended to other DBs by updating the Java class. 


![image](https://github.com/user-attachments/assets/de40acc1-d623-42c1-b99a-e76e0bb651e9)



Benefits

Eliminates hard-coded passwords in ServiceNow datasource configurations which can be decrypted by admins.

Centralizes secret management in Vault with auditability.

Enhances security by leveraging Vault Agent token caching and renewal.

Easily extendable to other secret backends.

Usage


Configure Vault Agent with auto-auth and token caching on the MID Server.https://releases.hashicorp.com/vault/1.16.9+ent/

Set the ServiceNow datasource password field with the Vault secret path, e.g., data/XXXX/SNXXX/PROD/....

Set the JDBC oracle url with custom format jdbc:customoracle: or depending on how you configured in the JAVA class. 

Create a new format choice eg.Prox Driver with value 'com.snresolver.ProxySQLDriver ' in datasource record which can be set at datasource level.

Ensure the vault agent url is updated in JAVA class or use mid server properties. 

Deploy the custom proxy JDBC driver jar in ServiceNow MID Server classpath.

The driver automatically resolves the Vault password at runtime.
