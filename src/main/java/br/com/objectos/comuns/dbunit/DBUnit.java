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

import static com.google.common.collect.Lists.asList;

import java.util.List;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.operation.DatabaseOperation;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
@Singleton
public class DBUnit {

  public static final String QUALIFIED_TABLE_NAMES = "http://www.dbunit.org/features/qualifiedTableNames";

  private final Provider<IDatabaseConnection> connections;

  private final List<DataSetSupplier> datasets;

  @Inject
  DBUnit(Provider<IDatabaseConnection> connections, List<DataSetSupplier> datasets) {
    this.connections = connections;
    this.datasets = datasets;
  }

  public void executar() {
    executar(datasets);
  }

  public void executar(DataSetSupplier primeiro) {
    List<DataSetSupplier> arquivos = ImmutableList.of(primeiro);
    executar(arquivos);
  }

  public void executar(DataSetSupplier primeiro, DataSetSupplier... outros) {
    List<DataSetSupplier> arquivos = asList(primeiro, outros);
    executar(arquivos);
  }

  private void executar(List<DataSetSupplier> arquivos) {
    try {

      executarOperacoes(arquivos);

    } catch (Exception e) {

      throw new RuntimeException("Não foi possível iniciar o serviço de DBUnit. "
          + "Por favor, verifique suas configurações e tente novamente", e);

    }
  }

  private void executarOperacoes(List<DataSetSupplier> ops) throws Exception {

    IDatabaseConnection conn = connections.get(); // don't get conned

    try {
      disableReferentialIntegrity(conn);

      for (DataSetSupplier op : ops) {

        try {

          IDataSet dataSet = op.get();

          DatabaseOperation operacao = op.getOperation();
          DatabaseOperation transacao = DatabaseOperation.TRANSACTION(operacao);
          transacao.execute(conn, dataSet);

        } catch (NoSuchTableException e) {
          String msg = String.format("\n*****\nInserção do ArquivoDBUnit:%s falhou!!!\n"
              + "Esperava a existência da tabela %s " + "mas não a encontrei.\n"
              + "Você não se esqueceu de adicionar " + "a classe JPA no persistence.xml?\n*****",
              op.getFilename(), e.getMessage());
          throw new DBUnitSetupException(msg);
        }
      }

      enableReferentialIntegrity(conn);
    } finally {
      conn.close();
    }

  }

  protected void disableReferentialIntegrity(IDatabaseConnection con) {
    justDoIt(con, "set referential_integrity FALSE"); // HSQL DB 1.8
    justDoIt(con, "SET DATABASE REFERENTIAL INTEGRITY FALSE"); // HSQL DB 2.0
    justDoIt(con, "set foreign_key_checks=0"); // MySQL
  }

  protected void enableReferentialIntegrity(IDatabaseConnection con) {
    justDoIt(con, "set referential_integrity TRUE"); // HSQL DB 1.8
    justDoIt(con, "SET DATABASE REFERENTIAL INTEGRITY TRUE"); // HSQL DB 2.0
    justDoIt(con, "set foreign_key_checks=1"); // MySQL
  }

  private void justDoIt(IDatabaseConnection con, String sql) {
    try {
      con.getConnection().prepareStatement(sql).execute();
    } catch (Exception ex) {
      // yep... don't complain. We (think we) know what we're doing.
    }
  }

}