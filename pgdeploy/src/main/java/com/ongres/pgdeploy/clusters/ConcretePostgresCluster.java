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

import com.ongres.pgdeploy.installations.PostgresInstallation;
import com.ongres.pgdeploy.pgconfig.PropertyParser;

import java.nio.file.Path;

/**
 * Created by pablo on 25/04/17.
 */

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "URF_UNREAD_FIELD",
    justification = "They will be used, but not yet")
public class ConcretePostgresCluster extends PostgresCluster {

  private final Path directory;
  private final PostgresInstallation installation;
  private final PropertyParser supplier;

  public ConcretePostgresCluster(
      Path directory, PostgresInstallation installation, PropertyParser supplier) {
    this.directory = directory;
    this.installation = installation;
    this.supplier = supplier;
  }
}
