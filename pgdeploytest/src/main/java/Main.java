import com.ongres.pgdeploy.PgDeploy;
import com.ongres.pgdeploy.clusters.PostgresCluster;
import com.ongres.pgdeploy.clusters.PostgresClusterCreationOptions;
import com.ongres.pgdeploy.core.Platform;
import com.ongres.pgdeploy.core.PostgresInstallationSupplier;
import com.ongres.pgdeploy.core.exceptions.BadInstallationException;
import com.ongres.pgdeploy.core.exceptions.ExtraFoldersFoundException;
import com.ongres.pgdeploy.core.pgversion.Post10PostgresMajorVersion;
import com.ongres.pgdeploy.core.pgversion.PostgresMajorVersion;
import com.ongres.pgdeploy.core.pgversion.Pre10PostgresMajorVersion;
import com.ongres.pgdeploy.installations.BadClusterException;
import com.ongres.pgdeploy.installations.ClusterDirectoryNotEmptyException;
import com.ongres.pgdeploy.installations.PostgresInstallation;
import com.ongres.pgdeploy.pgconfig.properties.PropertyValue;
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

  private static Path installationPath = Paths.get("installation").toAbsolutePath();
  private static Path clusterPath = Paths.get("cluster").toAbsolutePath();
  private static Path logFile = null;

  private static final PgDeploy pgDeploy = new PgDeploy();
  private static PostgresInstallationSupplier supplier = null;

  private static Scanner scanner = new Scanner(System.in);

  private static PostgresInstallation installation = null;
  private static PostgresCluster cluster = null;

  private static boolean stop = false;

  private static List<Options> options = Arrays.asList(
      new Options("Find a supplier", o -> true, o -> getSupplier()),
      new Options("Install a supplier", o -> (supplier != null), o -> install()),
      new Options("Create a cluster", o -> (installation != null), o -> createCluster()),
      new Options("Update the logfile", o -> (cluster != null), o -> setLogfile()),
      new Options("Autoconfig a cluster for start",
          o -> (cluster != null), o -> configClusterForStart()),
      new Options("Start a cluster", o -> (cluster != null), o -> startCluster()),
      new Options("Config a cluster as you wish",
          o -> (cluster != null), o -> customConfigCluster()),
      new Options("Stop", o -> true, o -> stop = true)
  );

  public static void main(String[] args) throws Exception {


    while (!stop) {
      List<Options> optionsList = options.stream()
          .filter(it -> it.condition.test(""))
          .collect(Collectors.toList());

      Map<Integer, Options> optionsWithIndex = IntStream.range(0, optionsList.size())
          .mapToObj(i -> new AbstractMap.SimpleEntry<Integer, Options>(i + 1, optionsList.get(i)))
          .collect(Collectors.toMap(
              AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

      System.out.println("What do you want to do?");
      optionsWithIndex.forEach( (key, value) -> System.out.println(key + " - " + value.text));

      int selectedInt = scanner.nextInt();

      Options selectedOption = optionsWithIndex.get(selectedInt);

      if (selectedOption == null) {
        System.out.println( "Invalid option " + selectedInt);
      } else {
        selectedOption.action.accept(null);
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

  private static void getSupplier() {
    System.out.println("Find a supplier");

    int first = getIntegerInputForSentence("Enter first number from major:");
    int second = getIntegerInputForSentence(
        "Enter second number from major (0 for only first):");

    PostgresMajorVersion major = (second == 0)
            ? new Post10PostgresMajorVersion(first)
            : new Pre10PostgresMajorVersion(first, second);

    int minor = getIntegerInputForSentence("Enter minor:");
    Platform platform = Platform.valueOf(
        getStringInputForSentence("Enter platform (LINUX, WINDOWS or MACOS):").toUpperCase());

    Optional<PostgresInstallationSupplier> supplierOptional =
        pgDeploy.findSupplier(major, minor, platform);

    if (supplierOptional.isPresent()) {
      supplier = supplierOptional.get();
      System.out.println("Supplier found!");
    } else {
      System.out.println("Supplier not found. Nothing changed.");
    }
  }

  private static void install() {
    String home = System.getProperty("user.home");
    installationPath = Paths.get(home).resolve(
        getStringInputForSentence("Path for installation (from " + home + "):"));

    try {
      installation = pgDeploy.install(supplier,
          PgDeploy.InstallOptions.binaries().withInclude().withShare(), installationPath);
      System.out.println("Installation complete!");
    } catch (BadInstallationException | ExtraFoldersFoundException | IOException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
    }
  }


  private static void setLogfile() {
    String home = System.getProperty("user.home");
    logFile = Paths.get(home).resolve(
        getStringInputForSentence("Path for logfile (from " + home + "):"));
    System.out.println("Logfile updated!");
  }


  private static void createCluster() {
    String home = System.getProperty("user.home");
    clusterPath = Paths.get(home).resolve(
        getStringInputForSentence("Path for cluster (from " + home + "):"));
    try {
      cluster = installation.createCluster(clusterPath,
          PostgresClusterCreationOptions.fromDefault()
              .defaultEncoding()
              .defaultLocale()
              .withSuperUser("postgres")
              .withoutDataChecksums()
      );
      System.out.println("Cluster created!");
    } catch (BadClusterException | IOException | InterruptedException
        | ClusterDirectoryNotEmptyException | BadProcessExecutionException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
    }
  }

  private static void configClusterForStart() {
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

  private static void startCluster() {

    try {
      cluster.start(logFile);
      System.out.println("Cluster started!");
    } catch (BadProcessExecutionException | IOException
        | InterruptedException e) {
      System.out.println(e.getMessage() + "\nNothing changed.");
    }
  }

  private static void customConfigCluster() {
    String key;
    String value;

    Map<String, PropertyValue> map = new HashMap<>();

    while (true) {
      key = getStringInputForSentence("Name of the property (- to stop):");

      if (Objects.equals(key, "-")) {
        break;
      }
      value = getStringInputForSentence("New value for the property:");

      map.put(key, PropertyValue.from(value));
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
    private Predicate<String> condition;
    private Consumer<Void> action;

    public Options(String text, Predicate condition, Consumer action) {
      this.text = text;
      this.condition = condition;
      this.action = action;
    }

  }
}
