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
package com.ongres.pgdeploy;

import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import org.junit.Before;
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
