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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Tag("div")
@Route(value = "component-renderer-virtual-list", registerAtStartup = false)
public class ComponentRendererVirtualListView
        extends Composite<HorizontalLayout> {

    final VirtualList<User> componentRendererVirtualList;

    public ComponentRendererVirtualListView() {
        // virtual list using component renderer
        componentRendererVirtualList = new VirtualList<>();
        componentRendererVirtualList
                .setRenderer(new ComponentRenderer<>(this::userComponent));

        var title = new Div("Component Renderer Virtual List");
        title.addClassNames(LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD);

        componentRendererVirtualList.addClassNames(LumoUtility.Border.ALL);

        var block = new VerticalLayout();
        block.setSizeFull();
        block.addClassNames(LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE);
        block.add(title);
        block.add(componentRendererVirtualList);

        var content = getContent();
        content.setPadding(true);
        content.setSizeFull();
        content.add(block);

        componentRendererVirtualList.setItems(UserData.all());
    }

    private Component userComponent(User user) {
        var firstNameSpan = new Span(user.getFirstName());
        firstNameSpan.setId("first-name");

        var lastNameSpan = new Span(user.getLastName());
        lastNameSpan.setId("last-name");

        var nameSpan = new Span();
        nameSpan.addClassNames(LumoUtility.Display.INLINE_FLEX,
                LumoUtility.Gap.XSMALL);
        nameSpan.add("Name:");
        nameSpan.add(firstNameSpan);
        nameSpan.add(lastNameSpan);

        var activeSpan = new Span(user.isActive() ? "Yes" : "No");
        activeSpan.setId("active");

        var activeToggleButton = new NativeButton("Toggle",
                event -> toggleActive(componentRendererVirtualList, user));

        var activeToggleSpan = new Span();
        activeToggleSpan.addClassNames(LumoUtility.Display.INLINE_FLEX,
                LumoUtility.Gap.XSMALL);
        activeToggleSpan.add("Active:");
        activeToggleSpan.add(activeSpan);
        activeToggleSpan.add(activeToggleButton);

        var userDiv = new Div();
        userDiv.addClassNames(LumoUtility.Display.INLINE_FLEX,
                LumoUtility.Gap.XSMALL);
        userDiv.add(nameSpan);
        userDiv.add(";");
        userDiv.add(activeToggleSpan);

        return userDiv;
    }

    private void toggleActive(VirtualList<User> virtualList, User user) {
        user.setActive(!user.isActive());
        virtualList.getDataProvider().refreshAll();
    }

}
