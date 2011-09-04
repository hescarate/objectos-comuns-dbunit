/*
 * Copyright 2011 Objectos, Fábrica de Software LTDA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.objectos.comuns.testing.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;

import br.com.objectos.comuns.sql.JdbcCredentials;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class DatabaseTesterModuleBuilder {

  private Vendor vendor = Vendor.HSQLDB;

  public JndiModuleBuilder jndi(String lookupName) {
    return new JndiModuleBuilder(lookupName);
  }

  public JdbcModuleBuilder jdbc(JdbcCredentials credentials) {
    return new JdbcModuleBuilder(credentials);
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

    public JdbcModuleBuilder(JdbcCredentials credentials) {
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