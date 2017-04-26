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
import com.ongres.pgdeploy.core.router.Router;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;

import javax.annotation.Nonnull;

@Immutable
public abstract class PostgresInstallation {

  public abstract Router getRouter();

  public abstract Path getPath();

  public abstract PostgresCluster createCluster(@Nonnull Path destination)
      throws BadClusterCreationException;

}
