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
package com.ongres.pgdeploy.wrappers;

import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by pablo on 26/04/17.
 */
public class PgCtlWrapperTest {

  private Path clusterPath =
      Paths.get("../pgdeploy/src/test/resources/cluster");

  private Path stdErrClusterPath =
      Paths.get("../pgdeploy/src/test/resources/stdErrCluster");

  private Path stdOutClusterPath =
      Paths.get("../pgdeploy/src/test/resources/stdOutCluster");

  private Path pgCtlPath =
      Paths.get("../pgdeploy/src/test/resources/installation/bin/pg_ctl");

  @Test
  public void testStatusOnStoppedCluster() throws Exception {
    PgCtlWrapper.status(pgCtlPath, clusterPath, null);
  }

  @Test
  public void testStatusOnActiveCluster() throws Exception {

  }

  @Test
  public void testStatusOnRestartingCluster() throws Exception {

  }

  @Test
  public void testStatusOnNonExistingCluster() throws Exception {

  }

  @Ignore
  @Test
  public void testStartOnStoppedCluster() throws Exception {
    PgCtlWrapper.start(pgCtlPath, clusterPath, null);
  }

  @Test(expected = BadProcessExecutionException.class)
  public void testStartOnStdErrFailingCluster() throws Exception {
    PgCtlWrapper.start(pgCtlPath, stdErrClusterPath, null);
  }

  @Test(expected = BadProcessExecutionException.class)
  public void testStartOnStdOutFailingCluster() throws Exception {
    PgCtlWrapper.start(pgCtlPath, stdOutClusterPath, null);
  }
}