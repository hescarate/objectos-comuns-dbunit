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
package br.com.objectos.comuns.testes;

import br.com.objectos.comuns.dbunit.ObjectosComunsDbunitModule;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class ModuloCOMUNS_TESTES extends AbstractModule {

  @Override
  protected void configure() {
    install(new JpaPersistModule("mockPU"));

    bind(Jndi.class).toInstance(new Jndi("java:/MockDS"));
    bind(Provedor.class).toInstance(Provedor.HSQLDB);
    bind(JdbcUrl.class).toInstance(new JdbcUrl("jdbc:hsqldb:mem:test"));
    bind(UsuarioJdbc.class).toInstance(new UsuarioJdbc("sa"));
    bind(SenhaJdbc.class).toInstance(new SenhaJdbc(""));

    install(new ObjectosComunsDbunitModule());

    bind(ServicoDeBancoDeDados.class);
  }

}