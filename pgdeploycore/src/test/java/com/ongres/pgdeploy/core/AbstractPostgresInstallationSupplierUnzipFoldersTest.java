/*
 * Copyright 2017, OnGres.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ongres.pgdeploy.core;

import com.ongres.pgdeploy.core.exceptions.NonWritableDestinationException;
import com.ongres.pgdeploy.core.exceptions.UnreachableBinariesException;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AbstractPostgresInstallationSupplierUnzipFoldersTest {

  private PostgresInstallationSupplier supplier;
  private Path path;


  private Path writablePath = Paths.get("installation");

  private Path nonWritablePath = Paths.get("nonWritable");


  private List<String> existingZip =
      Arrays.asList("src", "test", "resources", "binaries", "bin.zip");

  private List<String> nonExistingZip =
      Arrays.asList("qwerqwer", "bin.zip");


  private List<PostgresInstallationFolder> allFolders =
      Arrays.asList(PostgresInstallationFolder.BIN,
          PostgresInstallationFolder.INCLUDE,
          PostgresInstallationFolder.LIB,
          PostgresInstallationFolder.SHARE);

  private List<PostgresInstallationFolder> someFolders =
      Arrays.asList(PostgresInstallationFolder.BIN,
          PostgresInstallationFolder.LIB);




  private PostgresInstallationSupplier setup(List<String> folders) {

    RelativeRoute route = new RelativeRoute(folders);

    return new MockedPostgresInstallationSupplier(
        0, 0, 0, Platform.LINUX, null, route);

  }

  @After
  public void tearDown() {
    if(Files.exists(path))
    {
      deleteFolder(path.toFile());
    }

  }

  public static void deleteFolder(File folder) {
    File[] files = folder.listFiles();
    if(files!=null) { //some JVMs return null for empty dirs
      for(File f: files) {
        if(f.isDirectory()) {
          deleteFolder(f);
        } else {
          f.delete();
        }
      }
    }
    folder.delete();
  }

  @Test
  public void unzipFoldersEverythingOk() throws Exception {
    supplier = setup(existingZip);

    path = writablePath;

    supplier.unzipFolders(path, allFolders);
    supplier.checkInstallation(path, allFolders);

  }

  @Test(expected = UnreachableBinariesException.class)
  public void unzipFoldersNonExistingZip() throws Exception {
    supplier = setup(nonExistingZip);

    path = writablePath;

    supplier.unzipFolders(path, allFolders);

  }

  @Test(expected = NonWritableDestinationException.class)
  public void unzipFoldersNonWritableDestination() throws Exception {
    supplier = setup(existingZip);

    path = nonWritablePath;


    Set<PosixFilePermission> perms =
        PosixFilePermissions.fromString("rw-rw-rw-");
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(perms);
    Files.createDirectory(path, attr);

    supplier.unzipFolders(path, allFolders);

  }

  @Test
  public void unzipFoldersNotAllOptions() throws Exception {
    supplier = setup(existingZip);

    path = writablePath;

    supplier.unzipFolders(path, someFolders);
    supplier.checkInstallation(path, someFolders);

  }
  private class MockedPostgresInstallationSupplier extends AbstractPostgresInstallationSupplier {

    private MockedPostgresInstallationSupplier(
        int majorVersion, int minorVersion, int revision,
        Platform platform, String extraVersion, RelativeRoute relativeRoute) {
      this.majorVersion = majorVersion;
      this.minorVersion = minorVersion;
      this.revision = revision;
      this.platform = platform;
      this.extraVersion = extraVersion;
      this.routeToZippedCode = relativeRoute;

    }

  }

}