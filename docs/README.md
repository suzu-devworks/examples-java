# examples-java

## Table of Contents <!-- omit in toc -->

- [Development](#development)
  - [How the project was initialized](#how-the-project-was-initialized)

## Development

### How the project was initialized

This project was initialized with the following command:

```shell
## root pom.xml
mvn archetype:generate \
  -DarchetypeGroupId=org.codehaus.mojo.archetypes \
  -DarchetypeArtifactId=pom-root \
  -DgroupId=com.github.suzu_devworks.examples \
  -DartifactId=examples-java \
  -DinteractiveMode=false

mv examples-java/pom.xml .
rm -r examples-java/

## modules
mvn archetype:generate \
  -DarchetypeGroupId=org.codehaus.mojo.archetypes \
  -DarchetypeArtifactId=pom-root \
  -DgroupId=com.github.suzu_devworks.examples \
  -DartifactId=modules \
  -DinteractiveMode=false

## modules/examples-various
mvn archetype:generate \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DgroupId=com.github.suzu_devworks.examples \
  -DartifactId=examples-various \
  -DoutputDirectory=modules \
  -DinteractiveMode=false

```
