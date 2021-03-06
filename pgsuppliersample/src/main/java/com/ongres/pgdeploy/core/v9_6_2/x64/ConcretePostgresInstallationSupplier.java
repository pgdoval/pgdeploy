package com.ongres.pgdeploy.core.v9_6_2.x64;

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.RelativeRoute;
import com.ongres.pgdeploy.core.pgversion.Pre10PostgresMajorVersion;
import com.ongres.pgdeploy.core.unpack.PackageMode;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

 
public class ConcretePostgresInstallationSupplier extends AbstractPostgresInstallationSupplier {

  public ConcretePostgresInstallationSupplier() throws URISyntaxException {
    super(new Pre10PostgresMajorVersion(9, 6), 2, new Platform("Linux", "x64"),
        Paths.get(ConcretePostgresInstallationSupplier
            .class.getProtectionDomain().getCodeSource().getLocation().getPath())
            .resolve("code.zip"), false, PackageMode.ZIP);
  }
}
