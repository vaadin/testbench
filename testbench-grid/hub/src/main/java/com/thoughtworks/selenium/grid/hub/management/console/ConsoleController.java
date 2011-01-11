package com.thoughtworks.selenium.grid.hub.management.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.selenium.grid.hub.HubRegistry;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Controller;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Page;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

public class ConsoleController extends Controller {

    public ConsoleController(HubRegistry registry) {
        super(registry);
    }

    public void process(HttpServletResponse response) throws IOException {
        final Page page = list();
        render(page, response);
    }

    public Page list() {
        final Page page;

        page = new Page("index.html");

        page.set("environments", registry().environmentManager().environments());
        List<RemoteControlInfo> rcInfo = getRcInfo();
        List<RemoteControlInfo> available = new ArrayList<RemoteControlInfo>();
        List<RemoteControlInfo> reserved = new ArrayList<RemoteControlInfo>();

        for (RemoteControlInfo info : rcInfo) {
            if (info.getActiveEnvironment() == null) {
                available.add(info);
            } else {
                reserved.add(info);
            }
        }

        page.set("availableRcs", createAvailableRcsHTML(available));
        // page.set("availableRemoteControls", available);
        page.set("reservedRemoteControls", reserved);

        return page;
    }

    private String createAvailableRcsHTML(List<RemoteControlInfo> available) {
        StringBuilder sb = new StringBuilder();
        for (RemoteControlInfo info : available) {
            sb.append("<tr>\n<td valign=\"top\">");
            sb.append(info.getHost());
            sb.append("</td>\n<td>");
            for (String env : info.getEnvironments()) {
                sb.append(env);
                sb.append("<br/>\n");
            }
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();

    }

    private List<RemoteControlInfo> getRcInfo() {
        Map<String, RemoteControlInfo> rcInfo = new HashMap<String, RemoteControlInfo>();

        List<RemoteControlProxy> all = registry().remoteControlPool()
                .allRegisteredRemoteControls();

        // Set<RemoteControlProxy> available = new HashSet<RemoteControlProxy>(
        // registry().remoteControlPool().availableRemoteControls());
        Set<RemoteControlProxy> reserved = new HashSet<RemoteControlProxy>(
                registry().remoteControlPool().reservedRemoteControls());

        for (RemoteControlProxy proxy : all) {
            RemoteControlInfo info = rcInfo.get(proxy.host());
            if (info == null) {
                info = new RemoteControlInfo(proxy.hostName());
                rcInfo.put(proxy.host(), info);
            }
            info.addEnvironment(proxy.environment());
            if (reserved.contains(proxy)) {
                info.setActiveEnvironment(proxy.environment());
                info.setExecutionTime(proxy.runtime());
                info.setCurrentTestName(proxy.getCurrentTestName());
            }
        }

        // Sort according to host name
        ArrayList<RemoteControlInfo> rcInfoList = new ArrayList<RemoteControlInfo>(
                rcInfo.values());
        Collections.sort(rcInfoList);

        return rcInfoList;
    }

    public static class RemoteControlInfo implements
            Comparable<RemoteControlInfo> {

        // Ip address or host name
        private final String host;

        private List<String> environments = new ArrayList<String>();
        private String activeEnvironment;
        private String currentTestName;

        private long executionTime;

        public RemoteControlInfo(String host) {
            this.host = host;
        }

        public String getHost() {
            return host;
        }

        public List<String> getEnvironments() {
            Collections.sort(environments);
            return environments;
        }

        public void addEnvironment(String environments) {
            this.environments.add(environments);
        }

        public String getActiveEnvironment() {
            return activeEnvironment;
        }

        public void setActiveEnvironment(String activeEnvironment) {
            this.activeEnvironment = activeEnvironment;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public int getExecutionTimeSeconds() {
            return (int) (executionTime / 1000);
        }

        public void setExecutionTime(long executionTime) {
            this.executionTime = executionTime;
        }

        public String getCurrentTestName() {
            return currentTestName;
        }

        public void setCurrentTestName(String currentTestName) {
            this.currentTestName = currentTestName;
        }

        public int compareTo(RemoteControlInfo o) {
            if (o == null) {
                return 1;
            }

            return getHost().compareTo(o.getHost());
        }

    }

}
