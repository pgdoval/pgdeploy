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
import com.ongres.pgdeploy.clusters.PostgresClusterCreationOptions;
import com.ongres.pgdeploy.core.RelativeRoute;
import com.ongres.pgdeploy.core.router.DefaultRouter;
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

 
public class PostgresInstallationTest {

  private PostgresInstallation installation;
  private Path path;

  private Path twoLevelPath = new RelativeRoute(Arrays.asList("clusters", "cl1")).asRelativePath();

  @Before
  public void setup() {
    Router router = DefaultRouter.getInstance();

    installation = new ConcretePostgresInstallation(router,
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


  @Test
  public void createClusterInNonExistentFolder() throws Exception {
    PostgresCluster cluster = installation.createCluster(path);
    assertNotNull(cluster);
  }

  @Test
  public void createClusterInEmptyFolder() throws Exception {
    Files.createDirectory(path);

    PostgresCluster cluster = installation.createCluster(path);
    assertNotNull(cluster);
  }

  @Test
  public void createClusterInNonWritableFolder() throws Exception {

    Set<PosixFilePermission> perms =
        PosixFilePermissions.fromString("rw-------");
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(perms);
    Files.createDirectory(path, attr);

    PostgresCluster cluster = installation.createCluster(path);
    assertNotNull(cluster);
  }

  @Test(expected = ClusterDirectoryNotEmptyException.class)
  public void createClusterInNonEmptyFolder() throws Exception {

    Files.createDirectory(path);
    Files.createFile(path.resolve("a.txt"));

    PostgresCluster cluster = installation.createCluster(path);
    assertNotNull(cluster);
  }

  @Test(expected = BadProcessExecutionException.class)
  public void createClusterWithWrongLocale() throws Exception {

    Files.createDirectory(path);

    PostgresClusterCreationOptions options = PostgresClusterCreationOptions
        .fromDefault()
        .defaultEncoding()
        .withLocale("wertwert")
        .defaultSuperUser()
        .withoutDataChecksums();

    PostgresCluster cluster = installation.createCluster(path, options);
    assertNotNull(cluster);
  }

  @Test(expected = BadProcessExecutionException.class)
  public void createClusterWithEncoding() throws Exception {

    Files.createDirectory(path);

    PostgresClusterCreationOptions options = PostgresClusterCreationOptions
        .fromDefault()
        .withEncoding("wertwert")
        .defaultLocale()
        .defaultSuperUser()
        .withoutDataChecksums();

    PostgresCluster cluster = installation.createCluster(path, options);
    assertNotNull(cluster);
  }

  @Test
  public void createClusterInNonExistingTwoLevelFolder() throws Exception {
    PostgresCluster cluster = installation.createCluster(twoLevelPath);
    assertNotNull(cluster);
  }



}