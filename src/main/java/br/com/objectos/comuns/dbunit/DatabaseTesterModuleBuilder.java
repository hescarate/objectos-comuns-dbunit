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
package br.com.objectos.comuns.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class DatabaseTesterModuleBuilder {

  public JndiModuleBuilder jndi(String lookupName) {
    return new JndiModuleBuilder(lookupName);
  }

  public JdbcModuleBuilder driverClass(String driverClass) {
    return new JdbcModuleBuilder(driverClass);
  }

  protected class JndiModuleBuilder {

    private final String lookupName;

    public JndiModuleBuilder(String lookupName) {
      this.lookupName = lookupName;
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
        }
      };
    }

  }

  protected class JdbcModuleBuilder {

    private final String driverClass;

    private String url;

    private String username;

    private String password;

    public JdbcModuleBuilder(String driverClass) {
      this.driverClass = driverClass;
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
          } catch (ClassNotFoundException e) {
            addError(e);
          }
        }
      };
    }

    public JdbcModuleBuilder url(String url) {
      this.url = url;
      return this;
    }

    public JdbcModuleBuilder username(String username) {
      this.username = username;
      return this;
    }

    public JdbcModuleBuilder password(String password) {
      this.password = password;
      return this;
    }

  }

}