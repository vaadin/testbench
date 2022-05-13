package com.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route("param")
@Tag("div")
public class SingleParam extends Component implements HasUrlParameter<String> {
    public String parameter;

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.parameter = parameter;
    }
}
