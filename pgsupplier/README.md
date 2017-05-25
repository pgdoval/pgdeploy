
 # pgdeploytest
 
 
 ## Overview
 
 This project autodeploys itself with the desired version of postgres for the desired os and arch.
 
 ## Usage
 
 The way to use it is by running the following command (changing the parameters) in a console:
 ```
 
 mvn clean generate-resources package -Dpostgres.major=9.5 -Dpostgres.minor=2 -Dpostgres.arch=x64 -Dpostgres.os=linux
 
  
  ```
