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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by pablo on 8/05/17.
 */
public class InstallationChecker {

  public static void checkInstallationIsComplete(
      Path destination, List<PostgresInstallationFolder> folders)
      throws BadInstallationException {

    List<Path> notFound = new ArrayList<>();
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
  }

  public static void checkInstallationExtraFolders(
      Path destination, List<PostgresInstallationFolder> folders)
      throws ExtraFoldersFoundException {

    List<String> extraFound = new ArrayList<>();

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
}
