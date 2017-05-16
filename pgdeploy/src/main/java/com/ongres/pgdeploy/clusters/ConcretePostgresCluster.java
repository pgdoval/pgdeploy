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
package com.ongres.pgdeploy.clusters;

import com.ongres.pgdeploy.core.router.DefaultRouter;
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import com.ongres.pgdeploy.pgconfig.DefaultPropertyParser;
import com.ongres.pgdeploy.pgconfig.PostgresConfig;
import com.ongres.pgdeploy.pgconfig.PropertyParser;
import com.ongres.pgdeploy.wrappers.PgCtlWrapper;
import com.ongres.pgdeploy.wrappers.PgHbaConfWrapper;
import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;
import com.ongres.pgdeploy.wrappers.postgresqlconf.PostgreSqlConfWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Created by pablo on 25/04/17.
 */

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "URF_UNREAD_FIELD",
    justification = "They will be used, but not yet")
public class ConcretePostgresCluster extends PostgresCluster {

  private final Path directory;
  private final Router router;
  private final PropertyParser parser;
  private final PgCtlWrapper pgCtlWrapper;

  public ConcretePostgresCluster(
      Path directory, PostgresInstallation installation, PropertyParser parser) {
    this( directory, installation.getPath(), parser, installation.getRouter());
  }

  public ConcretePostgresCluster(
      Path directory, Path installationDirectory, PropertyParser parser, Router router) {
    this( new PgCtlWrapper(router.routeToPgCtl(installationDirectory), directory),
        directory, parser, router);
  }

  ConcretePostgresCluster(
      PgCtlWrapper wrapper, Path directory, PropertyParser parser, Router router) {
    pgCtlWrapper = wrapper;
    this.directory = directory;
    this.router = router;
    this.parser = parser;
  }

  ConcretePostgresCluster(PgCtlWrapper wrapper, Path directory) {
    pgCtlWrapper = wrapper;
    this.directory = directory;
    this.router = DefaultRouter.getInstance();
    this.parser = DefaultPropertyParser.getInstance();
  }

  @Override
  public void start(@Nullable Path logFile)
      throws BadProcessExecutionException, IOException, InterruptedException {
    pgCtlWrapper.start(logFile);
  }

  @Override
  public void stop(@Nullable Path logFile)
      throws BadProcessExecutionException, IOException, InterruptedException {
    pgCtlWrapper.stop(logFile);
  }

  @Override
  public Status status(@Nullable Path logFile)
      throws BadProcessExecutionException, IOException, InterruptedException {

    return Status.valueOf(pgCtlWrapper.status(logFile).name());
  }

  @Override
  public void config(PostgresConfig config, @Nullable Path logFile)
      throws IOException, BadProcessExecutionException, InterruptedException {

    Status status = status(null);
    PostgreSqlConfWrapper.updateConfFile(router.routeToPostgresqlConf(directory), config);

    boolean needToRestart = config.asStream().anyMatch(entry -> entry.getKey().isNeedToRestart());

    if (status == Status.ACTIVE) {
      if (needToRestart) {
        pgCtlWrapper.restart(logFile);
      } else {
        pgCtlWrapper.reload(logFile);
      }
    }
  }

  @Override
  public PostgresConfig.Builder createConfigBuilder() {
    return new PostgresConfig.Builder(parser);
  }

  @Override
  public void setPgHbaConf(String content, @Nullable Path logFile)
      throws IOException, BadProcessExecutionException, InterruptedException {

    PgHbaConfWrapper.overwriteConf(router.routeToPgHbaConf(directory),content);
    pgCtlWrapper.restart(logFile);

  }

  @Override
  public void setPgHbaConf(Path originalFile, @Nullable Path logFile)
      throws IOException, BadProcessExecutionException, InterruptedException {
    setPgHbaConf(Files.lines(originalFile).collect(Collectors.joining("\n")), logFile);
  }
}
