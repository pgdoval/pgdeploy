
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
 
 
 
 
 ##Usage
 
 The library allows for several operations. This is the interaction proposed for these use cases:
 
 ###Installing a specific library in a location
 
 We retrieve the specific PostgreSQL supplier for the desired version and platform.
 If the version is not available, the user can decide what to do. We finally 
 specify the folder in which the binaries will appear, and the binary folders we
 want to unzip (*bin* and *lib* are mandatory, *share* and *include* are optional).
 ```
PostgresSupplier supplier = Library.findSupplier(Version.9_6_1, Platform.LINUX, Optional.empty())
                                .getOrElse(throw new UnsupportedVersionException());

Installation installation = Library.install(supplier, InstallOptions.basicInstallation().withShare(), Paths.get("/home/username/pg962));
 ```
  
 ###Creating a cluster in a location

 We use a previously created installation object to create the cluster.
 ```
Cluster cluster = installation.createCluster(Paths.get("/home/username/clusters/pg962/cluster));
 ```
  
 ###Starting, stopping or checking the status of a cluster 
 
 We can perform these operations on the created cluster
 ```
 cluster.start();
 cluster.stop();
 Status status = cluster.status();
 ```
  
 ###Configuring a cluster 

 We can create a config object via its builder. Only the cluster that will consume the config object can create it.
 For the configuration properties, different data types and units are allowed, but this is handled opaquely. 
 The programmer only has to specify the properties the way he would do in PostgreSQLql.conf . 
 ```
Config config = cluster.createConfigBuilder()
                    .withProperty("port", "5432")
                    .withProperty("shared_buffers", "2GB")
                    .withProperty("random_page_cost", "3.5")
                    .build();        
                     
 
cluster.config(config); //this method calls restart. Future versions will be able to call reload if restart is not needed
 ```
 