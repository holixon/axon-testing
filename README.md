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

Writing event upcasters is sometimes difficult. Testing of them require even more effort. The library helps to create 
easy upcaster JUnit 5 tests.

Imagine you need to test an upcaster for the `fixture.bankaccount.event.AccountCreatedEvent` in an application using 
Jackson serializer. The old event revision was `12` and the new revision is `13`. There are only few steps needed to 
implement such a test.

Please put the following dependency on your project classpath:

```xml
<dependency>
  <groupId>io.holixon.axon.testing</groupId>
  <artifactId>axon-testing-upcaster-junit5</artifactId>
  <version>4.x.x.x</version>
  <scope>test</scope>
</dependency>
```

Now create a JUnit 5 test for your upcaster:

```java
// the package is important to put event data in correct folder inside the test resources
package io.holixon.axon.testing.examples.upcaster.junit5.java;

// intentionally left out some trivial imports 

import io.holixon.axon.testing.upcaster.MessageEncoding;
import io.holixon.axon.testing.upcaster.UpcasterTest;

import static io.holixon.axon.testing.upcaster.UpcasterTestSupport.*;

// the class name is important
public class AccountCreatedEventUpcastingJavaTest {

	// must be defined as a static field of type matching the MessageEncoding
	// the initialization is extracted to a separate method for simplicity
	private final static JacksonSerializer jacksonSerializer = createJacksonSerializer();

	// special annotation in use at least specifying the encoding type
	@UpcasterTest(messageEncoding = MessageEncoding.JACKSON)
	// important method name as the last folder
	public void upcasts_account_created_jackson(
			// will contain ordered sequence of source events, see explanation below
			List<IntermediateEventRepresentation> sourceEvents,
			// will contain ordered sequence of result events, see explanation below
			List<IntermediateEventRepresentation> expectedSerializedEvents) {

		AccountCreatedEventUpcaster upcaster = new AccountCreatedEventUpcaster();

		Stream<IntermediateEventRepresentation> upcastedStream = upcaster.upcast(sourceEvents.stream());
		List<AccountCreatedEvent> upcastedEvents = deserializeEvents(upcastedStream, jacksonSerializer).collect(
				Collectors.toList());

		// asserts
		List<AccountCreatedEvent> expectedEvents = deserializeEvents(expectedSerializedEvents, jacksonSerializer);
		assertThat(upcastedEvents).containExactlyElementsOf(expectedEvents);
	}
}
```
This looks like a miracle that the source events are passed in to the test, can be directly passed to the upcaster under the test
and the result can be asserted using the collection of expected events. Indeed, the library makes use of JUnit 5 parameterized test
functionality, by defining a custom argument provider reading events from the file system. 

We believe this is a good
practice for upcaster and upcaster chain testing, where several representations of different versions of the same event exist and must 
be managed and set-up for the test. In also helps in situations where comparison with the expected event as an object in Java is 
not feasible because only one version of a class may be available (probably the latest one).

Back to the test class implementation, please pay attention to the `static` `JacksonSerializer` defined in the test class and on 
the full-qualified name of the test class and the test method name.

The content of the `src/test/resources` folder is as following:

```
└── io
    └── holixon
        └── axon
            └── testing
                └── examples
                    └── upcaster
                        └── junit5
                            └── java
                                └── AccountCreatedEventUpcastingJavaTest
                                    └── upcasts_account_created_jackson
                                        ├── 1__fixture.bankaccount.event.AccountCreatedEvent__12.json
                                        ├── 2__fixture.bankaccount.event.AccountCreatedEvent__12.json
                                        ├── 1__fixture.bankaccount.event.AccountCreatedEvent__13__result.json
                                        └── 2__fixture.bankaccount.event.AccountCreatedEvent__13__result.json
```

The full-qualified class name and the test method name are mapped to the directory structure. Inside the test data directory,
there are four files. Those two of them without the `__result` suffix are read in and considered to be JSON representations
of source events. The source events are passed to the test method annotated by the `@UpcasterTest` as a first parameter `sourceEvents`. 
The other two with the `__result` suffix are considered to be JSON representations of the expected events, received after the 
upcaster run. Those will be passed to the test method as the second parameter `expectedSerializedEvents`. 

The ordering of the files (in each group) is determined based on the first numbers before the first `__` separator. The next part of 
the file name is considered as the type name of the stored event. Finally, the last number is representing the revision of the event.

You may want to use only parts of the functionality above. A good starting point for this are our examples. Please consider to look at the 
`io.holixon.axon.testing.upcaster.UpcasterTestSupport` class for helpful methods. In addition, you might want to use the `@UpcasterTest`
functionality in a slightly different way. The method signature of the test method may have one or two parameters (expected events must not be read 
from the filesystem) of different types: `List<IntermediateEventRepresentation>` to get intermediate representation, `List<String>` to get
the file content of the read-in files or just `List<File>` to get the files matching criteria. There are some additional options on
the `@UpcasterTest` annotation to tune the behaviour of the parameterized test argument provider. You can specify different strategy to 
resolve the payload type and revision and a different content retrieval strategy.

## Changelog

* Beginning with release `4.7.4.0` we changed to a semantic versioning model where the first 3 digits refer to the axon framework version this lib is supposed to work withm the last digit is the build version of this lib against the framework.


## License

This library is developed under

[![Apache 2.0 License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](/LICENSE)

## Sponsors and Customers

[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-red.svg)](https://holisticon.de/)

