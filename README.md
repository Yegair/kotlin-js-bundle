# kotlin-js-bundle

[![Build Status](https://travis-ci.org/Yegair/kotlin-js-bundle.svg?branch=master)](https://travis-ci.org/Yegair/kotlin-js-bundle)

## Maven Plugin

```XML
<build>
  <plugins>
    <plugin>
      <groupId>io.yegair</groupId>
      <artifactId>kotlin-js-bundle-maven-plugin</artifactId>
      <version>${kotlin-js-bundle.version}</version>
    </plugin>
  </plugins>
</build>
```

### Using the latest SNAPSHOT version

Add the Sonatype OSSRH Snapshot repository to your `settings.xml`

```XML
<profiles>
  <profile>
    <id>ossrh-snapshots</id>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <repositories>
      <repository>
        <id>ossrh-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
          <enabled>false</enabled>
        </releases>
        <snapshots>
          <enabled>true</enabled>
        </snapshots>
      </repository>
    </repositories>
  </profile>
</profiles>
```

