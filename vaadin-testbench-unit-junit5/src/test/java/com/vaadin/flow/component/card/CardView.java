/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.card;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "card", registerAtStartup = false)
public class CardView extends Component implements HasComponents {

    Card card;
    Button contentButton;
    Button headerButton;
    Button headerPrefixButton;
    Button headerSuffixButton;
    Button footerButton;

    public CardView() {
        card = new Card();
        contentButton = new Button("Content");
        card.add(contentButton);
        headerButton = new Button("Header");
        card.setHeader(headerButton);
        headerPrefixButton = new Button("Header prefix");
        card.setHeaderPrefix(headerPrefixButton);
        headerSuffixButton = new Button("Header suffix");
        card.setHeaderSuffix(headerSuffixButton);
        footerButton = new Button("Footer");
        card.addToFooter(footerButton);
        add(card);
    }
}
