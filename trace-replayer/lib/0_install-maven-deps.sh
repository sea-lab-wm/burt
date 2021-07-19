#!/bin/bash

mvn install:install-file "-Dfile=org.eclipse.jdt.core_3.8.1.v20120531-0637.jar" "-DgroupId=org.eclipse.jdt" "-DartifactId=core" "-Dversion=3.8.1.v20120531-0637" "-Dpackaging=jar"
mvn install:install-file "-Dfile=org.eclipse.core.jobs_3.5.200.v20120521-2346.jar" "-DgroupId=org.eclipse.core" "-DartifactId=jobs" "-Dversion=3.5.200.v20120521-2346" "-Dpackaging=jar"
mvn install:install-file "-Dfile=org.eclipse.core.resources_3.8.0.v20120522-2034.jar" "-DgroupId=org.eclipse.core" "-DartifactId=resources" "-Dversion=3.8.0.v20120522-2034" "-Dpackaging=jar"
mvn install:install-file "-Dfile=org-eclipse-jface-3.6.2.jar" "-DgroupId=org.eclipse" "-DartifactId=jface" "-Dversion=3.6.2" "-Dpackaging=jar"
mvn install:install-file "-Dfile=hierarchyviewer2lib.jar" "-DgroupId=hierarchyviewer" "-DartifactId=hierarchyviewer" "-Dversion=x.y.z" "-Dpackaging=jar"