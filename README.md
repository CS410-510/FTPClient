# CS410-510
## Build
From the project's main directory (wherever the project's `pom.xml` lives), run:

`mvn package`

This will compile, run tests, and package the project with its dependencies into the `target` subdirectory.
## Run
Once the package is done, you can run the program with:

`java -jar target/FTPClient-1.0-SNAPSHOT.jar`

and follow it up with any command-line args you'd like to run it with. Here are the current args for command-line usage:

```
usage: FTPClient
 -c,--copy <PATHS>           Copy a directory on the server.
 -C,--connect <SERVER>       Begin or switch the connection context.
 -d,--delete <PATH>          Delete a file, use with -R for a directory.
 -g,--get <PATHS>            Get file(s).
 -h,--help                   Print this help information.
 -i,--dir <PATH>             Create a directory.
 -l,--list <PATH>            List files and directories.
 -L,--local                  Source is the local machine.
 -m,--modify <OCTAL, PATH>   Modify a file's permissions on the server.
 -N,--disconnect             Disconnect from the current connection
                             context.
 -p,--put <PATHS>            Put file(s).
 -R,--recursive              Where allowed, target is a directory.
```
## Clean up old artifacts/builds
It may be a good idea to clean between packages if you're switching between branches frequently. From the project's main directory, run:

`mvn clean`

to remove any old artifacts from previous builds. This typically removes the `target` subdirectory and its contents.
## Generate site/docs
If you'd like to generate all sorts of Javadocs, test results, and other documentation for the project, run:

`mvn site`

and a website containing all documentation relevant to the project will be created in the `target/site` subdirectory, with the root at index.html.
