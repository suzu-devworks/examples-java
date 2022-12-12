# pom.xml

POM stands for `Project Object Model`.

## Artifact Version

### DefaultArtifactVersion

基本的には次のように指定します。

```
<MajorVersion [> . <MinorVersion [> . <IncrementalVersion ] ] [> - <BuildNumber | Qualifier ]>
```

このへんは[セマンティックバージョン](https://semver.org/lang/ja/)に従うのがオススメとのこと。

ここでの注意事項は、

> 「どこの MajorVersion、MinorVersion の、IncrementalVersion と BuildNumber をはすべての数値であり、修飾子は文字列です。バージョン番号がこの形式と一致しない場合は、バージョン番号全体が修飾子として扱われます。」

つまり、1.2.9.1 > 1.2.10.1 になるようです。  
文字だからね。  
でも Maven 3 で改善されたとかされないとか...

### Qualifiers

修飾子は半角ダッシュを使ってバージョン番号の後ろに付記します。 大文字でも小文字でも構いませんが、慣習として大文字を使用することが多いようです。

| #   | Qualifier      | Examples                        | Description                                     |
| --- | -------------- | ------------------------------- | ----------------------------------------------- |
| 1   | alpha or a     | 1.2.3-alpha1, 1.2.3-alpha2, ... | アルファ版                                      |
| 2   | beta or b      | 1.2.3-beta1, 1.2.3-beta2, ...   | ベータ版                                        |
| 3   | milestone or m | 1.2.3-m1, 1.2.3-m2, ...         | マイルストーン版                                |
| 4   | rc or cr       | 1.2.3-rc1, 1.2.3-rc2, ...       | リリース候補版                                  |
| 5   | snapshot       | 1.2.3-SNAPSHOT                  | 安定版ではないことを使用者と Maven へ伝えます。 |
| 6   | ga or final    | 1.2.3                           | 修飾子なしと同様                                |
| 7   | sp             | 1.2.3-sp1, 1.2.3-sp2, ...       | バグ修正版                                      |

順番的には次のようになるようです。

```
1.0-beta1-SNAPSHOT
1.0-beta1
1.0-beta2-SNAPSHOT
1.0-rc1-SNAPSHOT
1.0-rc1
1.0-SNAPSHOT
1.0 と 1（これらは等しい）
1.0-sp
1-whatever
1.0.1
```

これは、次のようなコードで確認できるらしいです。

`Compare.java`:

```java
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.Assert;
import org.junit.Test;

Assert.assertTrue(new ComparableVersion("1.0-SNAPSHOT").compareTo(new ComparableVersion("1.0")) < 0);

```

## Super pom

すべてのプロジェクトで適用されるデフォルトの設定が記述された POM。

- https://maven.apache.org/pom.html#The_Super_POM
