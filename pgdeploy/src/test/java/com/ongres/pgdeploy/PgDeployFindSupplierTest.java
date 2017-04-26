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
package com.ongres.pgdeploy;

import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PgDeployFindSupplierTest {

  private PgDeploy pgDeploy;
  private String myExtraVersion = "my";
  private String otherExtraVersion = "other";

  @Test
  public void checkExistingSupplierWithoutExtraVersionButNotRequired()
  {
    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);

    Optional<PostgresInstallationSupplier> supplier =
            pgDeploy.findSupplier(0, 0, 0, Platform.LINUX);

    assertTrue("Existing supplier not found", supplier.isPresent());
    assertEquals("Existing supplier not mocked", mockedSupplier, supplier.get());
  }


  @Test
  public void checkSupplierWithDifferentMajor()
  {

    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);

    assertFalse("Unexpected supplier found",
            pgDeploy.findSupplier(1, 0, 0, Platform.LINUX).isPresent());
  }


  @Test
  public void checkSupplierWithDifferentMinor()
  {

    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);

    assertFalse("Unexpected supplier found",
            pgDeploy.findSupplier(0, 1, 0, Platform.LINUX).isPresent());
  }


  @Test
  public void checkSupplierWithDifferentRevision()
  {

    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);

    assertFalse("Unexpected supplier found",
            pgDeploy.findSupplier(0, 0, 1, Platform.LINUX).isPresent());
  }


  @Test
  public void checkSupplierWithDifferentPlatform()
  {

    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);

    assertFalse("Unexpected supplier found",
            pgDeploy.findSupplier(00, 0, 0, Platform.WINDOWS).isPresent());
  }


  @Test
  public void checkExistingSupplierWithExtraVersionButNotRequired()
  {
    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, myExtraVersion);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);


    Optional<PostgresInstallationSupplier> supplier =
            pgDeploy.findSupplier(0, 0, 0, Platform.LINUX);


    assertTrue("Existing supplier not found", supplier.isPresent());
    assertEquals("Existing supplier not mocked", mockedSupplier, supplier.get());
  }

  @Test
  public void checkExistingSupplierWithExtraVersionSameAsRequired()
  {
    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, myExtraVersion);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);


    Optional<PostgresInstallationSupplier> supplier =
            pgDeploy.findSupplier(0, 0, 0, Platform.LINUX, myExtraVersion);


    assertTrue("Existing supplier not found", supplier.isPresent());
    assertEquals("Existing supplier not mocked", mockedSupplier, supplier.get());
  }

  @Test
  public void checkExistingSupplierWithExtraVersionDifferentFromRequired()
  {
    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, myExtraVersion);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);


    assertFalse(pgDeploy.findSupplier(0, 0, 0, Platform.LINUX, otherExtraVersion).isPresent());

  }

  @Test
  public void checkExistingSupplierWithoutExtraVersionButRequired()
  {
    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    PostgresInstallationSupplier mockedSupplier = new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null);
    suppliers.add(mockedSupplier);

    pgDeploy = new PgDeploy(suppliers);


    assertFalse(pgDeploy.findSupplier(0, 0, 0, Platform.LINUX, myExtraVersion).isPresent());

  }





}
