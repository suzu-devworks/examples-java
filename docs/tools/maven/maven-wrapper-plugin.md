# Apache Maven Wrapper Plugin

## maven-wrapper-plugin

Maven Wrapper(mvnw) というコマンド自体をプロジェクトに含めるプラグインです。Gradle Wrapper に触発されたそうです。
Maven をローカルインストールしなくても使えるようにするためのプラグインです。

- https://maven.apache.org/wrapper/maven-wrapper-plugin/index.html

### 既存の Maven プロジェクトへの組み込み

```sh
$ mvn wrapper:wrapper
```

```console
[INFO] Scanning for projects...
[INFO]
[INFO] --------< com.github.suzu_devworks.examples_java:examples-java >--------
[INFO] Building examples-java 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-wrapper-plugin:3.1.1:wrapper (default-cli) @ examples-java ---
[INFO] Unpacked bin type wrapper distribution org.apache.maven.wrapper:maven-wrapper-distribution:zip:bin:3.1.1
[INFO] Configuring .mvn/wrapper/maven-wrapper.properties to use Maven 3.8.6 and download from https://repo.maven.apache.org/maven2
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.840 s
[INFO] Finished at: 2022-12-11T13:08:29Z
[INFO] ------------------------------------------------------------------------
```

出来上がったファイルツリー:

```console
$ tree -a
.
├── .mvn
│   └── wrapper
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── github
    │               └── suzu_devworks
    │                   └── examples_java
    │                       └── App.java
    └── test
        └── java
            └── com
                └── github
                    └── suzu_devworks
                        └── examples_java
                            └── AppTest.java
```

### wrapper をつかう

```sh
$ ./mvnw --version

$ ./mvnw clean

$ ./mvnw compile
```
