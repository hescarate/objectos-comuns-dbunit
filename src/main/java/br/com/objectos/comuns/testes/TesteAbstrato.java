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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.persistence.EntityManager;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import br.com.objectos.comuns.dbunit.DataSetSupplier;
import br.com.objectos.comuns.dbunit.DBUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public abstract class TesteAbstrato {

  private static Injector injector;

  @BeforeSuite
  public void prepararTesteAbstrato() throws IOException, URISyntaxException {
    getInstance(GeradorDePersistenceXml.class).gerar();

    ServicoDeBancoDeDados bancoDeDados = getInstance(ServicoDeBancoDeDados.class);
    PersistService service = getInstance(PersistService.class);

    bancoDeDados.iniciar();
    service.start();
  }

  @BeforeClass
  public void prepareClass() {
    limparCacheDoBancoDeDados();

    DBUnit dbUnit = getInstance(DBUnit.class);
    executarDBUnit(dbUnit);

    this.injectMembersBeforeClass();
  }

  @AfterSuite
  public void finalizarTesteAbstrato() {
    PersistService service = getInstance(PersistService.class);
    service.stop();
  }

  protected void adicionarDBUnit(DataSetSupplier arquivo) {
    DBUnit dbUnit = getInstance(DBUnit.class);
    dbUnit.executar(arquivo);
  }

  protected void adicionarDBUnit(DataSetSupplier primeiro, DataSetSupplier... outros) {
    DBUnit dbUnit = getInstance(DBUnit.class);
    dbUnit.executar(primeiro, outros);
  }

  protected <T> T buscarPorId(Class<T> classeDaEntidade, int id) {
    return getEm().find(classeDaEntidade, Integer.valueOf(id));
  }

  protected <T> T buscarPorId(Class<T> classeDaEntidade, long id) {
    return getEm().find(classeDaEntidade, Long.valueOf(id));
  }

  protected <T> T buscarPorId(Class<T> classeDaEntidade, Object id) {
    return getEm().find(classeDaEntidade, id);
  }

  protected void executarDBUnit(DBUnit dbUnit) {
    dbUnit.executar();
  }

  protected <T> T getInstance(Class<T> arg0) {
    return getInjector().getInstance(arg0);
  }

  protected final void injectMembers() {
    getInjector().injectMembers(this);
  }

  protected final void injectMembersBeforeClass() {
    getInjector().injectMembers(this);
  }

  protected void limparCacheDoBancoDeDados() {
    getEm().clear();
  }

  private EntityManager getEm() {
    return getInstance(EntityManager.class);
  }

  private Injector getInjector() {
    if (injector == null) {
      Module module = configurarModulo();
      injector = Guice.createInjector(module);
    }

    return injector;
  }

  protected abstract Module configurarModulo();

}