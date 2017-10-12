# Performance Tests Checker

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

There are multiple ways of using performance-tests-checker in your environment. For a detailed explanation, please refer to the [official FindBugs documentation](http://findbugs.sourceforge.net/AddingDetectors.txt). 

### Adding to Eclipse Plugin ###

It is possible to configure custom detectors via Eclipse workspace preferences.
Go to `Window->Preferences->Java->FindBugs->Misc. Settings->Custom Detectors`
and specify there the jar of the performance-tests-checker.

### Adding to FindBugs (untested) ###

FindBugs uses a plugin-based approach to adding detectors.
This makes it easy for users to add their own detectors alongside the ones that come built in.

Therefore, one could copy the jar file to the `plugins` directory of SpotBugs.


## Newly Implemented Rules ##

To be described...

## Authors

* [Diego Costa](https://github.com/DiegoEliasCosta)
* Philip Leitner
* Cor-Paul Benzemer
* Christoph Laaber 


## License

To be described...


