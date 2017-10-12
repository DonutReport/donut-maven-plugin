![](http://donutreport.github.io/donut/img/Donut-05.png)

[![Build Status](https://travis-ci.org/DonutReport/donut-maven-plugin.svg?branch=master)](https://travis-ci.org/DonutReport/donut-maven-plugin) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/report.donut/donut-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/report.donut/donut-maven-plugin)

## donut-maven-plugin

This is a maven plugin for donut. More about donut [here](http://github.com/DonutReport/donut).

## usage

in your `pom.xml` add the plugin with the following configuration: 

```
<plugin>
    <groupId>report.donut</groupId>
    <artifactId>donut-maven-plugin</artifactId>
    <version>0.0.5</version>
    <executions>
        <execution>
            <id>execution</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <sourceDirectory>${project.build.directory}/cucumber-reports</sourceDirectory>
                <outputDirectory>${project.build.directory}/donut</outputDirectory>
                <timestamp>${maven.build.timestamp}</timestamp>
                <template>default</template>
                <projectName>${project.name}</projectName>
                <!-- optional -->
                <customAttributes>
                   <customAttribute>
                      <name>My Custom 1</name>
                      <value>custom123</value>
                   </customAttribute>
                   <customAttribute>
                      <name>App Name</name>
                      <value>${app.name}</value>
                   </customAttribute>
                </customAttributes>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### all configuration parameters

```
<sourceDirectory>
<outputDirectory>
<prefix>
<timestamp>
<template>
<countSkippedAsFailure>
<countPendingAsFailure>
<countUndefinedAsFailure>
<countMissingAsFailure>
<projectName>
<projectVersion>
<customAttributes>
```

default values:
* **sourceDirectory** : is mandatory and it should be the directory that hold the generated JSON files to be visualised.
* **outputDirectory** : by default a `donut` folder will be generated
* **prefix** : the generated file is `donut-report.html`, however you can specify prefix i.e. `myproject-`
* **timestamp** : refers to the start time of your execution. If not specified by the user reports will use `now`
* **template** : donut supports 2 themes, `default` and `light`. `default` is the default value.
* **countSkippedAsFailure**, **countPendingAsFailure**, **countUndefinedAsFailure**, **countMissingAsFailure**: are boolean values, and by default they are set to `false`. 
* **projectName** : is a string value
* **projectVersion** : is a string value
* **customAttributes** : is a map of name/value pairs

#### report timestamp

As it may take a while to execute your tests, Donut expects that you pass the start timestamp, otherwise will default it to `now` at the time of the report execution. The format should be `yyyy-MM-dd-HHmm`

Example: 

```
<properties>
   <maven.build.timestamp.format>yyyy-MM-dd-HHmm</maven.build.timestamp.format>
</properties>
```

### Contributing

To contribute:

* Create an integration test to demonstrate the behaviour under `src/it/`.  For example, to add support to default the report prefix to an empty string:
    * Create src/it/default-empty-prefix
    * Copy the contents of the src/it/default-empty-prefix directory and update the pom as appropriate to demonstrate the configuration.  Update the verify.groovy to implement the test for your feature.
    * Run `mvn clean install -Prun-its` to run the integration tests.

### License

This project is under an MIT license

Powered by: [MagenTys](https://magentys.io), [Mechanical Rock](https://www.mechanicalrock.io)