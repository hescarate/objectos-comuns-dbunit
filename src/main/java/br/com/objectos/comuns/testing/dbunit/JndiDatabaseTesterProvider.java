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
import org.dbunit.JndiDatabaseTester;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
class JndiDatabaseTesterProvider implements Provider<IDatabaseTester> {

  private final String lookupName;

  @Inject
  public JndiDatabaseTesterProvider(@Named("obj.comuns.dbunit.jndi") String lookupName) {
    this.lookupName = lookupName;
  }

  @Override
  public IDatabaseTester get() {
    return new JndiDatabaseTester(lookupName);
  }

}