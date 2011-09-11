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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.JndiDatabaseTester;
import org.testng.annotations.Test;

import br.com.objectos.comuns.sql.JdbcCredentials;
import br.com.objectos.comuns.sql.PropertiesJdbcCredentialsProvider;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
@Test
public class DatabaseTesterModuleBuilderTest {

  public void it_should_build_a_jndi_module() {
    Module module = newBuilder() //
        .jndi("java:/DataSource") //
        .build();

    IDatabaseTester tester = Guice.createInjector(module).getInstance(IDatabaseTester.class);
    assertThat(tester, instanceOf(JndiDatabaseTester.class));
    assertThat(tester.toString(), containsString("=java:/DataSource"));
  }

  public void it_should_build_a_jdbc_module() {
    Provider<JdbcCredentials> credentials = new PropertiesJdbcCredentialsProvider(getClass());

    Module module = newBuilder() //
        .jdbc(credentials) //
        .build();

    IDatabaseTester tester = Guice.createInjector(module).getInstance(IDatabaseTester.class);

    assertThat(tester, instanceOf(JdbcDatabaseTester.class));
    assertThat(tester.toString(), containsString("=org.hsqldb.jdbcDriver"));
    assertThat(tester.toString(), containsString("=jdbc:hsqldb:mem:obj-comuns-jdbc"));
    assertThat(tester.toString(), containsString("=sa"));
    // assertThat(tester.toString(), containsString("=unbreakable")); duh! makes
    // sense
  }

  private DbunitModuleBuilder newBuilder() {
    return new DbunitModuleBuilder();
  }

}
