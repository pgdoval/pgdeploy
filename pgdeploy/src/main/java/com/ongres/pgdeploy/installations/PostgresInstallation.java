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
package com.ongres.pgdeploy.installations;

import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.clusters.PostgresClusterCreationOptions;
import com.ongres.pgdeploy.core.router.Router;
import net.jcip.annotations.Immutable;

import java.io.IOException;
import java.nio.file.Path;

import javax.annotation.Nonnull;

@Immutable
public abstract class PostgresInstallation {

  public abstract Router getRouter();

  public abstract Path getPath();

  /** Creates a cluster by running initdb. By default, it uses UTF-8 encoding.
   * @param destination The folder where the cluster will be created. It may not exist, but it is
   *                    mandatory that if it exists, it is empty. It may exist and not be writable
   *                    as well. The initdb process will do its job anyway.
   * @return An instance of {@link PostgresCluster} that will be able to perform
   *     operations on this cluster
   * @throws BadClusterException The cluster created does not have the
   *     expected contents
   * @throws IOException The path to initdb is wrong
   * @throws InterruptedException The execution of the command lasted more than
   *     expected (40 seconds) and thus has been stopped.
   * @throws ClusterDirectoryNotEmptyException The precondition that the <tt>destination</tt>
   *     folder had to be empty wasn't fulfilled.
   */
  public abstract PostgresCluster createCluster(@Nonnull Path destination)
      throws BadClusterException, IOException,
      InterruptedException, ClusterDirectoryNotEmptyException;

  /** Creates a cluster by running initdb. By default, it uses UTF-8 encoding.
   * @param destination The folder where the cluster will be created. It may not exist, but it is
   *                    mandatory that if it exists, it is empty. It may exist and not be writable
   *                    as well. The initdb process will do its job anyway.
   * @param options The options for cluster creation: encoding, locale, super user and data
   *                checksums. Created via <tt>fromDefault</tt> static method in class
   *                {@link PostgresClusterCreationOptions}.
   * @return An instance of {@link PostgresCluster} that will be able to perform
   *     operations on this cluster
   * @throws BadClusterException The cluster created does not have the
   *     expected contents
   * @throws IOException The path to initdb is wrong
   * @throws InterruptedException The execution of the command lasted more than
   *     expected (40 seconds) and thus has been stopped.
   * @throws ClusterDirectoryNotEmptyException The precondition that the <tt>destination</tt>
   *     folder had to be empty wasn't fulfilled.
   */
  public abstract PostgresCluster createCluster(
      @Nonnull Path destination, PostgresClusterCreationOptions options)
      throws BadClusterException, IOException,
      InterruptedException, ClusterDirectoryNotEmptyException;

  public abstract void checkCluster(@Nonnull Path destination) throws BadClusterException;
}
