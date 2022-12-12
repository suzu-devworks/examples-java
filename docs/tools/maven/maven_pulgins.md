# Apache Maven Plugins

## maven-archetype-plugin

`archetype`とよばれる、Maven のテンプレートからプロジェクトを作成できます。

- https://maven.apache.org/archetype/maven-archetype-plugin/index.html

### プロジェクトを作成する

たいてい `maven-archetype-quickstart` でこと足りそう。

```sh
$ mvn archetype:generate \
    -DgroupId=com.github.suzu_devworks.examples_java \
    -DartifactId=examples-java \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DarchetypeVersion=1.4
```

## maven-help-plugin

プロジェクトの各種情報を出力できます。

- https://maven.apache.org/plugins/maven-help-plugin/index.html

### `settings.xml` のデフォルト設定を含めた有効値を出力

```sh
$ mvn help:effective-settings -Doutput=effective-settings.xml
```

```console
[INFO] Scanning for projects...
[INFO]
[INFO] --------------------< com.example:restful-endpoint >--------------------
[INFO] Building Thorntail Example 1.0.0-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] --- maven-help-plugin:3.2.0:effective-settings (default-cli) @ restful-endpoint ---
[INFO] Effective-settings written to: /home/akira/workspaces/repos/myApp/effective-settings.xml  # ← here!
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.825 s
[INFO] Finished at: 2020-08-14T17:11:11+09:00
[INFO] ------------------------------------------------------------------------
```

### `pom.xml` のデフォルト設定を含めた 有効値を出力

```sh
$ mvn help:effective-pom -Doutput=effective-pom.xml
```

```console
[INFO] Scanning for projects...
[INFO]
[INFO] --------------------< com.example:restful-endpoint >--------------------
[INFO] Building Thorntail Example 1.0.0-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] --- maven-help-plugin:3.2.0:effective-pom (default-cli) @ restful-endpoint ---
[INFO] Effective-POM written to: /home/akira/workspaces/repos/myApp/effective-pom.xml # ← here!
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.756 s
[INFO] Finished at: 2020-08-14T17:12:36+09:00
[INFO] ------------------------------------------------------------------------
```

## maven-dependency-plugin

プロジェクトの依存関係の情報を収集、リモートのリポジトリから指定された場所にコピーおよびまたはアンパックできます。

- https://maven.apache.org/plugins/maven-dependency-plugin/index.html

### プロジェクトの依存関係ツリー出力

```sh
$ mvn dependency:tree -DoutputFile=dependency.txt
```

```console
[INFO] Scanning for projects...
[INFO]
[INFO] --------------------< com.example:restful-endpoint >--------------------
[INFO] Building Thorntail Example 1.0.0-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ restful-endpoint ---
[INFO] Wrote dependency tree to: /home/akira/workspaces/repos/myApp/dependency.txt # ← here!
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.468 s
[INFO] Finished at: 2020-08-14T17:22:35+09:00
[INFO] ------------------------------------------------------------------------
```

`dependency.txt`:

```
com.example:restful-endpoint:war:1.0.0-SNAPSHOT
+- io.thorntail:jaxrs:jar:2.7.0.Final:compile
|  +- io.thorntail:container:jar:2.7.0.Final:compile
|  |  +- io.thorntail:spi:jar:2.7.0.Final:compile
|  |  |  \- org.jboss:jandex:jar:2.1.2.Final:compile
|  |  +- io.thorntail:bootstrap:jar:2.7.0.Final:compile
|  |  +- io.thorntail:config-api:jar:2.7.0:compile
|  |  |  +- io.thorntail:config-api-runtime:jar:2.7.0:compile
|  |  |  \- org.wildfly.common:wildfly-common:jar:1.5.1.Final:compile
|  |  \- io.thorntail.jdk-specific:thorntail-jdk-specific:jar:2:compile

```

### 依存ライブラリを特定のディレクトリにコピー

```sh
$ mvn dependency:copy-dependencies -DoutputDirectory=lib
```

```console
[INFO] Scanning for projects...
[INFO]
[INFO] --------------------< com.example:restful-endpoint >--------------------
[INFO] Building Thorntail Example 1.0.0-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] --- maven-dependency-plugin:2.8:copy-dependencies (default-cli) @ restful-endpoint ---
[INFO] Copying staxmapper-1.3.0.Final.jar to /home/akira/workspaces/repos/myApp/lib/staxmapper-1.3.0.Final.jar
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.606 s
[INFO] Finished at: 2020-08-14T17:26:39+09:00
[INFO] ------------------------------------------------------------------------
```

## References

- [よく使う Maven コマンド集](https://qiita.com/KevinFQ/items/e8363ad6123713815e68)
