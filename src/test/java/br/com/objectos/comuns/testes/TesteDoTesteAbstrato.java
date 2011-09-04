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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
@Test
public class TesteDoTesteAbstrato extends TesteAbstrato {

  @Inject
  private Provider<EntityManager> em;

  @BeforeClass
  public void prepararClasse() {
    injectMembers();
    em.get().clear();
  }

  public void verifiqueCarregamentoDoDBUnit() {
    EntidadeOBJ obj = em.get().find(EntidadeOBJ.class, Integer.valueOf(1));

    assertEquals(obj.getValor(), "A");
  }

  public void verifiqueCorretoFuncionamentoDoArquivoDeLimpeza() {
    EntidadeOBJ obj = em.get().find(EntidadeOBJ.class, Integer.valueOf(1));
    assertEquals(obj.getValor(), "A");

    adicionarDBUnit(new ArquivoDeLimpeza());

    em.get().clear();

    obj = em.get().find(EntidadeOBJ.class, Integer.valueOf(1));
    assertNull(obj);
  }

  @Override
  protected Module configurarModulo() {
    return new ModuloDoTesteAbstrato();
  }

}