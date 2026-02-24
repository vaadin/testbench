/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "checkbox", registerAtStartup = false)
public class CheckboxView extends Component implements HasComponents {

    Checkbox checkbox = new Checkbox();

    Map<String, Name> items = Stream.of("foo", "bar", "baz", "jay")
            .collect(Collectors.toMap(Function.identity(), Name::new,
                    (a, b) -> a, LinkedHashMap::new));

    CheckboxGroup<Name> checkboxGroup = new CheckboxGroup<>();

    public CheckboxView() {
        add(checkbox);

        checkboxGroup.setItems(items.values());
        checkboxGroup.setItemLabelGenerator(item -> "test-" + item);
        add(checkboxGroup);
    }

    public static class Name {
        String name;

        public Name(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
