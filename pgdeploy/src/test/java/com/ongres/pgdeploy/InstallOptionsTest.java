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