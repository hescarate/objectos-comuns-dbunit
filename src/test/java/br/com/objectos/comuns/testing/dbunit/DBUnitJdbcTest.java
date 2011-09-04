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

import static com.google.common.collect.Lists.transform;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
@Test
@Guice(modules = JdbcModule.class)
public class DBUnitJdbcTest {

  @Inject
  private Provider<Connection> conns;

  @Inject
  private DBUnit dbunit;

  private Connection conn;
  private Statement stmt;

  @BeforeClass
  public void prepareSchemas() throws SQLException {
    setupConnection();
    stmt.execute("create schema OBJ authorization DBA");
    stmt.execute("create table OBJ.ENTITY (ID INT PRIMARY KEY, VALUE VARCHAR(5))");
  }

  @BeforeMethod
  public void setupConnection() throws SQLException {
    conn = conns.get();
    conn.setAutoCommit(false);
    stmt = conn.createStatement();
  }

  public void it_should_load_data_from_xml_files() throws SQLException {
    dbunit.load(new MiniDbXml());

    stmt.execute("select * from OBJ.ENTITY");
    ResultSet rs = stmt.getResultSet();

    List<ResultSet> rss = ImmutableList.of(rs, rs, rs);
    List<Entity> result = transform(rss, new ToEntity());

    assertThat(result.size(), equalTo(3));
    assertEquals(result.toString(), "[Entity{1, A}, Entity{2, B}, Entity{3, C}]");
  }

  public void it_should_truncate_data_properly() throws SQLException {
    dbunit.load(new MiniDbXml());

    stmt.execute("select * from OBJ.ENTITY");
    ResultSet rs = stmt.getResultSet();

    List<ResultSet> rss = ImmutableList.of(rs, rs, rs);
    List<Entity> result = transform(rss, new ToEntity());

    assertThat(result.size(), equalTo(3));
    assertEquals(result.toString(), "[Entity{1, A}, Entity{2, B}, Entity{3, C}]");

    dbunit.load(new MiniTruncateXml());

    stmt.execute("select * from OBJ.ENTITY");
    rs = stmt.getResultSet();

    rss = ImmutableList.of(rs, rs, rs);
    result = transform(rss, new ToEntity());

    assertThat(result.size(), equalTo(3));
    assertEquals(result.toString(), "[null, null, null]");
  }

  @AfterClass(alwaysRun = true)
  @AfterMethod(alwaysRun = true)
  public void cleanup() {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
      }
    }
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
      }
    }
  }

  private class ToEntity implements Function<ResultSet, Entity> {
    @Override
    public Entity apply(ResultSet input) {
      try {
        return input.next() ? new Entity(input) : null;
      } catch (SQLException e) {
        return null;
      }
    }
  }

}