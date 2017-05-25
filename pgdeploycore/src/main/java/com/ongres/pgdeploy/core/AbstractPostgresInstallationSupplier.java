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
package com.ongres.pgdeploy.core;

import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.exceptions.ExtraFoldersFoundException;
import com.ongres.pgdeploy.core.exceptions.NonWritableDestinationException;
import com.ongres.pgdeploy.core.exceptions.UnreachableBinariesException;
import com.ongres.pgdeploy.core.pgversion.PostgresMajorVersion;
import com.ongres.pgdeploy.core.router.DefaultRouter;
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.core.unpack.ObtainResourceStrategy;
import com.ongres.pgdeploy.core.unpack.ObtainResourceStrategyFactory;
import com.ongres.pgdeploy.core.unpack.PackageMode;
import com.ongres.pgdeploy.core.unpack.UnpackFoldersStrategy;
import com.ongres.pgdeploy.core.unpack.UnpackFoldersStrategyFactory;
import com.ongres.pgdeploy.pgconfig.DefaultPropertyParser;
import com.ongres.pgdeploy.pgconfig.PropertyParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public abstract class AbstractPostgresInstallationSupplier implements PostgresInstallationSupplier {

  protected final PostgresInstallationSupplierFeatures features;

  protected final Path routeToPackedCode;

  protected final boolean fromJar;

  protected final PackageMode packageMode;

  protected AbstractPostgresInstallationSupplier( PostgresMajorVersion majorVersion,
      int minorVersion, Platform platform, Path path, boolean fromJar, PackageMode packageMode) {

    this( new PostgresInstallationSupplierFeatures(majorVersion, minorVersion, platform),
        path, fromJar, packageMode);
  }

  protected AbstractPostgresInstallationSupplier( PostgresMajorVersion majorVersion,
      int minorVersion, Platform platform, String extraVersion, Path path,
      boolean fromJar, PackageMode packageMode) {

    this( new PostgresInstallationSupplierFeatures(
        majorVersion, minorVersion, platform, extraVersion), path, fromJar, packageMode);
  }

  protected AbstractPostgresInstallationSupplier(
      PostgresInstallationSupplierFeatures features, Path path,
      boolean fromJar, PackageMode packageMode) {
    this.features = features;
    this.routeToPackedCode =
        fromJar ? path.resolve(features.getPackedFileResourceName(packageMode)) : path;
    this.fromJar = fromJar;
    this.packageMode = packageMode;
  }

  /** Simply compares the features required to the ones that the supplier offers.
   */
  @Override
  public boolean accepts(PostgresInstallationSupplierFeatures features) {
    return this.features.equals(features);
  }

  @Override
  public void unpackFolders(Path destination, List<PostgresInstallationFolder> folders)
      throws IOException, NonWritableDestinationException, UnreachableBinariesException {

    ObtainResourceStrategy obtainResourceStrategy =
        ObtainResourceStrategyFactory.getUnpackFoldersStrategy(
            fromJar, routeToPackedCode);

    UnpackFoldersStrategy unpackFoldersStrategy =
        UnpackFoldersStrategyFactory.getUnpackFoldersStrategy(packageMode);

    InputStream stream = obtainResourceStrategy.obtainResource();

    unpackFoldersStrategy.unpackFolders(destination, folders, stream);
  }

  @Override
  public void checkInstallation(Path destination, List<PostgresInstallationFolder> folders)
      throws BadInstallationException, ExtraFoldersFoundException {

    InstallationChecker.checkInstallationIsComplete(destination, folders);
    InstallationChecker.checkInstallationExtraFolders(destination, folders);

  }

  @Override
  public Router getRouter() {
    return DefaultRouter.getInstance();
  }

  @Override
  public PropertyParser getParser() {
    return DefaultPropertyParser.getInstance();
  }
}
