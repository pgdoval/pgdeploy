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
package com.ongres.pgdeploy.clusters;

import com.ongres.pgdeploy.pgconfig.PostgresConfig;
import com.ongres.pgdeploy.wrappers.PgCtlWrapper;
import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

 
public abstract class PostgresCluster {

  /** Starts a cluster by calling pg_ctl start on it
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, a folder with permissions bigger than rwx------, etc.
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public  void start()
      throws BadProcessExecutionException, IOException, InterruptedException {
    start(null);
  }

  /** Starts a cluster by calling pg_ctl start on it
   * @param logFile The route to the optional log file. If provided, the output
   *     of the command is redirected to it.
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, a folder with permissions bigger than rwx------, etc.
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public abstract void start(@Nullable Path logFile)
      throws BadProcessExecutionException, IOException, InterruptedException;

  /** Stops a cluster by calling pg_ctl stop on it
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, an already stopped cluster, etc.
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public void stop()
      throws BadProcessExecutionException, IOException, InterruptedException {
    stop(null);
  }

  /** Stops a cluster by calling pg_ctl stop on it
   * @param logFile The route to the optional log file. If provided, the output
   *     of the command is redirected to it.
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, an already stopped cluster, etc.
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public abstract void stop(@Nullable Path logFile)
      throws BadProcessExecutionException, IOException, InterruptedException;

  /** Gets the status of a cluster by calling pg_ctl status on it
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, a non-existing folder, etc.
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public Status status()
      throws BadProcessExecutionException, IOException, InterruptedException {
    return status(null);
  }

  /** Gets the status of a cluster by calling pg_ctl status on it
   * @param logFile The route to the optional log file. If provided, the output
   *     of the command is redirected to it.
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, a non-existing folder, etc.
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public abstract Status status(@Nullable Path logFile)
      throws BadProcessExecutionException, IOException, InterruptedException;

  /** Updates the config in postgresql.conf and then calls pg_ctl restart/reload
   * @param config An instance of {@link PostgresConfig}, obtained via the builder returned by
   *               {@link PostgresCluster#createConfigBuilder()}
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, a non-existing folder, etc.
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public void config(PostgresConfig config)
      throws IOException, BadProcessExecutionException, InterruptedException {
    config(config, null);
  }

  /** Updates the config in postgresql.conf and then calls pg_ctl restart/reload
   * @param config An instance of {@link PostgresConfig}, obtained via the builder returned by
   *               {@link PostgresCluster#createConfigBuilder()}
   * @throws IOException The pg_ctl file or the cluster don't exist
   * @throws BadProcessExecutionException If the command wasn't able to run. For example, running
   *     it in an empty folder, a non-existing folder, etc.
   * @throws InterruptedException If the execution of the pg_ctl process is interrupted
   */
  public abstract void config(@Nonnull PostgresConfig config, @Nullable Path logFile)
      throws IOException, BadProcessExecutionException, InterruptedException;

  public abstract PostgresConfig.Builder createConfigBuilder();

  public void setPgHbaConf(@Nonnull String content)
      throws IOException, BadProcessExecutionException, InterruptedException {
    setPgHbaConf(content, null);
  }

  public abstract void setPgHbaConf(@Nonnull String content, @Nullable Path logFile)
      throws IOException, BadProcessExecutionException, InterruptedException;

  public void setPgHbaConf(@Nonnull Path originalFile)
      throws IOException, BadProcessExecutionException, InterruptedException {
    setPgHbaConf(originalFile, null);
  }

  public abstract void setPgHbaConf(@Nonnull Path originalFile, @Nullable Path logFile)
      throws IOException, BadProcessExecutionException, InterruptedException;


  public enum Status {
    ACTIVE,
    STOPPED;

    public static Status fromPgCtlStatus(PgCtlWrapper.Status pgCtlStatus) {
      return valueOf(pgCtlStatus.name());
    }
  }

}
