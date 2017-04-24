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
package com.ongres.pgdeploy;

import com.ongres.pgdeploy.core.BadInstallationException;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationFolder;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import net.jcip.annotations.Immutable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;

@Immutable
public class PgDeploy {

  private final Iterable<PostgresInstallationSupplier> supplierCandidates;

  public PgDeploy() {
    this(ServiceLoader.load(PostgresInstallationSupplier.class));
  }

  protected PgDeploy(Iterable<PostgresInstallationSupplier> supplierCandidates) {
    this.supplierCandidates = supplierCandidates;
  }



  public Optional<PostgresInstallationSupplier> findSupplier(
          int major, int minor, int revision, @Nonnull Platform platform) {
    return findSupplier(major, minor, revision, platform, null);
  }


  public Optional<PostgresInstallationSupplier> findSupplier(
          int major, int minor, int revision, @Nonnull Platform platform, String extraVersion) {

    final Iterator<PostgresInstallationSupplier> iterator = supplierCandidates.iterator();

    PostgresInstallationSupplier supplierCandidate;

    while (iterator.hasNext()) {
      supplierCandidate = iterator.next();
      if (isSupplierSuitable(supplierCandidate, major, minor, revision, platform, extraVersion)) {
        return Optional.of(supplierCandidate);
      }
    }

    return Optional.empty();

  }



  private boolean isSupplierSuitable( PostgresInstallationSupplier supplier,
          int major, int minor, int revision, Platform platform, String extraVersion) {

    boolean result = supplier.getMajorVersion() == major
                    && supplier.getMinorVersion() == minor
                    && supplier.getRevision() == revision
                    && supplier.getPlatform() == platform;

    if (extraVersion != null) {
      result &= (Objects.equals(supplier.getExtraVersion(), extraVersion));
    }

    return result;
  }


  public PostgresInstallation install(@Nonnull PostgresInstallationSupplier supplier,
                                      @Nonnull InstallOptions options, @Nonnull Path destination)
          throws BadInstallationException, IOException {

    List<PostgresInstallationFolder> folders = options.toFolderList();
    supplier.unzipFolders(destination, folders);
    supplier.checkInstallation(destination, folders);

    return new PostgresInstallation(supplier, destination);
  }

  public static class InstallOptions {

    private boolean share = false;
    private boolean include = false;

    private InstallOptions() {
    }

    public static InstallOptions binaries() {
      return new InstallOptions();
    }

    public InstallOptions withShare() {
      share = true;
      return this;
    }

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
