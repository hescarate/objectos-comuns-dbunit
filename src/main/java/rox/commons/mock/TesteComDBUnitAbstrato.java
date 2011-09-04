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
 * TesteComDBUnitAbstrato.java
 * Created: 29/10/2009
 * By: mendo 
 */

import static com.google.common.collect.Lists.newArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.dbunit.JndiDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public abstract class TesteComDBUnitAbstrato extends TesteComJPAAbstrato {

  private JndiDatabaseTester databaseTester;

  private List<String> nomesDosArquivos = newArrayList();

  protected abstract void selecionarDataSets();

  protected void novoDataSet(String nomeDoArquivo) {
    nomesDosArquivos.add(nomeDoArquivo);
  }

  @BeforeClass
  @Parameters( { "dbunit" })
  public void prepararDBUnit(@Optional("true") boolean ativo) throws Exception {
    if (ativo) {
      String jndi = JNDI;
      databaseTester = new JndiDatabaseTester(jndi);

      selecionarDataSets();

      IDatabaseConnection connection = databaseTester.getConnection();

      connection.getConfig().setFeature(
          "http://www.dbunit.org/features/qualifiedTableNames",
          dbUnitQualifiedTableNames());

      try {
        disableReferentialIntegrity(connection);

        for (String nomeDoArquivo : nomesDosArquivos) {
          IDataSet dataSet = criarDataSet(nomeDoArquivo);
          DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        }

        enableReferentialIntegrity(connection);
      } finally {
        connection.close();
      }
    }
  }

  private IDataSet criarDataSet(String nomeDoArquivo) {
    File diretorioBase = new File("src/test/resources/dbunit");
    File arquivo = new File(diretorioBase, nomeDoArquivo);
    InputStream in = null;
    try {
      in = new FileInputStream(arquivo);
      if (nomeDoArquivo.endsWith(".gz")) {
        in = new GZIPInputStream(in);
      }
      Reader reader = new BufferedReader(new InputStreamReader(in));
      IDataSet dataSet = new FlatXmlDataSet(reader, false, true, false);
      return dataSet;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao adicionar data set DBunit", e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          // gulp!!!
        }
      }
    }
  }

  protected boolean dbUnitQualifiedTableNames() {
    return false;
  }

  /*
   * Chupinhado de AbstractDBUnitSeamTest.
   * 
   * Removida a parte de MySQL...
   */
  protected void disableReferentialIntegrity(IDatabaseConnection con) {
    try {
      con.getConnection().prepareStatement("set referential_integrity FALSE")
          .execute(); // HSQL DB
    } catch (Exception ex) {
      // gulp!
    }

    try {
      con.getConnection().prepareStatement("set foreign_key_checks=0")
          .execute();
    } catch (Exception ex) {
      // gulp!
    }
  }

  /*
   * Chupinhado de AbstractDBUnitSeamTest.
   * 
   * Removida a parte de MySQL...
   */
  protected void enableReferentialIntegrity(IDatabaseConnection con) {
    try {
      con.getConnection().prepareStatement("set referential_integrity TRUE")
          .execute(); // HSQL DB
    } catch (Exception ex) {
      // gulp!
    }

    try {
      con.getConnection().prepareStatement("set foreign_key_checks=1")
          .execute();
    } catch (Exception ex) {
      // gulp!
    }
  }

}
