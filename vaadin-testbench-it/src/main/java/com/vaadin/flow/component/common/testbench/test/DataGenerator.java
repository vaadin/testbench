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

        System.out.println("Fetching " + first + " - " + (first + count - 1)
                + " (" + count + " items)");
        Stream<Person> data = IntStream.range(first, first + count)
                .mapToObj(i -> {
                    return new Person("First Name " + i, "Last name " + i,
                            i);
                });

        return data;
    }

    @Override
    protected int sizeInBackEnd(Query<Person, String> query) {
        return size;
    }

}