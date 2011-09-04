/*
 * ObjectosComunsDbunitModule.java criado em 04/09/2011
 * 
 * Propriedade de Objectos Fábrica de Software LTDA.
 * Reprodução parcial ou total proibida.
 */
package br.com.objectos.comuns.dbunit;

import org.dbunit.database.IDatabaseConnection;

import com.google.inject.AbstractModule;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class ObjectosComunsDbunitModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IDatabaseConnection.class).toProvider(IDatabaseConnectionProvider.class);
    bind(DBUnit.class);
  }

}