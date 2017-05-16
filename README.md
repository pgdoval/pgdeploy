
 # PostgreSQL Java Wrapper
 
 
 ## Overview
 
 The intent of this library is to make PostgreSQL usage transparent for the final users. It
 provides methods for:
 
 * Installing the binaries of a specific PostgreSQL version for a specific platform in the desired folder.
 * Use a specific PostgreSQL installation to create a cluster in a specified location.
 * Via the PostgreSQL installation, perform start, stop, status and config operations
 on a specified cluster.
 
 It will also include some implementations for different PostgreSQL versions and platforms,
 and a script for creating a new implementation.
 
 The code is BSD "Simplified 2 Clause" licensed (see [LICENSE](LICENSE)).
 
 
 
 
 ## Usage
 
 The library allows for several operations. This is the interaction proposed for these use cases:
 
 ### Installing a specific library in a location
 
 We retrieve the specific PostgreSQL supplier for the desired version and platform.
 If the version is not available, the user can decide what to do. We finally 
 specify the folder in which the binaries will appear, and the binary folders we
 want to unzip (*bin* and *lib* are mandatory, *share* and *include* are optional).
 ```
 
 PgDeploy pgDeploy = new PgDeploy();
PostgresInstallationSupplier supplier = pgDeploy.findSupplier(9, 6, 2, Platform.LINUX)
                                .orElseGet(throw new UnsupportedVersionException());
//or alternatively
PostgresInstallationSupplier supplier = pgDeploy.findSupplier(9, 6, 2, Platform.LINUX, "myVersion")
                                .orElseGet(throw new UnsupportedVersionException());

PostgresInstallation installation = pgDeploy.install(supplier, InstallOptions.binaries().withShare(), "/home/username/pg962");
 ```
  
 We can also retrieve a previously created installation, if we specify its supplier:
  
  ```
  PostgresInstallation installation = pgDeploy.retrieveInstallation(Paths.get("/home/username/pg962"));
   ```
  
 ### Creating a cluster in a location

 We use a previously created installation object to create the cluster.
 ```
PostgresCluster cluster = installation.createCluster(
        Paths.get("/home/username/clusters/pg962/cluster"), 
        PostgresClusterCreationOptions.defaultOptions());
 ```
 We can also retrieve a previously created cluster, if we specify the 
 installation that created it, and its supplier:
  
  ```
  PostgresCluster cluster = pgDeploy.retrieveCluster(
        Paths.get("/home/username/clusters/pg962/cluster"), //cluster path
        Paths.get("/home/username/pg962") //installation path
  );
   ```
  
 ### Starting, stopping or checking the status of a cluster 
 
 We can perform these operations on the created cluster
 ```
 cluster.start();
 cluster.stop();
 Status status = cluster.status();
 ```
  
 ### Configuring a cluster 

 We can create a config object via its builder. Only the cluster that will consume the config object can create it.
 For the configuration properties, different data types and units are allowed, but this is handled opaquely. 
 The programmer only has to specify the properties the way he would do in postgresql.conf . 
 ```
PostgresConfig config = cluster.createConfigBuilder()
                    .withProperty("port", 5432)
                    .withProperty("shared_buffers", PropertyValue.gb(2))
                    .withProperty("random_page_cost", 3.5)
                    .withProperty("autovacuum", false)
                    .build();        

//or alternatively
PostgresConfig config = cluster.createConfigBuilder()
                    .FromPropertyMap(myMapStringToPropertyValue)
                    .build();               
 
cluster.config(config); //this method calls restart. Future versions will be able to call reload if restart is not needed
 ```
 The programmer can also configure the pg_hba.conf:
  ```
 cluster.setPgHba(fileContent)
 
 //or alternatively
 cluster.setPgHba(originalFile)
  ```