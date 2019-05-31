package com.vaadin.flow.component.common.testbench.test;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataGenerator
        extends AbstractBackEndDataProvider<Person, String> {

    private int size;

    public DataGenerator(int size) {
        this.size = size;
    }

    @Override
    protected Stream<Person> fetchFromBackEnd(Query<Person, String> query) {
        int first = query.getOffset();
        int count = query.getLimit();

        return IntStream
                .range(first, first + count)
                .mapToObj(i -> new Person("First Name " + i, "Last name " + i, i));
    }

    @Override
    protected int sizeInBackEnd(Query<Person, String> query) {
        return size;
    }

}