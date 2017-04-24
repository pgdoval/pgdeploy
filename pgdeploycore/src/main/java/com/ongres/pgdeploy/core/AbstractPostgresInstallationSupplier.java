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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPostgresInstallationSupplier implements PostgresInstallationSupplier {

  protected int majorVersion;
  protected int minorVersion;
  protected int revision;
  protected Platform platform;
  protected String extraVersion;

  @Override
  public int getMajorVersion() {
    return majorVersion;
  }

  @Override
  public int getMinorVersion() {
    return minorVersion;
  }

  @Override
  public int getRevision() {
    return revision;
  }

  @Override
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public String getExtraVersion() {
    return extraVersion;
  }

  @Override
  public void unzipFolders(Path destination, List<PostgresInstallationFolder> folders)
          throws IOException{

  }

  @Override
  public void checkInstallation(Path destination, List<PostgresInstallationFolder> folders)
          throws BadInstallationException {

    List<Path> notFound = new ArrayList<>();
    List<Path> notADirectory = new ArrayList<>();

    folders.stream()
            .map(folder -> destination.resolve(folder.getStringId()))
            .forEach(path -> {
              if (!Files.exists(path)) {
                notFound.add(path);
              }

              if (!Files.isDirectory(path)) {
                notADirectory.add(path);
              }

            });


    if (!notFound.isEmpty() || !notADirectory.isEmpty()) {
      throw BadInstallationException.fromNotFoundAndNotADirectory(notFound, notADirectory);
    }

  }
}
