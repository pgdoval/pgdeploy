
 # pgdeploytest
 
 
 ## Overview
 
 This project is meant for testing the pgdeploy library by using it the way the user would. 
 It can also be used for demos. 
 
 ## Usage
 
 The way of interacting with the library is by running the Main class, which contains a main method.
 The code in it provides the user with an endless loop in which he can find suppliers for a determined 
 version of postgres, install them in a folder, use those installations to create clusters,
 and interact with those clusters.
 
 ## Adding suppliers

 
  The simplest way of getting new versions would be to simply add the dependencies for them,
   supposing that they have been published in maven central. This would have to be pasted
   in pom.xml:
 ```
 
        <dependency>
            <groupId>com.ongres</groupId>
            <artifactId>pgsupplier</artifactId>
            <version> the version </version>
        </dependency>
 
  
  ```

 
 Another way for getting new versions comes by the fact that the project contains a maven 
 repository pointing at its own folder libs. In order for the user to
 include new suppliers, the needed steps are:
 * To obtain the new supplier, probably some pgsupplier-version.jar obtained via 
 the [pgsupplier](../pgsupplier) project
 * To create 'version' folder under libs/grid/pgsupplier, and paste the jar file there
 * To add the dependency in pom.xml:
 
 ```
 
        <dependency>
            <groupId>grid</groupId>
            <artifactId>pgsupplier</artifactId>
            <version> the version </version>
        </dependency>
 
  
  ```
