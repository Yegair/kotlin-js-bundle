# Guide

## Maven Plugin

The `kotlin-js-bundle-maven-plugin` extracts JS files from dependencies of the project and bundles them with the project JS files into a single JS file. The JS files are ordered using the project's dependency tree.

### Goal `bundle` 

Bundles dependencies and project JS files into a single file. If not otherwise configured the goal runs in phase `process-classes`.

#### Usage

```XML
<build>
  <!-- the directory where the bundle JS file will be created -->
  <outputDirectory>${project.build.directory}/js</outputDirectory>
  
  <plugins>
    <plugin>
      <groupId>io.yegair</groupId>
      <artifactId>kotlin-js-bundle-maven-plugin</artifactId>
      <version>${kotlin-js-bundle.version}</version>
      <executions>
        <execution>
          <id>bundle</id>
          <goals>
            <goal>bundle</goal>
          </goals>
        </execution>  
      </executions>
    </plugin>
  </plugins>
</build>
```

#### Configuration

| Option | Description | Default value |
| --- | --- | --- | 
| `skip` | Skips the execution | `false` |‚
| `extractDirectory`  | The directory where the dependencies are extracted to   | `${project.build.directory}/kotlin-js-bundle/dependencies` |
| `outputDirectory` | The directory where the main bundle will be placed | `${project.build.outputDirectory}` |
| `outputFilename` | Filename for the main bundle | `${project.artifactId}.bundle.js` |
| `editorFold` | Indicates whether `<editor-fold>` comments should be included in the bundle file. They make it possible to collapse regions of the bundle file in certain editors. | `false` |

### Goal `test-bundle`

Bundles test dependencies, project JS files and project test JS files into a single file. If not otherwise configured the goal runs in phase `process-test-classes`.

#### Usage

```XML
<build>
  <!-- the directory where the test bundle JS file will be created -->
  <testOutputDirectory>${project.build.directory}/test-js</testOutputDirectory>
  
  <plugins>
    <plugin>
      <groupId>io.yegair</groupId>
      <artifactId>kotlin-js-bundle-maven-plugin</artifactId>
      <version>${kotlin-js-bundle.version}</version>
      <executions>
        <execution>
          <id>test-bundle</id>
          <goals>
            <goal>test-bundle</goal>
          </goals>
        </execution>  
      </executions>
    </plugin>
  </plugins>
</build>
```

#### Configuration

| Option | Description | Default value |
| --- | --- | --- | 
| `skip` | Skips the execution | `false` |‚
| `extractDirectory`  | The directory where the dependencies are extracted to   | `${project.build.directory}/kotlin-js-bundle/test-dependencies` |
| `outputDirectory` | The directory where the test bundle will be placed | `${project.build.testOutputDirectory}` |
| `outputFilename` | Filename for the test bundle | `${project.artifactId}-tests.bundle.js` |
| `editorFold` | Indicates whether `<editor-fold>` comments should be included in the bundle file. They make it possible to collapse regions of the bundle file in certain editors. | `false` |