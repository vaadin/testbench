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
package com.vaadin.flow.component.sidenav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

@Tag("div")
@Route(value = "sidenav", registerAtStartup = false)
public class SideNavView extends Component implements HasComponents {

    final SideNav sideNav;
    final SideNavItem adminSection;
    final SideNavItem securitySection;

    public SideNavView() {
        sideNav = new SideNav("Menu");
        SideNavItem messagesLink = new SideNavItem("Messages", TargetView.class,
                VaadinIcon.ENVELOPE.create());
        messagesLink.addItem(new SideNavItem("Inbox", TargetView.class,
                new RouteParameters("param", "inbox"),
                VaadinIcon.INBOX.create()));
        messagesLink.addItem(new SideNavItem("Sent", TargetView.class,
                new RouteParameters("param", "sent"),
                VaadinIcon.PAPERPLANE.create()));
        messagesLink.addItem(new SideNavItem("Trash", "sidenav-target/trash",
                VaadinIcon.TRASH.create()));

        adminSection = new SideNavItem("Admin");
        adminSection.setPrefixComponent(VaadinIcon.COG.create());
        securitySection = new SideNavItem("Security");
        adminSection.addItem(securitySection);

        securitySection.addItem(new SideNavItem("Users", TargetView.class,
                new RouteParameters("param", "users"),
                VaadinIcon.GROUP.create()));
        securitySection.addItem(new SideNavItem("Permissions", TargetView.class,
                new RouteParameters("param", "permissions"),
                VaadinIcon.KEY.create()));
        securitySection.addItem(new SideNavItem("No Op"));

        sideNav.addItem(messagesLink, adminSection);
        sideNav.addItem(new SideNavItem("No Link"));

        // Duplicated items
        sideNav.addItem(new SideNavItem("Top Level Duplicated"));
        sideNav.addItem(new SideNavItem("Top Level Duplicated"));
        SideNavItem nestedDuplicates = new SideNavItem("Nested duplicated");
        nestedDuplicates.setExpanded(true);
        nestedDuplicates.addItem(new SideNavItem("Duplicated"),
                new SideNavItem("Duplicated"));
        sideNav.addItem(nestedDuplicates);

        add(sideNav);
    }
}
