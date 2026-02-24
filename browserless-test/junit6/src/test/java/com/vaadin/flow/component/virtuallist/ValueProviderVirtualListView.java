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
package com.vaadin.flow.component.virtuallist;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Tag("div")
@Route(value = "value-provider-virtual-list", registerAtStartup = false)
public class ValueProviderVirtualListView extends Composite<HorizontalLayout> {

    final VirtualList<User> valueProviderVirtualList;

    public ValueProviderVirtualListView() {
        // virtual list using value provider
        valueProviderVirtualList = new VirtualList<>();
        valueProviderVirtualList
                .setRenderer(ValueProviderVirtualListView::userValueProvider);

        var title = new Div("Value Provider Virtual List");
        title.addClassNames(LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD);

        valueProviderVirtualList.addClassNames(LumoUtility.Border.ALL);

        var block = new VerticalLayout();
        block.setSizeFull();
        block.addClassNames(LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE);
        block.add(title);
        block.add(valueProviderVirtualList);

        var content = getContent();
        content.setPadding(true);
        content.setSizeFull();
        content.add(block);

        valueProviderVirtualList.setItems(UserData.all());
    }

    private static String userValueProvider(User user) {
        return String.join(" ", "Name:", user.getFirstName(),
                user.getLastName(), ";", "Active:",
                user.isActive() ? "Yes" : "No");
    }

}
