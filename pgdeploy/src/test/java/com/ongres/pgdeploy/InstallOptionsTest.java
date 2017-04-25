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

import com.ongres.pgdeploy.core.PostgresInstallationFolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.ongres.pgdeploy.core.PostgresInstallationFolder.*;
import static org.junit.Assert.*;

/**
 * Created by pablo on 25/04/17.
 */
@RunWith(Parameterized.class)
public class InstallOptionsTest {

  @Parameterized.Parameter(0)
  public PgDeploy.InstallOptions options;

  @Parameterized.Parameter(1)
  public List<PostgresInstallationFolder> expectedFolders;

  public static List<PostgresInstallationFolder> onlyBinaries =
      Arrays.asList(BIN, LIB);

  public static List<PostgresInstallationFolder> binariesAndShare =
      Arrays.asList(BIN, LIB, SHARE);

  public static List<PostgresInstallationFolder> binariesAndInclude =
      Arrays.asList(BIN, LIB, INCLUDE);

  public static List<PostgresInstallationFolder> allFolders =
      Arrays.asList(BIN, LIB, INCLUDE, SHARE);

  @Parameterized.Parameters
  public static Collection getParams() {
    List<Object[]> result = new ArrayList<>();

    result.add(new Object []{PgDeploy.InstallOptions.binaries(), onlyBinaries});
    result.add(new Object []{PgDeploy.InstallOptions.binaries().withShare(), binariesAndShare});
    result.add(new Object []{PgDeploy.InstallOptions.binaries().withInclude(), binariesAndInclude});
    result.add(new Object []{PgDeploy.InstallOptions.binaries().withShare().withInclude(), allFolders});

    return result;
  }

  @Test
  public void toFolderList() throws Exception {
    List<PostgresInstallationFolder> folderList = options.toFolderList();

    assertTrue("All generated are expected", folderList.stream().allMatch(expectedFolders::contains));
    assertTrue("All expected are generated", expectedFolders.stream().allMatch(folderList::contains));
  }

}