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

import static org.junit.Assert.assertTrue;

public class PgDeployFindSupplierTest {

  private PgDeploy pgDeploy;

  @Before
  public void setUp() {
    List<PostgresInstallationSupplier> suppliers = new ArrayList<>();
    suppliers.add(new MockedPostgresInstallationSupplier(0, 0, 0, Platform.LINUX, null));

    pgDeploy = new PgDeploy(suppliers);
  }

  @Test
  public void checkExistingSupplier()
  {
    Optional<PostgresInstallationSupplier> supplier =
            pgDeploy.findSupplier(0, 0, 0, Platform.LINUX, null);

    assertTrue("Existing supplier not found", supplier.isPresent());
    assertTrue("Existing supplier not mocked", supplier.get() instanceof MockedPostgresInstallationSupplier);
  }


  @Test
  public void checkNonExistingSupplier()
  {
    assertTrue(!pgDeploy.findSupplier(1000, 0, 0, Platform.LINUX, null)
                    .isPresent());
  }






}
