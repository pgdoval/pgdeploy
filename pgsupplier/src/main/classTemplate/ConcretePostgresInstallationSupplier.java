package com.ongres.pgdeploy.core.${package.name};

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.pgversion.PostgresMajorVersion;

import java.net.URISyntaxException;
import java.nio.file.Paths;


public class ConcretePostgresInstallationSupplier extends AbstractPostgresInstallationSupplier {

  public ConcretePostgresInstallationSupplier() throws URISyntaxException {
    super(PostgresMajorVersion.fromString("${postgres.major}").orElse(null),
        ${postgres.minor}, new Platform("${postgres.os}", "${postgres.arch}"),
        Paths.get(ConcretePostgresInstallationSupplier
            .class.getProtectionDomain().getCodeSource().getLocation().getPath()), true);
  }
}
