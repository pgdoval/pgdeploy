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