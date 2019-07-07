# SpotJMHBugs

[![Build Status](https://travis-ci.com/DiegoEliasCosta/spotjmhbugs.svg?branch=master)](https://travis-ci.com/DiegoEliasCosta/spotjmhbugs)

A SpotBugs plugin for static analysis on JMH performance tests.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

To build the performance-tests-checker plugin you just need Java and Maven.

```
Java 7 or higher
Maven 
```

### Building

You can use Maven to generate the jar file necessary for deployment.

First compile the source of the project  

```
mvn compile
```

And simply pack the compiled code into the jar format. This jar will then be used to integrate our rules to SpotBugs program. 

```
mvn package
```


## Deployment to SpotBugs

There are multiple ways of using SpotJMHBugs in your environment. For a detailed explanation, please refer to the [official FindBugs documentation](http://findbugs.sourceforge.net/AddingDetectors.txt). 

### Adding to Eclipse Plugin ###

It is possible to configure custom detectors via Eclipse workspace preferences.
Go to `Window->Preferences->Java->FindBugs->Misc. Settings->Custom Detectors`
and specify there the jar of the performance-tests-checker.

### Deploying with SpotBugs ###

SpotBugs uses a plugin-based approach to adding detectors.
This makes it easy for users to add their own detectors alongside the ones that come built in.

1. First download the latest SpotBugs binary release at the [SpotBugs official website](https://github.com/spotbugs/spotbugs/releases). The only dependencies required for SpotBugs is a Java version properly installed.

2. Extract the zip/rar SpotBugs and configure the environment variable `SPOTBUGS_HOME` to the extracted directory.

3. Copy the built (see Building instructions above) `spotJMHbugs.jar` to the `plugin` folder of the SpotBugs.

4. Execute the `$SPOTBUGS_HOME/lib/spotbugs.jar` with the `-textui` command line to get the list of all possible line commands of SpotBugs.
   
  1. To make sure that you have deployed a valid plugin jar, you can run the command `java -jar $SPOTBUGS_HOME/lib/spotbugs.jar -textui -showPlugins`. The performance-checker plugin should be listed in the Available plugins.  


## Bad JMH Practices ##

Our plugin detects five bad practices related to benchmark creation:

- Not using returned computation (RETU)
- Using accumulation to consume values inside a loop (LOOP)
- Using final primitive for benchmark inputs (FINAL)
- Running fixture methods for each benchmark method invocation (INVO)
- Configuring benchmarks with zero forks (FORK)

For more details on each of the above mentioned bad practices, please refer to our [TSE paper](https://www.researchgate.net/publication/333825812)

## Authors

* [Diego Costa](https://github.com/DiegoEliasCosta)
* [Philip Leitner](https://github.com/xLeitix)
* [Cor-Paul Bezemer](https://www.ece.ualberta.ca/~bezemer/)



