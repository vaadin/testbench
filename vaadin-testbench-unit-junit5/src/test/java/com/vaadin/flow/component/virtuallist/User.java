/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.virtuallist;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private Long key;
    private String firstName;
    private String lastName;
    private boolean active;

    public User(Long key, String firstName, String lastName, boolean active) {
        this.key = key;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    public User(Long key, String firstName, String lastName) {
        this(key, firstName, lastName, true);
    }

    public User(Long key) {
        this(key, null, null);
    }

    public User(String firstName, String lastName, boolean active) {
        this(null, firstName, lastName, active);
    }

    public User(String firstName, String lastName) {
        this(firstName, lastName, true);
    }

    public User() {
        this(null, null);
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User user))
            return false;

        if (!Objects.equals(key, user.key))
            return false;
        if (!Objects.equals(firstName, user.firstName))
            return false;
        if (!Objects.equals(lastName, user.lastName))
            return false;
        return (active == user.active);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
