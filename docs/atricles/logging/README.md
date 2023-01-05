# Java Logging

## Overview

Java のロギングはさまざまな社会状況により変わります。とくに[こんなのとか](https://www.ipa.go.jp/security/ciadr/vul/alert20211213.html)。

- [ひと昔前] Commons-logging + Log4j
- [ちょっと前] SLF4J + Logback
- [最近] ?????
- [これから] ?????

## References

- [Java Logging Frameworks: Summary and Best Practices](https://www.alibabacloud.com/blog/java-logging-frameworks-summary-and-best-practices_598223)
- [SLF4J、Logback、Log4J の関係を挙動とともに整理する](https://qiita.com/NagaokaKenichi/items/9febd2e559331152fcf8)
- [これから(Java9)のロガー](https://blog1.mammb.com/entry/2022/08/15/000000)
- [](https://blog.kengo-toda.jp/entry/2021/05/31/200807)

- <!-- ----- -->

## Logging ３要素

### Log Facade

アプリケーション上のインタフェースを統一して、実行時に好みのログフレームワークをバインドできる様にしたしくみ.

- [commons-logging](https://commons.apache.org/proper/commons-logging/)
- [SLF4J](https://www.slf4j.org/)

### Logging Framework (Implementations).

ログ実装とかバックエンドとかいうもの。

- [java.util.logging](https://docs.oracle.com/javase/jp/8/docs/api/java/util/logging/package-summary.html)
- [logback](https://logback.qos.ch/)
- [log4j2](https://logging.apache.org/log4j/2.x/)
- [JEP264: Platform Logging API(Java 9)](https://docs.oracle.com/javase/jp/9/docs/api/java/lang/System.Logger.html)

### Logging Adapter (Bridge)

各種 Facade や異なるログ実装との違いを吸収するものです。
修正できない jar が古いログ実装に依存している場合に使用します。

- [jcl-over-slf4j.xxx.jar]
  commons-logging - Jakarta Commons Logging => SLF4J
- [jul-to-slf4j.xxx.jar]
  java.util.logging => SLF4J
- [log4j-over-slf4j.xxx.jar]
- log4j => SLF4J

<!-- ----- -->
