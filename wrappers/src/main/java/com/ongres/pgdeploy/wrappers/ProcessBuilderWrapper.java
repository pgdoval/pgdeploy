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
package com.ongres.pgdeploy.wrappers;

import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pablo on 26/04/17.
 */
class ProcessBuilderWrapper {


  static Process runProcess(
      Path pathToCommand, String exceptionMessage, List<String> arguments)
      throws IOException, InterruptedException {

    if (!Files.exists(pathToCommand)) {
      throw new IOException(exceptionMessage);
    }

    List<String> args = new ArrayList<>();
    args.add(pathToCommand.toAbsolutePath().toString());
    args.addAll(arguments);

    ProcessBuilder processBuilder = new ProcessBuilder().command(args);

    return processBuilder.start();
  }



  static String getOutputFromProcess(Process process) throws IOException {
    return fromStream(process.getInputStream());
  }

  static String getErrorOutputFromProcess(Process process) throws IOException {
    return fromStream(process.getErrorStream());
  }

  private static String fromStream(InputStream is) throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(is, "UTF-8"));


    StringBuilder builder = new StringBuilder();
    String line;
    while ( (line = reader.readLine()) != null) {
      builder.append(line);
      builder.append(System.getProperty("line.separator"));
    }

    return builder.toString();
  }



  static void throwIfOutputContainsErrors(String output, String badWord, String processDescription)
      throws BadProcessExecutionException {

    if (output.contains(badWord)) {
      throw BadProcessExecutionException.create(output, processDescription);
    }
  }

  static void throwIfErrorOutputContainsErrors(String output, String processDescription)
      throws BadProcessExecutionException {

    if (!output.isEmpty()) {
      throw BadProcessExecutionException.create(output, processDescription);
    }

  }

}
