package junit.com.vaadin.testbench.tests.component.upload;

import com.vaadin.flow.component.upload.testbench.UploadElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.uitest.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.vaadin.flow.component.upload.testbench.test.UploadView.NAV;
import static com.vaadin.flow.component.upload.testbench.test.UploadView.UPLOAD;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.FIREFOX;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.SAFARI;
import static java.nio.charset.StandardCharsets.UTF_8;

@VaadinTest
public class UploadIT extends AbstractIT {

    /**
     * Disabled for
     * * Firefox
     * Firefox has issues with interaction with hidden file input
     * https://github.com/mozilla/geckodriver/issues/1173
     * * Safari
     * Safari webdriver does not support file uploads
     *
     * @param po
     * @throws Exception
     */
    @VaadinTest(navigateTo = NAV)
    @SkipBrowsers({SAFARI, FIREFOX})
    @Disabled("Can't be tested automatically")
    public void upload(GenericTestPageObject po) throws Exception {
        byte[] file1Contents = "This is file 1"
                .getBytes(UTF_8);
        byte[] file2Contents = "This is another åäö file"
                .getBytes(UTF_8);

        File file1 = createTempFile(file1Contents);

        final UploadElement upload = po.$(UploadElement.class).id(UPLOAD);

        upload.upload(file1);
        Assertions.assertEquals("File " + file1.getName() + " of size "
                        + file1Contents.length + " received",
                getLogRowWithoutNumber(po, 0));

        File file2 = createTempFile(file2Contents);
        upload.upload(file2);
        Assertions.assertEquals("File " + file2.getName() + " of size "
                        + file2Contents.length + " received",
                getLogRowWithoutNumber(po, 0));
    }

    @VaadinTest(navigateTo = NAV)
    @Disabled("Can't be tested automatically")
    // The upload finishes so quickly from localhost. Would need a huge file to
    // be created or throttling support
    public void abortUpload(GenericTestPageObject po) {
//    logger().warning(
//        "To test manually, remove @Ignore and set a breakpoint on the abort() line.
//        Then start uploading a huge file after hitting the breakpoint and continue with the test");

        final UploadElement upload = po.$(UploadElement.class).id(UPLOAD);
        upload.abort();
        String start = getLogRow(po, 1);
        String aborted = getLogRow(po, 0);

        Assertions.assertEquals(start, "Upload of");
        Assertions.assertEquals(start, "started");

        Assertions.assertEquals(aborted, "failed");

    }

    private File createTempFile(byte[] contents) throws IOException {
        File tempFile = File.createTempFile("TestFileUpload", ".txt");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(contents);
        }
        tempFile.deleteOnExit();
        return tempFile;
    }

}
