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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import com.google.common.base.Supplier;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public abstract class DataSetSupplier implements Supplier<IDataSet> {

  private final String filename;

  public DataSetSupplier(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public DatabaseOperation getOperation() {
    return DatabaseOperation.CLEAN_INSERT;
  }

  @Override
  public IDataSet get() {

    try {

      String classpathName = "dbunit/" + filename;
      ClassLoader classLoader = this.getClass().getClassLoader();

      InputStream in = classLoader.getResourceAsStream(classpathName);
      Reader reader = new BufferedReader(getReader(in));

      boolean dtdMetadata = false;
      boolean columnSensing = true;
      boolean caseSensitiveTableNames = false;

      return new FlatXmlDataSet(reader, dtdMetadata, columnSensing, caseSensitiveTableNames);

    } catch (DataSetException e) {
      throw new DBUnitSetupException(e);
    } catch (IOException e) {
      throw new DBUnitSetupException(e);
    } finally {

    }

  }

  protected Reader getReader(InputStream in) throws IOException {
    return new InputStreamReader(in);
  }

}