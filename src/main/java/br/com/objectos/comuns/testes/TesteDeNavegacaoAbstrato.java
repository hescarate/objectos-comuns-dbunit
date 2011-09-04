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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeMatcher;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import rox.commons.mock.TesteComGuiceAbstrato;

/**
 * @author marcio.endo@objectos.com.br (Marcio Endo)
 */
@Test(groups = { "navegacao" })
public abstract class TesteDeNavegacaoAbstrato extends TesteComGuiceAbstrato {

  protected void verifiqueQue(WebElement actual, Matcher<WebElement> matcher) {
    MatcherAssert.assertThat(actual, matcher);
  }

  protected Matcher<WebElement> possuiConteudoDiferenteDe(final String texto) {

    return new TypeSafeMatcher<WebElement>() {

      @Override
      public boolean matchesSafely(WebElement element) {
        return !element.getText().equals(texto);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("um elemento HTML contendo '" + texto + "'");
      }

    };

  }

  protected Matcher<WebElement> possuiTexto(final String texto) {

    return new TypeSafeMatcher<WebElement>() {

      @Override
      public boolean matchesSafely(WebElement element) {
        return element.getText().contains(texto);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("um elemento HTML contendo '" + texto + "'");
      }

    };

  }

}