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

import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import javax.persistence.Entity;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class PacotesDeEntidades {

  private final String marcador;

  private final Set<String> pacotes = newHashSet();

  public PacotesDeEntidades(String marcador) {
    this.marcador = marcador;
  }

  public boolean add(String e) {
    return pacotes.add(e);
  }

  public File getPersistenceXml() throws URISyntaxException, IOException {
    URL url = Thread.currentThread().getContextClassLoader()
        .getResource("META-INF/" + marcador);
    try {
      File file = new File(url.toURI());
      File metaInf = file.getParentFile();
      File persistence = new File(metaInf, "persistence.xml");
      Files.touch(persistence);
      return persistence;
    } catch (URISyntaxException e) {
      throw e;
    } catch (IllegalArgumentException e) {
      System.out.println(url.toString());
      throw e;
    }
  }

  public String getMarcador() {
    return marcador;
  }

  public Set<Class<?>> getEntidades() {
    Set<Class<?>> entities = newHashSet();

    for (String pacote : pacotes) {
      entities.addAll(getEntities(pacote));
    }

    return entities;
  }

  public Set<String> getNomes() {
    Set<Class<?>> entidades = getEntidades();
    Collection<String> nomes = Collections2.transform(entidades,
        new Function<Class<?>, String>() {
          @Override
          public String apply(Class<?> from) {
            return from.getName();
          }
        });
    return newHashSet(nomes);
  }

  private Set<Class<?>> getEntities(String pacote) {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.getUrlsForPackagePrefix(pacote)).setScanners(
            new TypeAnnotationsScanner()));
    return reflections.getTypesAnnotatedWith(Entity.class);
  }

}