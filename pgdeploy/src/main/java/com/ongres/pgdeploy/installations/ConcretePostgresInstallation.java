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
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.pgconfig.DefaultPropertyParser;
import com.ongres.pgdeploy.pgconfig.PropertyParser;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnull;

/**
 * Created by pablo on 25/04/17.
 */
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
      throws BadClusterCreationException {

    checkCluster(destination);

    PropertyParser parser = (router instanceof PostgresInstallationSupplier)
        ? (PostgresInstallationSupplier) router : new DefaultPropertyParser();

    return new ConcretePostgresCluster(destination,this, parser);

  }

  protected void checkCluster(@Nonnull Path destination) throws BadClusterCreationException {
    throwIfNotExists(router.routeToPgHbaConf(destination));
    throwIfNotExists(router.routeToPostgresqlConf(destination));
  }

  protected void throwIfNotExists(@Nonnull Path toCheck) throws BadClusterCreationException {
    if (!Files.exists(toCheck)) {
      throw BadClusterCreationException.fromPath(toCheck);
    }
  }

}
