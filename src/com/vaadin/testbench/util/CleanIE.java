package com.vaadin.testbench.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CleanIE {

    private static final Log LOGGER = LogFactory.getLog(CleanIE.class);

    public static void cleanRunningIE() {
        try {
            File file = File.createTempFile("internetExplorer", ".vbs");
            FileWriter fw = new FileWriter(file);

            String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
                    + "Set locator = CreateObject(\"WbemScripting.SWbemLocator\")\n"
                    + "Set service = locator.ConnectServer()\n"
                    + "Set processes = service.ExecQuery(\"Select processID from win32_process where name = 'iexplore.exe'\")\n"
                    + "For Each process in processes\n" + "process.terminate(1)\n" + "Next\n"
                    + "Set WSHShell = Nothing";

            fw.write(vbs);
            fw.close();

            Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());

            Thread.sleep(2000);

            file.delete();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
