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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

 
class ProcessBuilderWrapper {


  static Process runProcess(
      Path pathToCommand, String exceptionMessage,
      List<String> arguments, String processDescription)
      throws IOException, InterruptedException, BadProcessExecutionException {

    if (!Files.exists(pathToCommand)) {
      throw new IOException(exceptionMessage);
    }

    List<String> args = new ArrayList<>();
    args.add(pathToCommand.toAbsolutePath().toString());
    args.addAll(arguments);

    ProcessBuilder processBuilder = new ProcessBuilder().command(args);

    Process process = processBuilder.start();

    process.waitFor();

    if (process.exitValue() != 0) {
      String errorOutput = getErrorOutputFromProcess(process).collect(Collectors.joining("\n"));

      if (errorOutput.isEmpty()) {
        errorOutput = getOutputFromProcess(process).collect(Collectors.joining("\n"));
      }

      throw BadProcessExecutionException.create(errorOutput, processDescription);
    }


    return process;
  }



  static Stream<String> getOutputFromProcess(Process process) throws IOException {
    return fromStream(process.getInputStream());
  }

  static Stream<String> getErrorOutputFromProcess(Process process) throws IOException {
    return fromStream(process.getErrorStream());
  }

  private static Stream<String> fromStream(InputStream is) throws IOException {
    return new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))).lines();
  }

}
