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
package com.ongres.pgdeploy;

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PgDeployInstallTest {

  private PgDeploy pgDeploy;
  private PostgresInstallationSupplier mockedSupplier;
  private PostgresInstallationSupplier spy;
  private Path path;

  @Before
  public void setup() {
    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    //mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null);
    mockedSupplier = mock(AbstractPostgresInstallationSupplier.class);

    spy = spy(mockedSupplier);

    try {
      doNothing().when(spy)
              .unzipFolders(any(Path.class), anyList());
    } catch (IOException e) {
      e.printStackTrace();
    }


    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);
  }

  @After
  public void tearDown() {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void checkBasicInstallationWorks() {

    //given
    PgDeploy.InstallOptions options = PgDeploy.InstallOptions.binaries().withInclude().withShare();
    path = Paths.get("carpeta");

    //when
    PostgresInstallation installation = null;
    try {
      installation = pgDeploy.install(mockedSupplier, options, path);
    } catch (BadInstallationException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    //then
    //the installation process must check itself that the destination
    //path contains the desired folders and fail otherwise
    assertNotNull(installation);
    assertEquals(path, installation.getPath());
    assertEquals(mockedSupplier, installation.getRouter());

    try {
      verify(mockedSupplier, times(1)).unzipFolders(path,options.toFolderList());
      verify(mockedSupplier, times(1)).checkInstallation(path,options.toFolderList());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (BadInstallationException e) {
      e.printStackTrace();
    }

  }







}
