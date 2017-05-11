import com.ongres.pgdeploy.PgDeploy;
import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.clusters.PostgresClusterCreationOptions;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.exceptions.ExtraFoldersFoundException;
import com.ongres.pgdeploy.installations.BadClusterException;
import com.ongres.pgdeploy.installations.ClusterDirectoryNotEmptyException;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.UnitNotAvailableForPropertyException;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.WrongTypePropertyException;
import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by pablo on 10/05/17.
 */
public class Main {

  private static final Path installationPath = Paths.get("installation").toAbsolutePath();
  private static final Path clusterPath = Paths.get("cluster").toAbsolutePath();
  private static final String logFile = "logfile";



  public static void main(String[] args) throws Exception {
    System.out.println(installationPath.toString());
    PgDeploy pgDeploy = new PgDeploy();

    final Optional<PostgresInstallationSupplier> supplier =
        pgDeploy.findSupplier(9, 6, 2, Platform.LINUX);

    try {
      if (!supplier.isPresent()) {
        throw new IOException();
      }

      PostgresInstallation installation = null;
      PostgresCluster cluster = null;
      try {
        installation = pgDeploy.install(supplier.get(),
            PgDeploy.InstallOptions.binaries().withInclude().withShare(), installationPath);
      } catch (BadInstallationException | ExtraFoldersFoundException | IOException e) {
        e.printStackTrace();
        throw e;
      }


      try {
        cluster = installation.createCluster(clusterPath,
            PostgresClusterCreationOptions.fromDefault()
            .defaultEncoding()
            .defaultLocale()
            .withSuperUser("postgres")
            .withoutDataChecksums()
        );
      } catch (BadClusterException | IOException
          | InterruptedException | ClusterDirectoryNotEmptyException e) {
        e.printStackTrace();
        throw e;
      }

      try {
        cluster.config(
            cluster.createConfigBuilder()
                .withProperty("autovacuum", false)
                .withProperty("port", 9541)
                .build(), logFile);
      } catch (IOException | BadProcessExecutionException
          | WrongTypePropertyException | UnitNotAvailableForPropertyException e) {
        e.printStackTrace();
        throw e;
      }

      try {
        cluster.start(logFile);
      } catch (BadProcessExecutionException | IOException e) {
        e.printStackTrace();
        throw e;
      }

      try {
        cluster.config(cluster.createConfigBuilder().withProperty("port", 9543).build(), logFile);
      } catch (IOException | BadProcessExecutionException
          | WrongTypePropertyException | UnitNotAvailableForPropertyException e) {
        e.printStackTrace();
        throw e;
      }
    } finally {
      tearDown(installationPath);
      //tearDown(clusterPath);
    }

  }


  public static void tearDown(Path path) {
    if (Files.exists(path)) {
      deleteFolder(path.toFile());
    }

  }

  public static void deleteFolder(File folder) {
    File[] files = folder.listFiles();
    if (files != null) { //some JVMs return null for empty dirs
      for (File f: files) {
        if (f.isDirectory()) {
          deleteFolder(f);
        } else {
          f.delete();
        }
      }
    }
    folder.delete();
  }
}
