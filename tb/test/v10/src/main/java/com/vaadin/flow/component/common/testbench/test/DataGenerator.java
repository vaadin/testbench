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

import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

public class DataGenerator
    extends AbstractBackEndDataProvider<Person, String> {

  private int size;

  public DataGenerator(int size) {
    this.size = size;
  }

  @Override
  protected Stream<Person> fetchFromBackEnd(Query<Person, String> query) {
    int first = query.getOffset();
    int count = query.getLimit();

    System.out.println("Fetching " + first + " - " + (first + count - 1)
                       + " (" + count + " items)");
    Stream<Person> data = IntStream.range(first , first + count)
                                   .mapToObj(i -> {
                                     return new Person("First Name " + i , "Last name " + i ,
                                                       i);
                                   });

    return data;
  }

  @Override
  protected int sizeInBackEnd(Query<Person, String> query) {
    return size;
  }

}