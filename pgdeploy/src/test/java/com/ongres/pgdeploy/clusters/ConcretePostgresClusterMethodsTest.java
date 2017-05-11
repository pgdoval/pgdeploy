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
package com.ongres.pgdeploy.clusters;

import com.ongres.pgdeploy.core.RelativeRoute;
import com.ongres.pgdeploy.pgconfig.PostgresConfig;
import com.ongres.pgdeploy.pgconfig.properties.DataType;
import com.ongres.pgdeploy.pgconfig.properties.Property;
import com.ongres.pgdeploy.pgconfig.properties.Unit;
import com.ongres.pgdeploy.wrappers.PgCtlWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by pablo on 27/04/17.
 */
public class ConcretePostgresClusterMethodsTest {

  private ConcretePostgresCluster cluster;

  private PgCtlWrapper mockedWrapper;
  private PgCtlWrapper spy;
  private Path clusterPath = new RelativeRoute(Arrays.asList("src","test","resources","cluster")).asRelativePath();

  private String logFile = "";

  @Before
  public void setCluster() throws Exception {
    mockedWrapper = mock(PgCtlWrapper.class);
    spy = spy(mockedWrapper);

    when(spy.status(anyString())).thenAnswer(
        invocationOnMock -> PgCtlWrapper.Status.ACTIVE
    );
    when(spy.status(null)).thenAnswer(
        invocationOnMock -> PgCtlWrapper.Status.ACTIVE
    );
    doNothing().when(spy)
        .start(anyString());
    doNothing().when(spy)
        .stop(anyString());
    doNothing().when(spy)
        .restart(anyString());
    doNothing().when(spy)
        .reload(anyString());



    cluster = new ConcretePostgresCluster(spy, clusterPath);
  }


  @Test
  public void start() throws Exception {
    cluster.start(logFile);
    verify(spy, times(1)).start(logFile);
  }

  @Test
  public void stop() throws Exception {
    cluster.stop(logFile);
    verify(spy, times(1)).stop(logFile);
  }

  @Test
  public void status() throws Exception {
    cluster.status(logFile);
    verify(spy, times(1)).status(logFile);
  }

  @Test
  public void config() throws Exception {
    PostgresConfig config = new PostgresConfig.Builder(
        property -> Optional.of(new Property(property, true, DataType.INTEGER, Unit.noneList)))
        .withProperty("port", 25433)
        .build();

    cluster.config(config, logFile);

    verify(spy, times(1)).restart(logFile);

    config = new PostgresConfig.Builder(
        property -> Optional.of(new Property(property, true, DataType.INTEGER, Unit.noneList)))
        .withProperty("port", 25432)
        .build();

    cluster.config(config, logFile);
  }

  @Test
  public void configWithReload() throws Exception {
    PostgresConfig config = new PostgresConfig.Builder(
        property -> Optional.of(new Property(property, false, DataType.INTEGER, Unit.noneList)))
        .withProperty("port", 25433)
        .build();

    cluster.config(config, logFile);

    verify(spy, times(1)).reload(logFile);

    config = new PostgresConfig.Builder(
        property -> Optional.of(new Property(property, false, DataType.INTEGER, Unit.noneList)))
        .withProperty("port", 25432)
        .build();

    cluster.config(config, logFile);
  }

  @Test
  public void configStoppedCluster() throws Exception {
    when(spy.status(null)).thenAnswer(
        invocationOnMock -> PgCtlWrapper.Status.STOPPED
    );

    PostgresConfig config = new PostgresConfig.Builder(
        property -> Optional.of(new Property(property, true, DataType.INTEGER, Unit.noneList)))
        .withProperty("port", 25433)
        .build();

    cluster.config(config, logFile);

    verify(spy, times(0)).restart(logFile);
    verify(spy, times(0)).reload(logFile);

    config = new PostgresConfig.Builder(
        property -> Optional.of(new Property(property, false, DataType.INTEGER, Unit.noneList)))
        .withProperty("port", 25432)
        .build();

    cluster.config(config, logFile);
  }

  @Test
  public void setPgHbaConf() throws Exception {

    Path originalPath = new RelativeRoute(Arrays.asList("src","test","resources","or.conf")).asRelativePath();
    Path finalPath = new RelativeRoute(Arrays.asList("src","test","resources","pg_hba.conf")).asRelativePath();

    Files.createFile(originalPath);
    Files.createFile(finalPath);

    String pgHbaContent = "The content\nof the pg_hba.conf file";

    Files.write(originalPath, pgHbaContent.getBytes("UTF-8"));

    try{
      cluster = new ConcretePostgresCluster(spy, new RelativeRoute(Arrays.asList("src","test","resources")).asRelativePath());
      cluster.setPgHbaConf(originalPath, null);
      assertEquals(pgHbaContent, Files.lines(finalPath).collect(Collectors.joining("\n")));
    }
    finally {
      Files.delete(originalPath);
      Files.delete(finalPath);
    }

  }



}