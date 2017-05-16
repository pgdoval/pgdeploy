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

import com.ongres.pgdeploy.clusters.ConcretePostgresCluster;
import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.core.InstallationChecker;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationFolder;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.exceptions.ExtraFoldersFoundException;
import com.ongres.pgdeploy.core.pgversion.PostgresMajorVersion;
import com.ongres.pgdeploy.core.router.DefaultRouter;
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.installations.BadClusterException;
import com.ongres.pgdeploy.installations.ConcretePostgresInstallation;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import com.ongres.pgdeploy.pgconfig.DefaultPropertyParser;
import com.ongres.pgdeploy.pgconfig.PropertyParser;
import net.jcip.annotations.Immutable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;

/**
 * This class represents the entry point to the system. It allows for searching
 * for a specific Postgres version so, if its binaries are registered on the system,
 * the user will be able to install it.
 * */
@Immutable
public class PgDeploy {

  private final Iterable<PostgresInstallationSupplier> supplierCandidates;

  public PgDeploy() {
    this(ServiceLoader.load(PostgresInstallationSupplier.class));
  }

  protected PgDeploy(Iterable<PostgresInstallationSupplier> supplierCandidates) {
    this.supplierCandidates = supplierCandidates;
  }



  /** Searches the classpath seeking for an implementation of <tt>PostgresInstallationSupplier</tt>
  * that complies to the requirements specified by the parameters
   * @param major Postgres major version (9 for 9.3.2)
   * @param minor Postgres minor version (3 for 9.3.2)
   * @param platform The OS platform, represented by the enum {@link Platform Platform}
   * @return An optional value containing the supplier found, or <tt>Optional.empty()</tt> if
   *     no complying supplier has been found.
   */
  public Optional<PostgresInstallationSupplier> findSupplier(
      PostgresMajorVersion major, int minor, @Nonnull Platform platform) {
    return findSupplier(major, minor, platform, null);
  }


  /** Searches the classpath seeking for an implementation of <tt>PostgresInstallationSupplier</tt>
   * that complies to the requirements specified by the parameters
   * @param major Postgres major version (9 for 9.3.2)
   * @param minor Postgres minor version (3 for 9.3.2)
   * @param platform The OS platform, represented by the enum {@link Platform Platform}
   * @param extraVersion This field provides a way for the user to tag his versions and find
   *                     them with the specific tag.
   * @return An optional value containing the supplier found, or <tt>Optional.empty()</tt> if
   *     no complying supplier has been found.
   */
  public Optional<PostgresInstallationSupplier> findSupplier(
      PostgresMajorVersion major, int minor, @Nonnull Platform platform, String extraVersion) {

    final Iterator<PostgresInstallationSupplier> iterator = supplierCandidates.iterator();

    PostgresInstallationSupplier supplierCandidate;

    while (iterator.hasNext()) {
      supplierCandidate = iterator.next();
      if (isSupplierSuitable(supplierCandidate, major, minor, platform, extraVersion)) {
        return Optional.of(supplierCandidate);
      }
    }

    return Optional.empty();

  }



  private boolean isSupplierSuitable(
      PostgresInstallationSupplier supplier, PostgresMajorVersion major, int minor,
      Platform platform, String extraVersion) {

    boolean result = supplier.getMajorVersion().equals(major)
        && supplier.getMinorVersion() == minor
        && supplier.getPlatform() == platform;

    if (extraVersion != null) {
      result &= (Objects.equals(supplier.getExtraVersion(), extraVersion));
    }

    return result;
  }


  /** Installs a specific postgres version in a folder
   * @param supplier the <tt>PostgresInstallationSupplier</tt>, typically obtained via
   *     {@link PgDeploy#findSupplier(PostgresMajorVersion, int, Platform) findSupplier}.
   * @param options Instance of the <tt>InstallOptions</tt> inner class specifying which
   *                folders in the postgres folder will be copied to the installation folder. Both
   *                <tt>bin</tt> and <tt>lib</tt> folders are mandatory, while <tt>share</tt> and
    *               <tt>include</tt> are optional.
   * @param destination The path where postgres binaries will be copied
   * @return An instance of {@link PostgresInstallation} that is pointed to the
   *     <tt>destination</tt> folder
   * @throws BadInstallationException When not all the desired folders have been copied
   * @throws ExtraFoldersFoundException When there are significant folders in the
   *     <tt>destination</tt> folder other than the ones desired. This may be a desirable behaviour
   *     in case there was a previous installation, so it is kept as a different exception.
   * @throws IOException In case other I/O errors occur: the source folder in the supplier
   *     doesn't exist, or the destination folder is not writable
   */
  public PostgresInstallation install(
      @Nonnull PostgresInstallationSupplier supplier,
      @Nonnull InstallOptions options,
      @Nonnull Path destination)
      throws BadInstallationException, ExtraFoldersFoundException, IOException {

    List<PostgresInstallationFolder> folders = options.toFolderList();
    supplier.unzipFolders(destination, folders);
    supplier.checkInstallation(destination, folders);

    return new ConcretePostgresInstallation(supplier, destination);
  }


  /** Retrieves a previously existing PostgresInstallation.
   * @param supplier The {@link PostgresInstallationSupplier} instance to be used as {@link Router}
   *                 and {@link com.ongres.pgdeploy.pgconfig.PropertyParser PropertyParser} by the
   *                installation or by the clusters it creates.
   * @param destination The location of the existing installation.
   * @return An instance of {@link PostgresInstallation} to manage the installation in the
   *     desired folder.
   * @throws IOException When there are I/O problems with the folder, for instance, when the folder
   *     doesn't exist.
   * @throws BadInstallationException When any of the necessary folders (<tt>bin</tt> and
   *     <tt>lib</tt>) is not present in the installation folder. Folders <tt>share</tt> and
   *     <tt>include</tt> are optional.
   */
  public PostgresInstallation retrieveInstallation(
      @Nonnull PostgresInstallationSupplier supplier,
      @Nonnull Path destination)
      throws BadInstallationException, IOException {
    return retrieveInstallationWithRouter(supplier, destination);
  }


  /** Retrieves a previously existing PostgresInstallation. As no
   *     {@link PostgresInstallationSupplier} instance is provided, default versions of
   *     {@link Router} and
   *     {@link com.ongres.pgdeploy.pgconfig.PropertyParser PropertyParser} are used instead.
   * @param destination The location of the existing installation.
   * @return An instance of {@link PostgresInstallation} to manage the installation in the
   *     desired folder.
   * @throws IOException When there are I/O problems with the folder, for instance, when the folder
   *     doesn't exist.
   * @throws BadInstallationException When any of the necessary folders (<tt>bin</tt> and
   *     <tt>lib</tt>) is not present in the installation folder. Folders <tt>share</tt> and
   *     <tt>include</tt> are optional.
   */
  public PostgresInstallation retrieveInstallation(
      @Nonnull Path destination)
      throws BadInstallationException, IOException {
    return retrieveInstallationWithRouter(DefaultRouter.getInstance(), destination);
  }

  private PostgresInstallation retrieveInstallationWithRouter(
      @Nonnull Router router,
      @Nonnull Path destination)
      throws BadInstallationException, IOException {

    if (!Files.exists(destination)) {
      throw new IOException("Installation folder " + destination.toString() + "not found");
    }

    //We check that the folder contains at least bin and lib folders
    InstallationChecker.checkInstallationIsComplete(
        destination, InstallOptions.binaries().toFolderList());

    return new ConcretePostgresInstallation(router, destination);
  }


  /** Retrieves a previously existing cluster and links it to the installation in the desired path.
   *     As no {@link PostgresInstallationSupplier} instance is provided, default versions of
   *     {@link Router} and
   *     {@link com.ongres.pgdeploy.pgconfig.PropertyParser PropertyParser} are used instead.
   * @param clusterPath The path to the directory which contains the cluster
   * @param installationPath The path to the directory which contains the installation files
   * @return An instance of {@link PostgresCluster} to manage the operations of the desired cluster
   * @throws BadInstallationException When the installation path does not contain an installation.
   * @throws IOException When either the installation folder or the cluster folder don't exist.
   * @throws BadClusterException When the cluster path is not pointing to a real cluster
   */
  public PostgresCluster retrieveCluster(
      Path clusterPath, Path installationPath)
      throws BadInstallationException, IOException, BadClusterException {

    return retrieveCluster(clusterPath, installationPath,
        DefaultPropertyParser.getInstance(), DefaultRouter.getInstance());
  }

  /** Retrieves a previously existing cluster and links it to the installation in the desired path.
   * @param clusterPath The path to the directory which contains the cluster
   * @param installationPath The path to the directory which contains the installation files
   * @param supplier The {@link PostgresInstallationSupplier} instance to be used by the cluster as
   *                 {@link Router} and {@link PropertyParser}.
   * @return An instance of {@link PostgresCluster} to manage the operations of the desired cluster
   * @throws BadInstallationException When the installation path does not contain an installation.
   * @throws IOException When either the installation folder or the cluster folder don't exist.
   * @throws BadClusterException When the cluster path is not pointing to a real cluster
   */
  public PostgresCluster retrieveCluster(
      Path clusterPath, Path installationPath, PostgresInstallationSupplier supplier)
      throws BadInstallationException, IOException, BadClusterException {

    return retrieveCluster(clusterPath, installationPath, supplier, supplier);
  }

  private PostgresCluster retrieveCluster(
      Path directory, Path installationDirectory, PropertyParser parser, Router router)
      throws IOException, BadInstallationException, BadClusterException {


    if (!Files.exists(directory)) {
      throw new IOException("Cluster folder " + directory.toString() + "not found");
    }
    PostgresInstallation installation =
        retrieveInstallationWithRouter(router, installationDirectory);

    installation.checkCluster(directory);


    return new ConcretePostgresCluster(directory, installationDirectory, parser, router);
  }

  public static class InstallOptions {

    private boolean share = false;
    private boolean include = false;

    private InstallOptions() {
    }

    /**
     * @return A basic InstallOptions instance representing mandatory
     *     folders <tt>bin</tt> and <tt>lib</tt>
     */
    public static InstallOptions binaries() {
      return new InstallOptions();
    }

    /** Add the <tt>share</tt> folder and return itself
     * @return Itself
     */
    public InstallOptions withShare() {
      share = true;
      return this;
    }

    /** Add the <tt>include</tt> folder and return itself
     * @return Itself
     */
    public InstallOptions withInclude() {
      include = true;
      return this;
    }

    List<PostgresInstallationFolder> toFolderList() {
      List<PostgresInstallationFolder> result = new ArrayList<>();

      result.add(PostgresInstallationFolder.BIN);
      result.add(PostgresInstallationFolder.LIB);

      if (share) {
        result.add(PostgresInstallationFolder.SHARE);
      }

      if (include) {
        result.add(PostgresInstallationFolder.INCLUDE);
      }

      return result;
    }

  }
}
