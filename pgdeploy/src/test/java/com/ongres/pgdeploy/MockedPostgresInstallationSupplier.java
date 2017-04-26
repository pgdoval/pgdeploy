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
package com.ongres.pgdeploy;

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.Platform;

class MockedPostgresInstallationSupplier extends AbstractPostgresInstallationSupplier {

  public MockedPostgresInstallationSupplier(int majorVersion, int minorVersion, int revision, Platform platform, String extraVersion) {
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
    this.revision = revision;
    this.platform = platform;
    this.extraVersion = extraVersion;
  }



}
