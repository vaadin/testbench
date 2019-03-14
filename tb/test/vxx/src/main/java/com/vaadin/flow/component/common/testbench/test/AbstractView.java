/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.flow.component.common.testbench.test;

import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.flow.component.html.Div;

public class AbstractView extends Div implements HasLogger {
  private Log log;

  protected AbstractView() {
    log = new Log();
    add(log);
  }

  protected void log(String message) {
    log.log(message);
    logger().info(message);
  }

  public static class Log extends Div {

    private int nr = 1;

    Log() {
      setWidth("100%");
      setHeight("5em");
      getElement().getStyle().set("overflow" , "auto");
      setId("log");
    }

    public void log(String message) {
      Div row = new Div();
      row.setText(nr++ + ". " + message);
      getElement().insertChild(0 , row.getElement());
    }

  }

}
