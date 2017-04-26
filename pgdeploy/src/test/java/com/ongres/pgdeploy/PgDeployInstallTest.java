/*
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.ongres.pgdeploy;

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.exceptions.ExtraFoldersFoundException;
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
    } catch (ExtraFoldersFoundException e) {
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
    } catch (ExtraFoldersFoundException e) {
      e.printStackTrace();
    }

  }







}
