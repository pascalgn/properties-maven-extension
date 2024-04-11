# properties-maven-extension

[![License](https://img.shields.io/github/license/pascalgn/properties-maven-extension.svg?style=flat-square)](LICENSE)

A Maven extension to introduce properties *before* the build is started.
This allows the usage of dynamic project versions like `1.2.3.${sha1}`.

## Example

For a simple example, consider the following POM file:

```xml
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
```

For a useful project version, the build would have to be invoked like `mvn clean install -Dsha1=abcd123`.

Using the Properties Maven Extension, this configuration can be set in the project directory itself,
by adding the following `.mvn/extensions.xml` file to the project:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>com.github.pascalgn</groupId>
        <artifactId>properties-maven-extension</artifactId>
        <version>0.4.0</version>
    </extension>
</extensions>
```

A build is now as easy as `mvn clean install` and the commit hash will be available as a property,
so that the final project version will be `1.2.3.abcd123` (depending on the current commit hash).

## Requirements

You will need at least Maven 3.5.0 for this to work. See
[the release notes](https://maven.apache.org/docs/3.5.0/release-notes.html) for more information.
You also need to run your Maven build with at least Java 8 (due to the [JGit](https://eclipse.org/jgit) dependency).

## Properties

The following properties are currently provided by the extension:

* `git.branch`, the current branch name (*master*, *feature/some-feature*, etc.)
* `git.build.datetime.simple`, build time in simple datetime format (yyyyMMdd_HHmmss)
* `git.commit.id`, the full SHA of the HEAD commit (*cbf1b9a1be984a9f61b79a05f23b19f66d533537*)
* `git.commit.id.abbrev`, the abbreviated SHA of the HEAD commit (*cbf1b9a*)
* `git.count`, the current number of commits reachable from HEAD
* `git.commit.color.value`, always the first six characters of the commit SHA (*cbf1b9*)
* `git.commit.color.name`, the name of the closest matching color (*Gossip*)
* `git.commit.color.lightness`, the lightness of the color, in the range 0 - 100 (inclusive)
* `git.commit.color.foreground`, a recommended foreground color, based on the lightness (*000000* or *ffffff*)
* `git.dir.git`, the absolute path of the repository (*/home/user/workspace/myproject/.git*)
* `git.dir.worktree`, the absolute path of the working tree (*/home/user/workspace/myproject*)
* `git.tag.last`, last tag from sorted tag list (*v1.2.3*)

If you need more properties, feel free to create an [issue](https://github.com/pascalgn/properties-maven-extension/issues)
or a [pull request](https://github.com/pascalgn/properties-maven-extension/pulls).

## Notes

* When you use placeholders in your version, be sure to also read the [Maven CI Friendly Versions](https://maven.apache.org/maven-ci-friendly.html) guide, especially the section about the [flatten-maven-plugin](http://www.mojohaus.org/flatten-maven-plugin)
* When building in Jenkins, you might run into the issue that the
  [branch name is not set due to detached HEAD](https://stackoverflow.com/questions/39297783/detached-head-w-jenkins-git-plugin-and-branch-specifier)
* The versioning scheme of this project follows [semantic versioning](http://semver.org/)

## Related projects

* There are already plugins providing properties, like [maven-git-commit-id-plugin](https://github.com/ktoso/maven-git-commit-id-plugin).
  However, the POM structure, including versions, is built by Maven before any plugins are executed, hence this extension.
* The [maven-git-versioning-extension](https://github.com/qoomon/maven-git-versioning-extension) is very similar,
  but it directly sets the project version, based on existing branches and tags, which makes it less flexible.

## License

The Properties Maven Extension is licensed under the Apache License, Version 2.0.

This software uses the Resene RGB values list, see [the license](src/main/resources/resene.md) for more details.
