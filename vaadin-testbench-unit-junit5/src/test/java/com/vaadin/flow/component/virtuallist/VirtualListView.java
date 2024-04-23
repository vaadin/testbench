/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.stream.Stream;

@Tag("div")
@Route(value = "virtual-list", registerAtStartup = false)
public class VirtualListView extends Composite<HorizontalLayout> {

    private final VirtualList<User> componentRendererVirtualList;
    private final VirtualList<User> callbackLitRendererVirtualList;

    private final List<User> users;

    public VirtualListView() {
        // virtual list using value provider
        var valueProviderVirtualList = new VirtualList<User>();
        valueProviderVirtualList.setRenderer(VirtualListView::userValueProvider);

        // virtual list using component renderer
        componentRendererVirtualList = new VirtualList<>();
        componentRendererVirtualList.setRenderer(new ComponentRenderer<>(this::userComponent));

        // virtual list using callback lit renderer
        callbackLitRendererVirtualList = new VirtualList<>();
        callbackLitRendererVirtualList.setRenderer(userLitRenderer());

        var content = getContent();
        content.setPadding(true);
        content.setSizeFull();

        addVirtualListBlock("Value Provider Virtual List", valueProviderVirtualList);
        addVirtualListBlock("Component Renderer Virtual List", componentRendererVirtualList);
        addVirtualListBlock("Callback Lit Renderer Virtual List", callbackLitRendererVirtualList);

        users = UserData.all();

        valueProviderVirtualList.setItems(users);
        componentRendererVirtualList.setItems(users);
        callbackLitRendererVirtualList.setDataProvider(DataProvider.fromCallbacks(this::fetchCallback, this::countCallback));
    }


    private <T> void addVirtualListBlock(String titleText, VirtualList<T> virtualList) {
        var title = new Div(titleText);
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD);

        virtualList.addClassNames(LumoUtility.Border.ALL);

        var block = new VerticalLayout();
        block.setSizeFull();
        block.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.LARGE);
        block.add(title);
        block.add(virtualList);

        getContent().add(block);
    }

    private static String userValueProvider(User user) {
        return String.join(" ",
                "Name:", user.getFirstName(), user.getLastName(),
                ";", "Active:", user.isActive() ? "Yes" : "No");
    }

    private Component userComponent(User user) {
        var firstNameSpan = new Span(user.getFirstName());
        firstNameSpan.setId("first-name");

        var lastNameSpan = new Span(user.getLastName());
        lastNameSpan.setId("last-name");

        var nameSpan = new Span();
        nameSpan.addClassNames(LumoUtility.Display.INLINE_FLEX, LumoUtility.Gap.XSMALL);
        nameSpan.add("Name:");
        nameSpan.add(firstNameSpan);
        nameSpan.add(lastNameSpan);

        var activeSpan = new Span(user.isActive() ? "Yes" : "No");
        activeSpan.setId("active");

        var activeToggleButton = new NativeButton("Toggle",
                event -> toggleActive(componentRendererVirtualList, user));

        var activeToggleSpan = new Span();
        activeToggleSpan.addClassNames(LumoUtility.Display.INLINE_FLEX, LumoUtility.Gap.XSMALL);
        activeToggleSpan.add("Active:");
        activeToggleSpan.add(activeSpan);
        activeToggleSpan.add(activeToggleButton);

        var userDiv = new Div();
        userDiv.addClassNames(LumoUtility.Display.INLINE_FLEX, LumoUtility.Gap.XSMALL);
        userDiv.add(nameSpan);
        userDiv.add(";");
        userDiv.add(activeToggleSpan);

        return userDiv;
    }

    private LitRenderer<User> userLitRenderer() {
        return LitRenderer.<User>of("""
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
                        user -> toggleActive(callbackLitRendererVirtualList, user));
    }

    private void toggleActive(VirtualList<User> virtualList, User user) {
        user.setActive(!user.isActive());
        virtualList.getDataProvider().refreshAll();

    }

    private Stream<User> fetchCallback(Query<User, Void> userVoidQuery) {
        return users.stream()
                .skip(userVoidQuery.getOffset())
                .limit(userVoidQuery.getLimit());
    }

    private int countCallback(Query<User, Void> userVoidQuery) {
        return (int) fetchCallback(userVoidQuery).count();
    }

}
