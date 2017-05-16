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

import com.ongres.pgdeploy.clusters.ConcretePostgresCluster;
import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.clusters.PostgresClusterCreationOptions;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.pgconfig.DefaultPropertyParser;
import com.ongres.pgdeploy.pgconfig.PropertyParser;
import com.ongres.pgdeploy.wrappers.InitDbWrapper;
import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnull;

 
public class ConcretePostgresInstallation extends PostgresInstallation {

  private final Router router;
  private final Path path;

  public ConcretePostgresInstallation(Router router, Path path) {
    this.router = router;
    this.path = path;
  }

  public Router getRouter() {
    return router;
  }

  public Path getPath() {
    return path;
  }

  public PostgresCluster createCluster(@Nonnull Path destination)
      throws BadClusterException, IOException,
      InterruptedException, ClusterDirectoryNotEmptyException, BadProcessExecutionException {

    return createCluster(destination, PostgresClusterCreationOptions.defaultOptions());
  }

  public PostgresCluster createCluster(
      @Nonnull Path destination, PostgresClusterCreationOptions options)
      throws BadClusterException, IOException,
      InterruptedException, ClusterDirectoryNotEmptyException, BadProcessExecutionException {

    if (Files.exists(destination)) {

      boolean isEmpty;

      try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(destination)) {
        isEmpty = !dirStream.iterator().hasNext();
      }

      if (!isEmpty) {
        throw ClusterDirectoryNotEmptyException.fromPath(destination);
      }
    }
    
    InitDbWrapper.run(router.routeToInitDb(path),destination, options.toArgumentList());

    checkCluster(destination);

    PropertyParser parser = (router instanceof PostgresInstallationSupplier)
        ? (PostgresInstallationSupplier) router : DefaultPropertyParser.getInstance();

    return new ConcretePostgresCluster(destination,this, parser);

  }

  @Override
  public void checkCluster(@Nonnull Path destination) throws BadClusterException {
    throwIfNotExists(router.routeToPgHbaConf(destination));
    throwIfNotExists(router.routeToPostgresqlConf(destination));
  }

  private void throwIfNotExists(@Nonnull Path toCheck) throws BadClusterException {
    if (!Files.exists(toCheck)) {
      throw BadClusterException.fromPath(toCheck);
    }
  }

}
