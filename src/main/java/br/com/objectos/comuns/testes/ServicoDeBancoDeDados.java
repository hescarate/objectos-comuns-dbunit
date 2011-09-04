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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
@Singleton
public class ServicoDeBancoDeDados {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Jndi jndi;

  private final Provedor provedor;

  private final JdbcUrl jdbcUrl;

  private final UsuarioJdbc user;

  private final SenhaJdbc password;

  private final Catalogos catalogos;

  @Inject
  ServicoDeBancoDeDados(Jndi jndi, Provedor provedor, JdbcUrl jdbcUrl,
      UsuarioJdbc user, SenhaJdbc password, Catalogos catalogos) {
    this.jndi = jndi;
    this.provedor = provedor;
    this.jdbcUrl = jdbcUrl;
    this.user = user;
    this.password = password;
    this.catalogos = catalogos;
  }

  public void iniciar() {
    try {
      ComboPooledDataSource dataSource = new com.mchange.v2.c3p0.ComboPooledDataSource();

      dataSource.setDriverClass(provedor.getClasseJDBC());
      dataSource.setJdbcUrl(jdbcUrl.getValor());
      dataSource.setUser(user.getValor());
      dataSource.setPassword(password.getValor());

      switch (provedor) {
      case HSQLDB:
        criarCatalogosParaHSQLDB();
        break;
      }

      if (SimpleNamingContextBuilder.getCurrentContextBuilder() == null) {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind(jndi.getValor(), dataSource);
        builder.activate();
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Não foi possível iniciar o serviço de banco de dados. "
              + "Por favor, verifique suas configurações e tente novamente.", e);
    }
  }

  private void criarCatalogosParaHSQLDB() throws Exception {
    String jdbcClass = provedor.getClasseJDBC();
    String jdbcUrl = this.jdbcUrl.getValor();
    String user = this.user.getValor();
    String password = this.password.getValor();

    Class.forName(jdbcClass);
    Connection c = DriverManager.getConnection(jdbcUrl, user, password);
    Statement statement = c.createStatement();

    for (String catalogo : catalogos.getValor()) {
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

}