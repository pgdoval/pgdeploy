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
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

  private PgCtlWrapper pgCtlWrapper;

  @Before
  public void cleanBefore() {
    pgCtlWrapper = new PgCtlWrapper(pgCtlPath, clusterPath);
    clean();
  }

  @After
  public void cleanAfter() {
    clean();
  }

  private void clean() {

    try {
      if(pgCtlWrapper.status(null) == PgCtlWrapper.Status.ACTIVE) {
        pgCtlWrapper.stop(null);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (BadProcessExecutionException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testStatusOnStoppedCluster() throws Exception {
    pgCtlWrapper.start(null);
    TimeUnit.MILLISECONDS.sleep(200);
    pgCtlWrapper.stop(null);
    PgCtlWrapper.Status status = pgCtlWrapper.status(null);

    assertEquals(PgCtlWrapper.Status.STOPPED, status);
  }

  @Test
  public void testStatusOnActiveCluster() throws Exception {

    pgCtlWrapper.start(null);
    PgCtlWrapper.Status status = pgCtlWrapper.status(null);
    pgCtlWrapper.stop(null);

    assertEquals(PgCtlWrapper.Status.ACTIVE, status);
  }

  @Test (expected = BadProcessExecutionException.class)
  public void testStatusOnNonExistingCluster() throws Exception {
    pgCtlWrapper = new PgCtlWrapper(pgCtlPath, stdErrClusterPath);
    pgCtlWrapper.status(null);
  }

  @Test
  public void testStartAndStopOnStoppedCluster() throws Exception {
    pgCtlWrapper.start(null);
    TimeUnit.MILLISECONDS.sleep(200);
    pgCtlWrapper.stop(null);
  }

  @Test(expected = BadProcessExecutionException.class)
  public void testStartTwiceOnStoppedCluster() throws Exception {
    try {
      pgCtlWrapper.start(null);
      TimeUnit.MILLISECONDS.sleep(200);
      pgCtlWrapper.start(null);
    }
    finally {
      pgCtlWrapper.stop(null);
    }
  }

  @Test(expected = BadProcessExecutionException.class)
  public void testStopClusterTwice() throws Exception {
    pgCtlWrapper.start(null);
    TimeUnit.MILLISECONDS.sleep(200);
    pgCtlWrapper.stop(null);
    TimeUnit.MILLISECONDS.sleep(200);
    pgCtlWrapper.stop(null);
  }

  @Test(expected = BadProcessExecutionException.class)
  public void testStartOnStdErrFailingCluster() throws Exception {
    pgCtlWrapper = new PgCtlWrapper(pgCtlPath, stdErrClusterPath);
    pgCtlWrapper.start(null);
  }

  @Test(expected = BadProcessExecutionException.class)
  public void testStartOnStdOutFailingCluster() throws Exception {
    pgCtlWrapper = new PgCtlWrapper(pgCtlPath, stdOutClusterPath);
    pgCtlWrapper.start(null);
  }


  @Test
  public void testLogClusterOperations() throws Exception {
    String logfileName = "logFile";

    Path logFile = Paths.get(logfileName);

    try {

      Files.createFile(logFile);

      pgCtlWrapper.start(logfileName);
      pgCtlWrapper.stop(logfileName);

      String fileContent = Files.readAllLines(logFile).stream().reduce("", (s1, s2) -> s1 + '\n' + s2);

      assertFalse(fileContent.isEmpty());
    } finally {
      Files.delete(logFile);
    }

  }

}