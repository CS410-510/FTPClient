# CS410-510
## Build
From the project's main directory (wherever the project's `pom.xml` lives), run:

`mvn package`

This will compile, run tests, and package the project with its dependencies into the `target` subdirectory.
## Run
Once the package is done, you can run the program with:

`java -jar target/FTPClient-1.0-SNAPSHOT.jar`

and follow it up with any command-line args you'd like to run it with.
## Clean up old artifacts/builds
It may be a good idea to clean between packages if you're switching between branches frequently. From the project's main directory, run:

`mvn clean`

to remove any old artifacts from previous builds. This typically removes the `target` subdirectory and its contents.
