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

import java.util.List;
import java.util.stream.Stream;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Tag("div")
@Route(value = "callback-lit-renderer-virtual-list", registerAtStartup = false)
public class CallbackLitRendererVirtualListView
        extends Composite<HorizontalLayout> {

    final VirtualList<User> callbackLitRendererVirtualList;

    private final List<User> users;

    public CallbackLitRendererVirtualListView() {
        // virtual list using callback lit renderer
        callbackLitRendererVirtualList = new VirtualList<>();
        callbackLitRendererVirtualList.setRenderer(userLitRenderer());

        var title = new Div("Callback Lit Renderer Virtual List");
        title.addClassNames(LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD);

        callbackLitRendererVirtualList.addClassNames(LumoUtility.Border.ALL);

        var block = new VerticalLayout();
        block.setSizeFull();
        block.addClassNames(LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE);
        block.add(title);
        block.add(callbackLitRendererVirtualList);

        var content = getContent();
        content.setPadding(true);
        content.setSizeFull();
        content.add(block);

        users = UserData.all();
        callbackLitRendererVirtualList.setDataProvider(DataProvider
                .fromCallbacks(this::fetchCallback, this::countCallback));
    }

    private LitRenderer<User> userLitRenderer() {
        return LitRenderer
                .<User> of(
                        """
                                    <div>
                                        <span>
                                            Name:
                                            <span>${item.firstName}</span>
                                            <span>${item.lastName}</span>
                                        </span>
                                        ;
                                        <span>
                                            Active:
                                            <span>${item.active ? 'Yes' : 'No'}</span>
                                            <button @click=${onActiveToggleClick}>Toggle</button>
                                        </span>
                                    </div>
                                """)
                .withProperty("firstName", User::getFirstName)
                .withProperty("lastName", User::getLastName)
                .withProperty("active", User::isActive)
                .withFunction("onActiveToggleClick",
                        user -> toggleActive(callbackLitRendererVirtualList,
                                user));
    }

    private void toggleActive(VirtualList<User> virtualList, User user) {
        user.setActive(!user.isActive());
        virtualList.getDataProvider().refreshAll();
    }

    private Stream<User> fetchCallback(Query<User, Void> userVoidQuery) {
        return users.stream().skip(userVoidQuery.getOffset())
                .limit(userVoidQuery.getLimit());
    }

    private int countCallback(Query<User, Void> userVoidQuery) {
        return (int) fetchCallback(userVoidQuery).count();
    }

}
