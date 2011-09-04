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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class GeradorDePersistenceXml {
  
  private final PacotesDeEntidades pacotes;
  
  @Inject(optional = true)
  @Named("hibernate.debug")
  private boolean debug = false;
  
  @Inject
  GeradorDePersistenceXml(PacotesDeEntidades pacotes) {
    this.pacotes = pacotes;
  }

  public void gerar() throws IOException, URISyntaxException {
    File file = pacotes.getPersistenceXml();
    FileWriter fileWriter = new FileWriter(file);
    PrintWriter writer = new PrintWriter(fileWriter, true);
    
    Set<Class<?>> entities = pacotes.getEntidades();
    
    Collection<String> classes = Collections2.transform(entities, new ClassToString());
    escrever(writer, classes);
    writer.close();
  }

  private void escrever(PrintWriter writer, Collection<String> classes) {
    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.println("<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\"");
    writer.println("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    writer.println("xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd\"");
    writer.println("version=\"1.0\">");
    writer.println("<persistence-unit name=\"mockPU\" transaction-type=\"RESOURCE_LOCAL\">");
    writer.println("<provider>org.hibernate.ejb.HibernatePersistence</provider>");
    writer.println("<non-jta-data-source>java:/MockDS</non-jta-data-source>");
    
    for (String classe : classes) {
      writer.println(classe);
    }
    
    writer.println("<exclude-unlisted-classes>true</exclude-unlisted-classes>");
    writer.println("<properties>");
    writer.println("<property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\" />");
    writer.println("<property name=\"hibernate.dialect\" value=\"br.com.objectos.comuns.bancodedados.HSQLDialect\" />");
    writer.println("<property name=\"hibernate.jdbc.batch_size\" value=\"0\" />");
    writer.println("<property name=\"hibernate.cache.use_second_level_cache\" value=\"true\" />");
    writer.println("<property name=\"hibernate.cache.provider_class\" value=\"org.hibernate.cache.HashtableCacheProvider\" />");
    writer.println("<property name=\"hibernate.show_sql\" value=\"" + debug + "\" />");
    writer.println("<property name=\"hibernate.format_sql\" value=\"" + debug + "\" />");
    writer.println("</properties>");
    writer.println("</persistence-unit>");
    writer.println("</persistence>");
  }

  private class ClassToString implements Function<Class<?>, String> {
    @Override
    public String apply(Class<?> from) {
      return "<class>" + from.getName() + "</class>";
    }
  }

}