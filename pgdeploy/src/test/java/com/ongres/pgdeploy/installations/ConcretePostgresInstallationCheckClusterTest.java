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

import com.ongres.pgdeploy.core.AbstractPostgresInstallationSupplier;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.RelativeRoute;
import com.ongres.pgdeploy.core.router.DefaultRouter;
import com.ongres.pgdeploy.core.router.Router;
import com.ongres.pgdeploy.pgconfig.PropertyParser;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by pablo on 26/04/17.
 */
public class ConcretePostgresInstallationCheckClusterTest {

  private ConcretePostgresInstallation installation;

  private Path realClusterPath= new RelativeRoute(Arrays.asList("src", "test", "resources", "cluster")).asRelativePath();
  private Path nonClusterPath= new RelativeRoute(Arrays.asList("src", "test", "resources", "installation")).asRelativePath();

  @Before
  public void setup() {
    Router router = DefaultRouter.getInstance();

    installation = new ConcretePostgresInstallation(router,
        new RelativeRoute(Arrays.asList("src", "test", "resources", "installation")).asRelativePath());


  }

  @Test
  public void checkRealCluster() throws Exception {
    installation.checkCluster(realClusterPath);
  }


  @Test(expected = BadClusterCreationException.class)
  public void checkNonCluster() throws Exception {
    installation.checkCluster(nonClusterPath);
  }

}