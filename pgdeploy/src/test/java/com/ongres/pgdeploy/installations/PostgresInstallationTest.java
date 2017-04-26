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
package com.ongres.pgdeploy.installations;

import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.RelativeRoute;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by pablo on 25/04/17.
 */
public class PostgresInstallationTest {

  private PostgresInstallation installation;
  private Path path;

  @Before
  public void setup() {
    PostgresInstallationSupplier supplier = mock(AbstractPostgresInstallationSupplier.class);

    installation = new ConcretePostgresInstallation(supplier,
        new RelativeRoute(Arrays.asList("src", "test", "resources", "installation")).asRelativePath());

    path = new RelativeRoute(Arrays.asList("clusters")).asRelativePath();
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

  @Ignore
  @Test
  public void createCluster() throws Exception {
    PostgresCluster cluster = installation.createCluster(path);
    assertNotNull(cluster);
  }



}