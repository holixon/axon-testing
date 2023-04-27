# Axon Testing

[![stable](https://img.shields.io/badge/lifecycle-STABLE-green.svg)](https://github.com/holisticon#open-source-lifecycle)
[![Build Status](https://github.com/holixon/axon-testing/workflows/Development%20branches/badge.svg)](https://github.com/holixon/axon-testing/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.testing/axon-testing-jgiven-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.testing/axon-testing-jgiven-core)

This library is an opinionated collection of testing utilities used along and with Axon Framework.  

## Components

### Axon JGiven

We believe that scenario testing (BDD style) is useful for tests of systems built with Axon Framework. It starts with tests for 
single aggregates and sagas, for which Axon Framework provides Axon Test Fixtures. The test of more complex components and their 
interaction should follow the same pattern.

For both kind of tests, we propose to use a JVM Framework for BDD testing called JGiven.

### Axon Upcaster Testing

Writing event upcasters is sometimes difficult. Testing of them require even more effort. The library helps to create easy upcaster tests.

## Changelog

* Beginning with release `4.7.4.0` we changed to a semantic versioning model where the first 3 digits refer to the axon framework version this lib is supposed to work withm the last digit is the build version of this lib against the framework.
