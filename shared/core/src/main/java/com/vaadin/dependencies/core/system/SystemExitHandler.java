package com.vaadin.dependencies.core.system;

/**
 * <p>SystemExitHandler class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class SystemExitHandler implements ExitHandler {
    /** {@inheritDoc} */
    @Override
    public void exit(int exitcode) {
        System.exit(exitcode);
    }
}
