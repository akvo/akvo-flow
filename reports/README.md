# reports

A HTTP layer on top of existing `applet` functionality:

* Generating reports
* Importing data.

## Dependencies

* The `project.clj` file defines a dependency to `[exporterapplet "1.0.0"]`
  this needs to be added in your local [Maven](https://maven.apache.org/) cache

````
mvn install:install-file -Dfile=path/to/exporterapplet.jar \
    -DartifactId=exporterapplet \
    -Dversion=1.0.0 \
    -DgroupId=exporterapplet \
    -Dpackaging=jar
````

## License

Copyright © 2013 Stichting Akvo (Akvo Foundation)

Akvo FLOW is free software: you can redistribute it and modify it 
under the terms of the GNU Affero General Public License (AGPL) 
as published by the Free Software Foundation, 
either version 3 of the License or any later version.
