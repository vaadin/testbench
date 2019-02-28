/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.dependencies.core.fs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * <p>DirectoryUtils class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class DirectoryUtils {

  /**
   * <p>deleteIndexDirectory.</p>
   *
   * @param directoryName a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean deleteIndexDirectory(final String directoryName) {
    final Path indexDirectory = Paths.get(directoryName);
    return delete(indexDirectory);
  }

  /**
   * <p>deleteIndexDirectory.</p>
   *
   * @param directoryPath a {@link java.nio.file.Path} object.
   * @return a boolean.
   */
  public boolean deleteIndexDirectory(final Path directoryPath) {
    return delete(directoryPath);
  }



  private boolean delete(final Path indexDirectory) {
    if (Files.exists(indexDirectory)) {
      try {
        Files.walkFileTree(indexDirectory, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }
        });
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
      return true;
    }
    return false;
  }


}
