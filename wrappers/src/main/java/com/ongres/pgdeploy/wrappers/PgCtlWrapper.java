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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by pablo on 26/04/17.
 */
public class PgCtlWrapper {

  private static final String badWord = "FATAL:";
  private static final String start = "start";
  //private static final String stop = "stop";
  private static final String status = "status";

  private static Process getProcess(
      Path pgCtlPath, Path clusterPath, @Nullable String logfile, String command)
      throws IOException, InterruptedException {

    final String message = "pg_ctl file "
        + pgCtlPath.toAbsolutePath().toString()
        + " not found";

    List<String> args = new ArrayList<>();

    args.add("-D");
    args.add(clusterPath.toString());

    if (logfile != null) {
      args.add("-l");
      args.add(logfile);
    }
    
    args.add(command);

    return ProcessBuilderWrapper.runProcess(pgCtlPath, message, args);    
  }

  private static String getProcessOutput(
      Path pgCtlPath, Path clusterPath, @Nullable String logfile, String command)
      throws IOException, InterruptedException, BadProcessExecutionException {

    Process process = getProcess(pgCtlPath, clusterPath, logfile, command);

    String output = ProcessBuilderWrapper.getOutputFromProcess(process);
    String errorOutput = ProcessBuilderWrapper.getErrorOutputFromProcess(process);


    System.out.println(output);
    System.out.println("********************");
    System.out.println(errorOutput);


    String processDescription = "pg_ctl - " + command;
    ProcessBuilderWrapper.throwIfOutputContainsErrors(output, badWord, processDescription);
    ProcessBuilderWrapper.throwIfErrorOutputContainsErrors(errorOutput, processDescription);

    return output;
  }

  public static void status(Path pgCtlPath, Path clusterPath, @Nullable String logfile)
      throws IOException, InterruptedException, BadProcessExecutionException {

    getProcessOutput(pgCtlPath, clusterPath, logfile, status);
  }


  public static void start(Path pgCtlPath, Path clusterPath, @Nullable String logfile)
      throws IOException, InterruptedException, BadProcessExecutionException {

    getProcessOutput(pgCtlPath, clusterPath, logfile, start);
  }



}