package com.itmill.testingtools.runner;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class ITMillTask extends Task {

    private String[] testCase;
    private String[] testHost;
    private String deploymentUrl;

    public void setTestCase(String[] testCase) {
        this.testCase = testCase;
    }

    public void setTestHost(String[] testHost) {
        this.testHost = testHost;
    }

    public void setDeploymentUrl(String deploymentUrl) {
        this.deploymentUrl = deploymentUrl;
    }

    @Override
    public void execute() throws BuildException {
        System.out.println(testCase);
    }

}
