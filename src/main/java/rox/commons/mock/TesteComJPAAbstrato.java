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
package rox.commons.mock;

/*
 * TesteComJPAAbstrato.java
 * Created: 29/10/2009
 * By: mendo 
 */

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.naming.NamingException;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import br.com.objectos.comuns.testes.GeradorDePersistenceXml;

import com.google.inject.persist.PersistService;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public abstract class TesteComJPAAbstrato extends TesteComBancoDeDadosAbstrato {

  @BeforeSuite(groups = { "jpa" }, dependsOnGroups = { "bancoDeDados" })
  public void prepararJPA() throws PropertyVetoException, IllegalStateException, NamingException,
      IOException, URISyntaxException {
    prepararServicos();
  }

  protected void prepararServicos() throws IOException, URISyntaxException {
    getInstance(GeradorDePersistenceXml.class).gerar();

    PersistService service = getInstance(PersistService.class);
    service.start();
  }

  @AfterSuite(groups = { "jpa" })
  public void terminarSuiteDeTestes() {
    PersistService service = getInstance(PersistService.class);
    service.stop();
  }

}