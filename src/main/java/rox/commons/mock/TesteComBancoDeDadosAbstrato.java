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
 * TesteComBancoDeDadosAbstrato.java
 * Created: 29/10/2009
 * By: mendo 
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.google.common.collect.ImmutableList;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public abstract class TesteComBancoDeDadosAbstrato extends
    TesteComGuiceAbstrato {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  protected static String JNDI = "java:/MockDS";

  @Parameters({
      "driverClass",
      "jdbcUrl",
      "user",
      "password" })
  @BeforeSuite(groups = { "bancoDeDados" })
  public void prepararBancoDeDados(
      @Optional("org.hsqldb.jdbcDriver") String driverClass,
      @Optional("jdbc:hsqldb:mem:test") String jdbcUrl,
      @Optional("sa") String user, @Optional("") String password) {

    try {
      ComboPooledDataSource dataSource = new com.mchange.v2.c3p0.ComboPooledDataSource();
      dataSource.setDriverClass(driverClass);
      dataSource.setJdbcUrl(jdbcUrl);
      dataSource.setUser(user);
      dataSource.setPassword(password);

      criarCatalogosParaHSQLDB(driverClass, jdbcUrl, user, password);

      // bind no JNDI
      if (SimpleNamingContextBuilder.getCurrentContextBuilder() == null) {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind(JNDI, dataSource);
        builder.activate();
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Erro ao tentar iniciar o banco de dados HSQL", e);
    }
  }

  protected List<String> obterCatalogos() {
    return ImmutableList.of();
  }

  private void criarCatalogosParaHSQLDB(String driverClass, String jdbcUrl,
      String user, String password) throws Exception {
    if (!driverClass.equals("org.hsqldb.jdbcDriver")) {
      return;
    }

    Class.forName(driverClass);
    Connection c = DriverManager.getConnection(jdbcUrl, user, password);
    Statement statement = c.createStatement();

    for (String catalogo : obterCatalogos()) {
      try {
        statement.execute("CREATE SCHEMA " + catalogo + " AUTHORIZATION DBA");
      } catch (SQLException e) {
        logger.warn("Criação do SCHEMA " + catalogo
            + " falhou. Assumindo que já existe.");
      }
    }

    c.commit();
    c.close();
  }

  @AfterSuite(groups = { "bancoDeDados" })
  public void terminarBancoDeDados() {
  }

}
