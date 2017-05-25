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

import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.core.MockedPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.RelativeRoute;
import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.pgversion.Pre10PostgresMajorVersion;
import com.ongres.pgdeploy.core.router.DefaultRouter;
import com.ongres.pgdeploy.installations.BadClusterException;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by pablo on 8/05/17.
 */
public class PgDeployRetrieveInstallationTest {

  private Path nonExistentPath = new RelativeRoute(Arrays.asList("src", "test", "resources", "noInstall")).asRelativePath();
  private Path realClusterPath = new RelativeRoute(Arrays.asList("src", "test", "resources", "cluster")).asRelativePath();
  private Path realInstallationPath = new RelativeRoute(Arrays.asList("src", "test", "resources", "installation")).asRelativePath();

  private PgDeploy pgDeploy = new PgDeploy(new ArrayList<>());
  private Platform linuxPlatform = new Platform("Linux", "x64");

  @Test (expected = IOException.class)
  public void retrieveNonExistentInstallation() throws Exception {
    pgDeploy.retrieveInstallation(nonExistentPath);
  }

  @Test (expected = BadInstallationException.class)
  public void retrieveIncompleteInstallation() throws Exception {
    pgDeploy.retrieveInstallation(realClusterPath);
  }

  @Test
  public void retrieveValidInstallation() throws Exception {
    PostgresInstallation installation = pgDeploy.retrieveInstallation(realInstallationPath);
    assertEquals(DefaultRouter.getInstance(), installation.getRouter());
    assertEquals(realInstallationPath, installation.getPath());
  }

  @Test
  public void retrieveValidInstallationWithSupplier() throws Exception {

    PostgresInstallationSupplier supplier = new MockedPostgresInstallationSupplier(
        new Pre10PostgresMajorVersion(9, 5), 0, linuxPlatform, null);

    PostgresInstallation installation = pgDeploy.retrieveInstallation(supplier, realInstallationPath);
    assertEquals(realInstallationPath, installation.getPath());
  }


  @Test (expected = BadClusterException.class)
  public void retrieveNotACluster() throws Exception {
    PostgresCluster cluster = pgDeploy.retrieveCluster(realInstallationPath, realInstallationPath);
  }

  @Test (expected = BadInstallationException.class)
  public void retrieveValidClusterWithInvalidInstallation() throws Exception {
    PostgresCluster cluster = pgDeploy.retrieveCluster(realClusterPath, realClusterPath);
  }

  @Test (expected = IOException.class)
  public void retrieveValidClusterWithNonExistentInstallation() throws Exception {
    PostgresCluster cluster = pgDeploy.retrieveCluster(realClusterPath, nonExistentPath);
  }

  @Test (expected = IOException.class)
  public void retrieveNonExistentCluster() throws Exception {
    PostgresCluster cluster = pgDeploy.retrieveCluster(nonExistentPath, realInstallationPath);
  }

  @Test
  public void retrieveValidCluster() throws Exception {
    PostgresCluster cluster = pgDeploy.retrieveCluster(realClusterPath, realInstallationPath);
    assertNotNull(cluster);
  }

  @Test
  public void retrieveValidClusterWithSupplier() throws Exception {
    PostgresInstallationSupplier supplier = new MockedPostgresInstallationSupplier(
        new Pre10PostgresMajorVersion(9, 5), 0, linuxPlatform, null);

    PostgresCluster cluster = pgDeploy.retrieveCluster(realClusterPath, realInstallationPath, supplier);
    assertNotNull(cluster);
  }



}