package com.vaadin.tests.elements.ng.tooling;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 *
 */
public class ParallelForkAndJoinRunner extends BlockJUnit4ClassRunner {



    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError
     *     if the test class is malformed.
     */
    public ParallelForkAndJoinRunner(Class<?> klass)
        throws InitializationError {
        super(klass);

        throw new RuntimeException("not usable right now");

    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return super.computeTestMethods();
    }
}
