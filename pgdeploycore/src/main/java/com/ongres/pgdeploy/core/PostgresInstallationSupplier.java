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
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.pgconfig.PropertyParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface PostgresInstallationSupplier {

  /** Tells whether the supplier is or not compliant to the constraints (postgres version, OS,
   * architecture...) defined in the incoming parameter.
   */
  boolean accepts(PostgresInstallationSupplierFeatures features);

  /** Unpacks in the desired <tt>destination</tt> the packed installation
   * binaries contained in the supplier.
   * @param destination The path where to unpack the packed binaries
   * @param folders The folders within the packed binaries to be unpacked
   * @throws NonWritableDestinationException In case the destination folder is not writable.
   * @throws UnreachableBinariesException In case the packed binaries are unreachable
   * @throws IOException In case any of the files contained inside the packed binaries file is,
   *     for any reason, unreachable
   */
  void unpackFolders(Path destination, List<PostgresInstallationFolder> folders)
      throws IOException, NonWritableDestinationException, UnreachableBinariesException;

  /** Checks that the installation has been performed correctly
   * @param destination The path where the installation is
   * @param folders The significant folders expected to be there
   * @throws BadInstallationException When not all the desired folders are there
   * @throws ExtraFoldersFoundException When there are significant folders in the
   *     <tt>destination</tt> folder other than the ones desired. This may be a desirable behaviour
   *     in case there was a previous installation, so it is kept as a different exception.
   */
  void checkInstallation(Path destination, List<PostgresInstallationFolder> folders)
      throws BadInstallationException, ExtraFoldersFoundException;

  /**
   * @return The most suitable router for the supplier
   */
  Router getRouter();

  /**
   * @return The most suitable property parser for the supplier
   */
  PropertyParser getParser();
}
