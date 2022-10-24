/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.example.base.navigation;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Route;

@Route("navigation-postpone")
public class NavigationPostponeView extends VerticalLayout
        implements BeforeLeaveObserver {
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction action = event.postpone();
        Dialog dialog = new Dialog();
        dialog.add(new Span(
                "Are you sure you want to leave such a beautiful view?"),
                new Button("Yes", ev -> {
                    action.proceed();
                    dialog.close();
                }), new Button("No", ev -> dialog.close()));
        dialog.open();
    }
}
