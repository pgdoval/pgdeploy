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
import java.nio.file.Paths;
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

    installation = new PostgresInstallation(supplier,
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