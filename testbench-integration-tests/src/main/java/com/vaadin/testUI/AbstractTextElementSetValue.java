/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.testUI;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class AbstractTextElementSetValue extends AbstractTestUI {

    AbstractTextField[] comps = { new TextField(), new PasswordField(),
            new TextArea() };
    // one extra label for DateField, which we create in a separate method
    Label[] eventCountLabels = new Label[comps.length];
    int[] eventCounters = new int[comps.length];

    public static final String INITIAL_VALUE = "initial value";

    @Override
    protected void setup(VaadinRequest request) {

        for (int i = 0; i < comps.length; i++) {
            comps[i].setValue(INITIAL_VALUE);
            eventCountLabels[i] = new Label();
            eventCountLabels[i].setCaption("event count");
            // create an valueChangeListener, to count valueChangeListener
            // events
            comps[i].addValueChangeListener(new ValueChangeCounter(i));
            addComponent(comps[i]);
            addComponent(eventCountLabels[i]);

        }
    }

    @Override
    protected String getTestDescription() {
        return "Test type method of AbstractTextField components";
    }


    @Override
    protected Integer getTicketNumber() {
        return 13365;
    }

    // helper class, which increases valuechange event counter
    private class ValueChangeCounter implements ValueChangeListener<String> {
        private int index;

        public ValueChangeCounter(int index) {
            this.index = index;
        }

        @Override
        public void accept(ValueChangeEvent<String> event) {
            eventCounters[index]++;
            String value = "" + eventCounters[index];
            eventCountLabels[index].setValue(value);
        }

    }
}
