/*
 * ObjectosComunsDbunitModule.java criado em 04/09/2011
 * 
 * Propriedade de Objectos Fábrica de Software LTDA.
 * Reprodução parcial ou total proibida.
 */
package br.com.objectos.comuns.testing.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;

import br.com.objectos.comuns.sql.JdbcCredentials;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class DbunitModuleBuilder {

  private Vendor vendor = Vendor.HSQLDB;

  public JndiModuleBuilder jndi(String lookupName) {
    return new JndiModuleBuilder(lookupName);
  }

  public JdbcModuleBuilder jdbc(Provider<JdbcCredentials> credentialsProvider) {
    return new JdbcModuleBuilder(credentialsProvider);
  }

  private static class BaseModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(IDatabaseConnection.class).toProvider(IDatabaseConnectionProvider.class);
      bind(DBUnit.class);
    }
  }

  public class JndiModuleBuilder {

    private final String lookupName;

    public JndiModuleBuilder(String lookupName) {
      this.lookupName = lookupName;
    }

    public JndiModuleBuilder withMysql() {
      vendor = Vendor.MYSQL;
      return this;
    }

    public Module build() {
      return new AbstractModule() {
        @Override
        protected void configure() {
          install(new BaseModule());

          bind(IDatabaseTester.class) //
              .toProvider(JndiDatabaseTesterProvider.class) //
              .in(Scopes.SINGLETON);

          bind(String.class) //
              .annotatedWith(Names.named("obj.comuns.dbunit.jndi")) //
              .toInstance(lookupName);

          bind(Vendor.class).toInstance(vendor);
        }
      };
    }

  }

  public class JdbcModuleBuilder {

    private final String driverClass;

    private final String url;

    private final String username;

    private final String password;

    public JdbcModuleBuilder(Provider<JdbcCredentials> credentialsProvider) {
      JdbcCredentials credentials = credentialsProvider.get();

      Preconditions.checkNotNull(credentials);

      this.driverClass = credentials.getDriverClass();
      this.url = credentials.getUrl();
      this.username = credentials.getUser();
      this.password = credentials.getPassword();
    }

    public JdbcModuleBuilder withMysql() {
      vendor = Vendor.MYSQL;
      return this;
    }

    public Module build() {
      Preconditions.checkNotNull(url, "JBDC connection url cannot be null");
      Preconditions.checkNotNull(username, "JDBC connection username cannot be null");
      Preconditions.checkNotNull(password, "JDBC connection password cannot be null");

      return new AbstractModule() {
        @Override
        protected void configure() {
          try {
            install(new BaseModule());

            JdbcDatabaseTester tester = new JdbcDatabaseTester(driverClass, url, username, password);

            bind(IDatabaseTester.class) //
                .toInstance(tester);

            bind(Vendor.class).toInstance(vendor);
          } catch (ClassNotFoundException e) {
            addError(e);
          }
        }
      };
    }

  }

}