# properties-maven-extension

A Maven extension to introduce properties *before* the build is started.
This allows the usage of dynamic project versions like `1.2.3.${sha1}`.

## Example

For a simple example, consider the following POM file:

    <?xml version="1.0" encoding="UTF-8"?>
    <project>
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>com.example</groupId>
        <artifactId>example-artifact</artifactId>
        <version>1.2.3.${sha1}</version>
    
        <properties>
            <sha1>${git.commit.id.abbrev}</sha1>
        </properties>
    </project>

For a useful project version, the build must be invoked like `mvn clean install -Dsha1=abc123`.
Using the Properties Maven Extension, this configuration can be set in the project directory itself,
by adding the following `.mvn/extensions.xml` file to the project:

    <?xml version="1.0" encoding="UTF-8"?>
    <extensions>
        <extension>
            <groupId>com.github.pascalgn</groupId>
            <artifactId>properties-maven-extension</artifactId>
            <version>0.1.0</version>
        </extension>
    </extensions>

A build is now as easy as `mvn clean install` and the commit hash will be available as a property,
so that the final project version will be `1.2.3.abc123` (depending on the current commit hash).

## Requirements

You will need at least Maven 3.3.1 for this to work. See
[the release notes](https://maven.apache.org/docs/3.3.1/release-notes.html) for more information.

For *earlier* Maven versions, you need to manually download the JAR file
and place it in the `${MAVEN_HOME}/lib/ext` folder.

## Properties

The following properties are currently provided by the extension:

* `git.branch`, the current branch name (*master*, *feature/some-feature*, etc.)
* `git.commit.id`, the full SHA of the HEAD commit (*cbf1b9a1be984a9f61b79a05f23b19f66d533537*)
* `git.commit.id.abbrev`, the abbreviated SHA of the HEAD commit (*cbf1b9a*)

If you need more properties, feel free to create an [issue](https://github.com/pascalgn/properties-maven-extension/issues)
or a [pull request](https://github.com/pascalgn/properties-maven-extension/pulls).

## License

The Properties Maven Extension is licensed under the Apache License, Version 2.0
