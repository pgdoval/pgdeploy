import com.ongres.pgdeploy.PgDeploy;
import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.clusters.PostgresClusterCreationOptions;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.exceptions.ExtraFoldersFoundException;
import com.ongres.pgdeploy.core.exceptions.NonWritableDestinationException;
import com.ongres.pgdeploy.core.exceptions.UnreachableBinariesException;
import com.ongres.pgdeploy.core.pgversion.Post10PostgresMajorVersion;
import com.ongres.pgdeploy.core.pgversion.PostgresMajorVersion;
import com.ongres.pgdeploy.core.pgversion.Pre10PostgresMajorVersion;
import com.ongres.pgdeploy.installations.BadClusterException;
import com.ongres.pgdeploy.installations.ClusterDirectoryNotEmptyException;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import com.ongres.pgdeploy.pgconfig.properties.PropertyValue;
import com.ongres.pgdeploy.pgconfig.properties.Unit;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.PropertyNotFoundException;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.UnitNotAvailableForPropertyException;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.WrongTypePropertyException;
import com.ongres.pgdeploy.wrappers.exceptions.BadProcessExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {

  private static Scanner scanner = new Scanner(System.in);

  private static boolean stop = false;

  public static void main(String[] args) throws Exception {

    final PgDeploy pgDeploy = new PgDeploy();
    PostgresCluster cluster = null;
    PostgresInstallation installation = null;
    PostgresInstallationSupplier supplier = null;
    Path logFile = null;

    while (!stop) {

      List<Options> options = new ArrayList<>();

      options.add(new Options("Find a supplier", "find"));

      if (supplier != null) {
        options.add(new Options("Install a supplier", "install"));
      }
      if (installation != null) {
        options.add(new Options("Create a cluster", "cluster"));
      }
      if (cluster != null) {
        options.add(new Options("Update the logfile", "logFile"));
      }
      if (cluster != null) {
        options.add(new Options("Autoconfig a cluster for start", "autoconfig"));
      }
      if (cluster != null) {
        options.add(new Options("Start a cluster", "pgctl"));
      }
      if (cluster != null) {
        options.add(new Options("Config a cluster as you wish", "config"));
      }

      options.add(new Options("Stop", "stop"));


      Map<Integer, Options> optionsWithIndex = IntStream.range(0, options.size())
          .mapToObj(i -> new AbstractMap.SimpleEntry<Integer, Options>(i + 1, options.get(i)))
          .collect(Collectors.toMap(
              AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


      String prompt = "\n\n\nWhat do you want to do?";
      System.out.println(prompt);
      optionsWithIndex.forEach( (key, value) -> System.out.println(key + " - " + value.text));

      int selectedInt = scanner.nextInt();

      Options selectedOption = optionsWithIndex.get(selectedInt);

      if (selectedOption == null) {
        System.out.println( "Invalid option " + selectedInt);
      } else {
        switch (selectedOption.name) {
          case "find":
            Optional<PostgresInstallationSupplier> supplierOptional = getSupplier(pgDeploy);
            if (supplierOptional.isPresent()) {
              supplier = supplierOptional.get();
            }
            break;
          case "install":
            installation = install(pgDeploy, supplier).orElse(installation);
            break;
          case "cluster":
            cluster = createCluster(installation).orElse(cluster);
            break;
          case "logFile":
            logFile = setLogfile();
            break;
          case "autoconfig":
            configClusterForStart(cluster, logFile);
            break;
          case "pgctl":
            startCluster(cluster, logFile);
            break;
          case "config":
            customConfigCluster(cluster, logFile);
            break;
          case "stop":
            stop = true;
            break;
          default:
            break;
        }
      }
    }
  }

  private static int getIntegerInputForSentence(String statement) {
    System.out.println(statement);
    return scanner.nextInt();
  }

  private static String getStringInputForSentence(String statement) {
    System.out.println(statement);
    return scanner.next();
  }

  private static Optional<PostgresInstallationSupplier> getSupplier(PgDeploy pgDeploy) {
    System.out.println("Find a supplier");

    int first = getIntegerInputForSentence("Enter first number from major:");
    int second = getIntegerInputForSentence(
        "Enter second number from major (0 for only first):");

    PostgresMajorVersion major = (second == 0)
        ? new Post10PostgresMajorVersion(first)
        : new Pre10PostgresMajorVersion(first, second);

    int minor = getIntegerInputForSentence("Enter minor:");

    String os = getStringInputForSentence("Enter platform (LINUX, WINDOWS, MACOS):");

    String arch = getStringInputForSentence("Enter arch (x64, x86):");

    Platform platform = new Platform(os, arch);

    return pgDeploy.findSupplier(major, minor, platform);

  }

  private static Optional<PostgresInstallation> install(
      PgDeploy pgDeploy, PostgresInstallationSupplier supplier) {

    String home = System.getProperty("user.home");
    Path installationPath = Paths.get(home).resolve(
        getStringInputForSentence("Path for installation (from " + home + "):"));

    try {
      return Optional.of(pgDeploy.install(supplier,
          PgDeploy.InstallOptions.binaries().withInclude().withShare(), installationPath));

    } catch (BadInstallationException | ExtraFoldersFoundException | IOException
        | NonWritableDestinationException | UnreachableBinariesException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
    }
    return Optional.empty();
  }


  private static Path setLogfile() {
    String home = System.getProperty("user.home");
    return Paths.get(home).resolve(
        getStringInputForSentence("Path for logfile (from " + home + "):"));
  }


  private static Optional<PostgresCluster> createCluster(PostgresInstallation installation) {
    String home = System.getProperty("user.home");
    Path clusterPath = Paths.get(home).resolve(
        getStringInputForSentence("Path for cluster (from " + home + "):"));
    try {
      return Optional.of(installation.createCluster(clusterPath,
          PostgresClusterCreationOptions.fromDefault()
              .defaultEncoding()
              .defaultLocale()
              .withSuperUser("postgres")
              .withoutDataChecksums()
      ));
    } catch (BadClusterException | IOException | InterruptedException
        | ClusterDirectoryNotEmptyException | BadProcessExecutionException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
    }
    return Optional.empty();
  }

  private static void configClusterForStart(PostgresCluster cluster, Path logFile) {
    try {
      cluster.config(
          cluster.createConfigBuilder()
              .withProperty("autovacuum", false)
              .withProperty("port", 9541)
              .build(), logFile);
      System.out.println("Cluster configured!");
    } catch (IOException | BadProcessExecutionException
        | PropertyNotFoundException | InterruptedException
        | WrongTypePropertyException | UnitNotAvailableForPropertyException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
    }
  }

  private static void startCluster(PostgresCluster cluster, Path logFile) {

    try {
      cluster.start(logFile);
      System.out.println("Cluster started!");
    } catch (BadProcessExecutionException | IOException
        | InterruptedException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
    }
  }

  private static void customConfigCluster(PostgresCluster cluster, Path logFile) {
    String key;
    String value;
    String type;
    String unitString;
    PropertyValue propertyValue;

    Map<String, PropertyValue> map = new HashMap<>();

    while (true) {
      key = getStringInputForSentence("Name of the property (- to stop):");

      if (Objects.equals(key, "-")) {
        break;
      }
      value = getStringInputForSentence("New value for the property:");
      type = getStringInputForSentence(
          "Type for the property: (i)nteger, (r)eal or string (default)");
      unitString = getStringInputForSentence(
          "Unit for the property: (n)one, TB, GB, MB, kB, ms, s, min, h, d").toUpperCase();

      Unit unit;
      if (Arrays.asList("TB", "GB", "MB", "KB", "MS", "S", "MIN", "H", "D").contains(unitString)) {
        unit = Unit.valueOf(unitString);
      } else {
        unit = Unit.NONE;
      }
      switch (type) {
        case "i":
          propertyValue = new PropertyValue<Long>(Long.parseLong(value), unit);
          break;
        case "r":
          propertyValue = new PropertyValue<Double>(Double.parseDouble(value), unit);
          break;
        default:
          propertyValue = new PropertyValue<String>(value, unit);
          break;
      }
      map.put(key, propertyValue);
    }



    try {
      cluster.config(cluster.createConfigBuilder().fromPropertyMap(map).build(), logFile);
      System.out.println("Cluster configured!");
    } catch (IOException | BadProcessExecutionException
        | PropertyNotFoundException | InterruptedException
        | WrongTypePropertyException | UnitNotAvailableForPropertyException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
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

  private static class Options {
    private String text;
    private String name;

    public Options(String text, String name) {
      this.text = text;
      this.name = name;
    }

  }
}
