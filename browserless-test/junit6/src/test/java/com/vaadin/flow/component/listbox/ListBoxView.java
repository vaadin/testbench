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
package com.vaadin.flow.component.listbox;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "list-boxes", registerAtStartup = false)
public class ListBoxView extends Component implements HasComponents {
    ListBox<String> listBox;
    MultiSelectListBox<String> multiSelectListBox;

    List<String> selection = Arrays.asList("one", "two");

    public ListBoxView() {
        listBox = new ListBox<>();
        listBox.setItems(selection);

        multiSelectListBox = new MultiSelectListBox<>();
        multiSelectListBox.setItems(selection);

        add(listBox, multiSelectListBox);
    }
}
