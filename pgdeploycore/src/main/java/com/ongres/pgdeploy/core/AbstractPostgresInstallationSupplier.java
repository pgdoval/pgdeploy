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
import com.ongres.pgdeploy.core.unpack.UnpackFoldersStrategy;
import com.ongres.pgdeploy.core.unpack.UnzipFoldersStrategy;
import com.ongres.pgdeploy.pgconfig.DefaultPropertyParser;
import com.ongres.pgdeploy.pgconfig.PropertyParser;
import com.ongres.pgdeploy.pgconfig.properties.Property;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class AbstractPostgresInstallationSupplier implements PostgresInstallationSupplier {

  protected final PostgresInstallationSupplierFeatures features;

  protected final Path routeToZippedCode;

  protected AbstractPostgresInstallationSupplier( PostgresMajorVersion majorVersion,
      int minorVersion, Platform platform, Path routeToZippedCode) {

    this( new PostgresInstallationSupplierFeatures(majorVersion, minorVersion, platform),
        routeToZippedCode);
  }

  protected AbstractPostgresInstallationSupplier( PostgresMajorVersion majorVersion,
      int minorVersion, Platform platform, String extraVersion, Path routeToZippedCode) {

    this( new PostgresInstallationSupplierFeatures(
        majorVersion, minorVersion, platform, extraVersion), routeToZippedCode);
  }

  protected AbstractPostgresInstallationSupplier(
      PostgresInstallationSupplierFeatures features, Path routeToZippedCode) {
    this.features = features;
    this.routeToZippedCode = routeToZippedCode;
  }

  protected AbstractPostgresInstallationSupplier(Path propertiesFile, Path routeToZippedCode) {
    this(fromProperties(propertiesFile), routeToZippedCode);
  }

  private static PostgresInstallationSupplierFeatures fromProperties(Path propPath) {
    try {
      Properties properties = new Properties();
      properties.load(Files.newInputStream(propPath));
      PostgresMajorVersion major = PostgresMajorVersion
          .fromString(properties.getProperty("major"))
          .orElseThrow(IllegalArgumentException::new);

      int minor = Integer.parseInt(properties.getProperty("minor"));

      String os = properties.getProperty("os");
      String arch = properties.getProperty("arch");

      Platform platform = new Platform(os, arch);

      String extra = properties.getProperty("arch", null);

      return new PostgresInstallationSupplierFeatures(major, minor, platform, extra);

    } catch (IOException e) {
      return null;
    }

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

    UnpackFoldersStrategy strategy = new UnzipFoldersStrategy();
    strategy.unpackFolders(destination, folders, routeToZippedCode);
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
