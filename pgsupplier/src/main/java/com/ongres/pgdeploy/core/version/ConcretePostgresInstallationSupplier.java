
package com.ongres.pgdeploy.core.version;

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.RelativeRoute;

import java.util.Arrays;

class ConcretePostgresInstallationSupplier extends AbstractPostgresInstallationSupplier {

  public ConcretePostgresInstallationSupplier() {
    super(majorVersion, minorVersion, revision, platform, extraVersion,
        new RelativeRoute(Arrays.asList("src", "main", "resources", "code.zip")));
  }

}
