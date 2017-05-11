package com.ongres.pgdeploy.core.v9_6_2.x64;

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.RelativeRoute;

import java.util.Arrays;

/**
 * Created by pablo on 10/05/17.
 */
public class ConcretePostgresInstallationSupplier extends AbstractPostgresInstallationSupplier {

  public ConcretePostgresInstallationSupplier() {
    super(9, 6, 2, Platform.LINUX,
        new RelativeRoute(Arrays.asList("pgsuppliersample", "src", "main", "resources", "code.zip"))
            .asRelativePath());

  }
}
