/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.grid;

import java.io.Serializable;

/**
 * Copied from grid integration-tests.
 */
@SuppressWarnings("serial")
public class Address implements Serializable {

    private String streetAddress = "";
    private Integer postalCode = null;
    private String city = "";
    private Country country = null;
    private int number;

    public Address() {

    }

    public Address(String streetAddress, int postalCode, String city,
            Country country) {
        setStreet(streetAddress);
        setPostalCode(postalCode);
        setCity(city);
        setCountry(country);
    }

    @Override
    public String toString() {
        return "Address [streetAddress=" + streetAddress + ", postalCode="
                + postalCode + ", city=" + city + ", country=" + country + "]";
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStreet() {
        return streetAddress;
    }

    public void setStreet(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

}