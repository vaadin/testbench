/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package junit.com.vaadin.testbench.tests.component.upload;

import static com.vaadin.flow.component.upload.testbench.test.UploadView.NAV;
import static com.vaadin.flow.component.upload.testbench.test.UploadView.UPLOAD;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.FIREFOX;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.SAFARI;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.flow.component.upload.testbench.UploadElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class UploadIT extends AbstractIT implements HasLogger {


  /**
   * Disbaled for
   *  * Firefox
   *    Firefox has issues with interaction with hidden file input
   *    https://github.com/mozilla/geckodriver/issues/1173
   *  * Safari
   *    Safari webdriver does not support file uploads
   *
   * @param po
   * @throws Exception
   */
  @VaadinTest(navigateAsString = NAV)
  @SkipBrowsers({SAFARI , FIREFOX})
  public void upload(GenericTestPageObject po) throws Exception {
    byte[] file1Contents = "This is file 1"
        .getBytes(UTF_8);
    byte[] file2Contents = "This is another åäö file"
        .getBytes(UTF_8);

    File file1 = createTempFile(file1Contents);

    final UploadElement upload = po.upload().id(UPLOAD);

    upload.upload(file1);
    Assertions.assertEquals("File " + file1.getName() + " of size "
                            + file1Contents.length + " received" ,
                            getLogRowWithoutNumber(po , 0));

    File file2 = createTempFile(file2Contents);
    upload.upload(file2);
    Assertions.assertEquals("File " + file2.getName() + " of size "
                            + file2Contents.length + " received" ,
                            getLogRowWithoutNumber(po , 0));
  }

  @VaadinTest(navigateAsString = NAV)
  @Disabled("Can't be tested automatically")
  // The upload finishes so quickly from localhost. Would need a huge file to
  // be created or throttling support
  public void abortUpload(GenericTestPageObject po) {
    logger().warning(
        "To test manually, remove @Ignore and set a breakpoint on the abort() line. Then start uploading a huge file after hitting the breakpoint and continue with the test");

    final UploadElement upload = po.upload().id(UPLOAD);
    upload.abort();
    String start = getLogRow(po , 1);
    String aborted = getLogRow(po , 0);

    Assertions.assertEquals(start , "Upload of");
    Assertions.assertEquals(start , "started");

    Assertions.assertEquals(aborted , "failed");

  }

  private File createTempFile(byte[] contents) throws IOException {
    File tempFile = File.createTempFile("TestFileUpload" , ".txt");
    try (FileOutputStream out = new FileOutputStream(tempFile)) {
      out.write(contents);
    }
    tempFile.deleteOnExit();
    return tempFile;
  }

}
