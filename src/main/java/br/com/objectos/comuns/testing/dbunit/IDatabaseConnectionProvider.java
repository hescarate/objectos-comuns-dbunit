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
import org.dbunit.database.IDatabaseConnection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
@Singleton
class IDatabaseConnectionProvider implements Provider<IDatabaseConnection> {

  private final IDatabaseTester databaseTester;

  @Inject
  IDatabaseConnectionProvider(IDatabaseTester databaseTester) {
    this.databaseTester = databaseTester;
  }

  @Override
  public IDatabaseConnection get() {
    try {
      IDatabaseConnection connection = databaseTester.getConnection();

      boolean tableNames = obterPropriedade();
      connection.getConfig().setFeature(DBUnit.QUALIFIED_TABLE_NAMES, tableNames);

      return connection;
    } catch (Exception e) {
      return null;
    }
  }

  private boolean obterPropriedade() {
    String property = System.getProperty(DBUnit.QUALIFIED_TABLE_NAMES);
    return property == null ? true : Boolean.valueOf(property);
  }

}