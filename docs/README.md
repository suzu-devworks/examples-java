# docs

## Table of Contents

- [Tools ...](./tools/README.md)

## The way to the present

```shell
git clone https://github.com/suzu-devworks/examples-java.git
cd examples-java

mvn archetype:generate \
  -DarchetypeGroupId=org.codehaus.mojo.archetypes \
  -DarchetypeArtifactId=pom-root \
  -DarchetypeVersion=RELEASE \
  -DinteractiveMode=false \
  -DgroupId=com.github.suzu_devworks \
  -DartifactId=examples-java

mvn archetype:generate \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false \
  -DgroupId=com.github.suzu_devworks \
  -Dpackage=com.github.suzu_devworks.examples \
  -DartifactId=examples-core \
  -DoutputDirectory=modules

```
