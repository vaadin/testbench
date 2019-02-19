/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.vaadin.addons.testbench.junit5.extensions.container;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rapidpm.dependencies.core.logger.HasLogger;

@WebServlet("/*")
public class HelloWorldServlet extends HttpServlet implements HasLogger {

  @Inject private UpperCaseService service;

  public HelloWorldServlet() {
    logger().info("Servlet created : " + HelloWorldServlet.class.getSimpleName());
  }

  public void doGet(HttpServletRequest request ,
                    HttpServletResponse response)
      throws IOException {
    response.setContentType("text/plain; charset=utf-8");

    String value = request.getParameter("value");

    response.getWriter().println(service.upperCase(value));
  }

}
