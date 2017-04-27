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
import com.ongres.pgdeploy.core.router.DefaultRouter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class AbstractPostgresInstallationSupplier implements PostgresInstallationSupplier {

  protected int majorVersion;
  protected int minorVersion;
  protected int revision;
  protected Platform platform;
  protected String extraVersion;

  protected RelativeRoute routeToZippedCode;


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
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
      value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE",
      justification = "The value itself has no interest")
  public void unzipFolders(Path destination, List<PostgresInstallationFolder> folders)
      throws IOException {

    int buffer = 2048;

    File file = routeToZippedCode.asRelativeFile();

    try (ZipFile zip = new ZipFile(file)) {
      String newPath = destination.toAbsolutePath().toString();

      if (!new File(newPath).mkdir()) {
        throw new NonWritableDestinationException("Unable to create or open file: " + newPath);
      }

      Enumeration zipFileEntries = zip.entries();

      while (zipFileEntries.hasMoreElements()) {

        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
        String currentEntry = entry.getName();

        if (!folders.stream().anyMatch(
            folder -> currentEntry.startsWith(folder.getStringId() + "/"))) {
          continue;
        }

        File destFile = new File(newPath, currentEntry);
        File destinationParent = destFile.getParentFile();

        // create the parent directory structure if needed
        destinationParent.mkdirs();

        if (!entry.isDirectory()) {
          try (BufferedInputStream is = new BufferedInputStream(
              zip.getInputStream(entry))) {

            int currentByte;
            // establish buffer for writing file
            byte[] data = new byte[buffer];

            // write the current file to disk

            FileOutputStream fos = new FileOutputStream(destFile);

            try (BufferedOutputStream dest = new BufferedOutputStream(fos, buffer)) {

              // read and write until last byte is encountered
              while ((currentByte = is.read(data, 0, buffer)) != -1) {
                dest.write(data, 0, currentByte);
              }
            }
          }
        }
      }
    } catch (FileNotFoundException e) {
      throw new UnreachableBinariesException(e.getMessage());
    }

  }

  @Override
  public void checkInstallation(Path destination, List<PostgresInstallationFolder> folders)
      throws BadInstallationException, ExtraFoldersFoundException {

    List<Path> notFound = new ArrayList<>();
    List<String> extraFound = new ArrayList<>();
    List<Path> notADirectory = new ArrayList<>();

    folders.stream()
        .map(folder -> destination.resolve(folder.getStringId()))
        .forEach(path -> {
          if (!Files.exists(path)) {
            notFound.add(path);
          } else {
            if (!Files.isDirectory(path)) {
              notADirectory.add(path);
            }
          }
        });


    if (!notFound.isEmpty() || !notADirectory.isEmpty()) {
      throw BadInstallationException.fromNotFoundAndNotADirectory(notFound, notADirectory);
    }

    Stream.of(PostgresInstallationFolder.values())
        .filter(folder -> !folders.contains(folder))
        .map(folder -> destination.resolve(folder.getStringId()))
        .forEach(path -> {
          if (Files.exists(path)) {
            extraFound.add(path.getFileName().toString());
          }
        });

    if (!extraFound.isEmpty()) {
      List<String> requested = folders.stream()
          .map(PostgresInstallationFolder::getStringId)
          .collect(Collectors.toList());

      throw ExtraFoldersFoundException.fromExpectedAndFound(requested,extraFound);
    }
  }

  @Override
  public Path routeToPostgresqlConf(Path basePath) {
    return DefaultRouter.getInstance().routeToPostgresqlConf(basePath);
  }

  @Override
  public Path routeToPgHbaConf(Path basePath) {
    return DefaultRouter.getInstance().routeToPgHbaConf(basePath);
  }


  @Override
  public Path routeToInitDb(Path basePath) {
    return DefaultRouter.getInstance().routeToInitDb(basePath);
  }


}