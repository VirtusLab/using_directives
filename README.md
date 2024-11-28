# Using directives

Library for processing meta-information written in using-directives syntax.

### Generating code coverage report

Run:

```shell
./gradlew jacocoTestReport
```

Then, the coverage report will appear in the output directory, inside the
`coverage_report` directory.

### Building the project

The project currently needs JAVA_HOME to be set to JDK 11. After that you can
build the project with:

```bash
./gradlew build
```

Run tests with:

```bash
./gradlew test
```

Apply formatting fixes with:

```bash
./gradlew :spotlessApply
```
