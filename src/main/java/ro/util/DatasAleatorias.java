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
package ro.util;

import static br.com.objectos.comuns.base.Dates.newLocalDate;

import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.LocalDate;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
public class DatasAleatorias {

  private static final LocalDate DATA_INICIAL = newLocalDate(1985, 1, 1);

  private static final AtomicInteger contador = new AtomicInteger(1);

  private DatasAleatorias() {
  }

  public static LocalDate proximaData() {
    int dias = contador.getAndIncrement();
    return DATA_INICIAL.plusDays(dias);
  }

}