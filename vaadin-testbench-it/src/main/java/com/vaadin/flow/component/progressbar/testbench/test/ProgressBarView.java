package com.vaadin.flow.component.progressbar.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(ProgressBarView.NAV)
@Theme(Lumo.class)
public class ProgressBarView extends AbstractView {

    public static final String DEFAULT = "default";
    public static final String HUNDRED = "hundred";
    public static final String NAV = "ProgressBar";

    public ProgressBarView() {
        ProgressBar progressBar = new ProgressBar(0, 10);
        progressBar.setId(DEFAULT);
        progressBar.setWidth("200px");
        progressBar.setValue(7);
        add(progressBar);

        progressBar = new ProgressBar(0, 100);
        progressBar.setId(HUNDRED);
        progressBar.setWidth("200px");
        progressBar.setValue(22);
        add(progressBar);
    }

}
