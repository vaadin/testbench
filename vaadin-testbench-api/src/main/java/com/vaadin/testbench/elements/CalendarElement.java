/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Calendar")
public class CalendarElement extends AbstractComponentElement {
    public List<WebElement> getWeekNumbers() {
        return findElements(By.className("v-calendar-week-number"));
    }

    public boolean hasMonthView() {
        return isElementPresent(By.className("v-calendar-week-numbers"));
    }

    public boolean hasWeekView() {
        return isElementPresent(By.className("v-calendar-header-week"));
    }

    public List<WebElement> getDayNumbers() {
        return findElements(By.className("v-calendar-day-number"));
    }

    public List<WebElement> getMonthDays() {
        return findElements(By.className("v-calendar-month-day"));
    }

    public boolean hasDayView() {
        return getDayHeaders().size() == 1;
    }

    public List<WebElement> getDayHeaders() {
        return findElements(By.className("v-calendar-header-day"));
    }

    public void back() {
        if(hasWeekView() || hasDayView()) {
            findElement(By.className("v-calendar-back")).click();
        }else {
            throw new IllegalStateException("Navigation only available in week or day view");
        }
    }

    public void next() {
        if(hasWeekView() || hasDayView()) {
            findElement(By.className("v-calendar-next")).click();
        }else {
            throw new IllegalStateException("Navigation only available in week or day view");
        }
    }

}
