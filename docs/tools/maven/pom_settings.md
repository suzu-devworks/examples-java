# pom.xml settings

## Sets JDK version

```diff
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>

  <properties>
    <!-- Generic properties -->
+   <java.version>17</java.version>
+   <maven.compiler.source>${java.version}</maven.compiler.source>
+   <maven.compiler.target>${java.version}</maven.compiler.target>
  </properties>

</project>
```

## Sets source encoding

```diff
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>

  <properties>
+   <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
+   <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  </properties>

</project>
```

## Sets packaging

Jar or War

```diff
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>

<groupId>jp.kogenet.sample</groupId>
<artifactId>app</artifactId>
<version>1.0-SNAPSHOT</version>

- <packaging>jar</packaging>
+ <packaging>war</packaging>

</project>
```

## Skip Tests

```diff
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>

  <properties>
+    <maven.test.skip>false</maven.test.skip>
+    <skipTests>true</skipTests>
  </properties>

</project>
```

- `maven.test.skip = true`:  
   テストコードのコンパイルとテストの実行をスキップ

- `skipTests = true`:  
   テストコードのコンパイルは実行して、テストの実行のみスキップ

## リソースフォルダーで${version}を置き換える

maven resource plugin のフィルタリングを有効にすると、リソースフォルダー内で、properties で設定した ${x}を置き換えることができます。

- [maven-resources-plugin のフィルタリングを使って設定ファイル内で変数置換](https://qiita.com/kozy4324/items/9fa17a98203761012fd9)

```diff
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>

  <build>
+   <resources>
+     <resource>
+       <directory>src/main/resources</directory>
+       <filtering>true</filtering>
+     </resource>
+   </resources>
    ...
  </build>

</project>
```

## web.xml is missing and <failOnMissingWebXml> is set to true

最近の Web アプリケーションでは web.xml ファイルはオプションなのですが、Maven の Web アプリケーションは web.xml ファイルが必要であるとしています。

```diff
  <properties>
+   <failOnMissingWebXml>false</failOnMissingWebXml>
  </properties>
```
