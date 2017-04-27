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

import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.exceptions.ExtraFoldersFoundException;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class AbstractPostgresInstallationSupplierCheckInstallationTest {

  private PostgresInstallationSupplier supplier;
  private Path path = Paths.get("installation");


  private List<PostgresInstallationFolder> allFolders =
          Arrays.asList(PostgresInstallationFolder.BIN,
                  PostgresInstallationFolder.INCLUDE,
                  PostgresInstallationFolder.LIB,
                  PostgresInstallationFolder.SHARE);

  private List<PostgresInstallationFolder> someFolders =
          Arrays.asList(PostgresInstallationFolder.BIN,
                  PostgresInstallationFolder.LIB);




  private PostgresInstallationSupplier createSupplier() {
    List<String> folders =
        Arrays.asList("src", "test", "resources", "binaries", "bin.zip");
    RelativeRoute route = new RelativeRoute(folders);

    return new MockedPostgresInstallationSupplier(
            0, 0, 0, Platform.LINUX, null, route);
  }

  private void prepareFolders(List<PostgresInstallationFolder> folders)
  {
    try {
      Files.createDirectory(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    folders.forEach(folder -> {
      try {
        Files.createDirectory(path.resolve(folder.getStringId()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
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
  public void checkInstallationAllInstalledAllRequested() throws Exception {
    supplier = createSupplier();

    prepareFolders(allFolders);
    supplier.checkInstallation(path, allFolders);
  }

  @Test(expected = ExtraFoldersFoundException.class)
  public void checkInstallationAllInstalledSomeRequested() throws Exception {
    supplier = createSupplier();

    prepareFolders(allFolders);
    supplier.checkInstallation(path, someFolders);
  }

  @Test(expected = BadInstallationException.class)
  public void checkInstallationSomeInstalledAllRequested() throws Exception {
    supplier = createSupplier();

    prepareFolders(someFolders);
    supplier.checkInstallation(path, allFolders);
  }

  @Test
  public void checkInstallationSomeInstalledSomeRequested() throws Exception {
    supplier = createSupplier();

    prepareFolders(someFolders);
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