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
package com.ongres.pgdeploy.clusters;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by pablo on 8/05/17.
 */
public class PostgresClusterCreationOptionsTest {

  private String encoding = "UTF-8";
  private String locale = "esES";
  private String superUser = "me";

  private PostgresClusterCreationOptions options1 =
      PostgresClusterCreationOptions
          .fromDefault()
      .defaultEncoding()
      .withLocale(locale)
      .defaultSuperUser()
      .withDataChecksums();

  private PostgresClusterCreationOptions options2 =
      PostgresClusterCreationOptions
          .fromDefault()
      .withEncoding(encoding)
      .defaultLocale()
      .withSuperUser(superUser)
      .withoutDataChecksums();

  private List<String> result1 = Arrays.asList("--locale", locale, "-k");
  private List<String> result2 = Arrays.asList("-E", encoding, "-U", superUser);

  @Test
  public void test() throws Exception {
    assertListEquals(result1, options1.toArgumentList());
    assertListEquals(result2, options2.toArgumentList());
  }

  private void assertListEquals(List<String> expected, List<String> actual) {
    assertEquals(expected.stream().collect(Collectors.joining("\n")),
        actual.stream().collect(Collectors.joining("\n")));
  }

}