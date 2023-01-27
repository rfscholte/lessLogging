# lessLogging

![crowded beach and empty beach](assets/images/beaches.png)

Compare these two pictures with the output of your build. Most builds I've seen look more like the crowded beach, but wouldn't it be nicer (and boring, I know) to have an empty beach where Waldo is in clear sight?

Checking every person on the the beach takes time. Similar: every logged line takes time. Logging is known to be a time consuming process, especially to the console.
Only by suppressing logging I've seen build times being reduced up to 15-20%, so it is absolutely worth it!
Other benefit is that you will have a much better overview of what the build is doing and warnings are easier to spot.

This git repository has the following structure

- [main branch][1]      : contains this documentation
- [issues branch][2]    : contains a lot of grouped examples that highlight a specific issue
- [solutions branch][3] : it contains the some examples as the issues branch, but where the logging issue is fixed
- [PRs][4]              : For every solution there's also a separate PR, where the solution can be explained more clearly, when necessary at codeline level.

Even though all examples have a `pom.xml` to run it with Maven, in general the issue is in the java code or properties, so Gradle users can also learn from this.

To run an example, go to its directory and run `mvn test` 

[1]: https://github.com/rfscholte/lessLogging
[2]: https://github.com/rfscholte/lessLogging/tree/issues
[3]: https://github.com/rfscholte/lessLogging/tree/solutions
[4]: https://github.com/rfscholte/lessLogging/pulls?q=is%3Apr+is%3Asolution

## Loggers

Logging is considered to be an important aspect of software development, as it allows developers and administrators to understand how the system is being used and identify any issues that may arise. 
It provides valuable information for debugging and troubleshooting, as well as for monitoring the system's performance and security. 
Logging can also be used to comply with regulatory requirements, such as those related to data privacy and security. 
Overall, logging plays a crucial role in maintaining the reliability, stability, and security of a system, and it is important to design and implement a robust logging strategy to meet the needs of the system and its users.

Your application logging is useful at runtime, most likely not during buildtime. While testing your code as part of the build, the code is in a runtime-state, however you shouldn't care about the logging at that time, as you're not testing the logging-framework, right?

In this paragraph I use SLF4J as the API to the actual logging framework, unless I'm using JDK specific output features. 
SLJF4J is currently one of the most used logging APIs and it will make the examples look similar, making it easier to understand the solution.

### System.out

This is one of the most used ways to quickly write something to the console. But by doing this, it can never be suppressed.
For that reason the basic rule here is: never commit/push code using System.out/System.err!
It is okay to do it during experimenting on your own machine, as long as it doesn't end up in the source repository.

For the main code it should be replaced with a logging framework, so we can control when to print these messages.
But for the main code it should be replaced with asserts for the following reason:
- consistency: the output should be handled the same by every developer. It is hard to know why a line was printed to the output: formatting? casing? proper language?
- continuity: the output should be tested every time, not only when the developer feels like it.

Issue
: https://github.com/rfscholte/lessLogging/tree/issues/logging_system-out

Solution 
: https://github.com/rfscholte/lessLogging/tree/solutions/logging_system-out

Pull Request
: https://github.com/rfscholte/lessLogging/pull/3

### Java Logging (JUL)

Website
: https://docs.oracle.com/en/java/javase/11/core/java-logging-overview.html

Default root logger level
: INFO

Since Java 1.4 the JDK has been enriched with platform's core logging facilities, located in the `java.util.logging` package.
There's is a [JUL to SLF4J bridge][6], but you shouldn't use is. Most likely you will loose the performance boost from suppressing logging by this single library. Hence the following statement on the SLF4j webpage: **j.u.l. to SLF4J translation can seriously increase the cost of disabled logging statements (60-fold or 6000%) and measurably impact the performance of enabled log statements (20% overall increase).** 

To configure JUL, you need provide one or more system properties, as described on the [LogManager JavaDoc][7]. 
In case of Maven, if you want to suppress logging during test you need to configure the maven-surefire-plugin by adding the [system properties][8].

Most likely the simplest option is to add `logging-test.properties` to `src/test/resources`

    .level=OFF

Issue
: https://github.com/rfscholte/lessLogging/tree/issues/logging_jul

Solution 
: https://github.com/rfscholte/lessLogging/tree/solutions/logging_jul

Pull Request
: https://github.com/rfscholte/lessLogging/pull/6

[6]: https://www.slf4j.org/legacy.html#jul-to-slf4j
[7]: https://docs.oracle.com/en/java/javase/17/docs/api/java.logging/java/util/logging/LogManager.html
[8]: https://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html#systemPropertyVariables

### Log4J

Website
: https://logging.apache.org/log4j/2.x

Default root logger level
: ERROR

Unlike logback, the Log4J team maintains their own SLF4J adapter, keep that in mind when adding the dependencies.
Also, the default logging level for the rootLogger is error, so the chance that you see any logging during is hopefully little.
Depending on your delivery strategy, you either provide the production logging configuration as part of your maincode (as done in the example), or you add/refer to it afterwards.
This logging can also be suppressed during the test phase of the build by adding `log4j2-test.xml` to `src/test/resources`.

    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration strict="true">
      <Loggers>
        <Root level="OFF"/>
      </Loggers>
    </Configuration>

Issue
: https://github.com/rfscholte/lessLogging/tree/issues/logging_log4j

Solution 
: https://github.com/rfscholte/lessLogging/tree/solutions/logging_log4j

Pull Request
: https://github.com/rfscholte/lessLogging/pull/5

### Logback

Website
: https://logback.qos.ch/

Default root logger level
: DEBUG

In case Logback is on the classpath, either as direct dependency or pulled in via a transitive dependency, it will log to the console.
By default the loglevel for the root logger is `debug`, so if there's no configuration, logback will log anyway.
Logback has a [strategy][5] how to configure it. It there's a `logback-test.xml`, it will used instead of the `logback.xml`, which is very useful in our case.

    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
    	<root level="OFF"/>
    </configuration>

Issue
: https://github.com/rfscholte/lessLogging/tree/issues/logging_logback

Solution 
: https://github.com/rfscholte/lessLogging/tree/solutions/logging_logback

Pull Request
: https://github.com/rfscholte/lessLogging/pull/4

[5]: https://logback.qos.ch/manual/configuration.html

## Spring-boot

At the moment Spring boot is one of the most popular frameworks. There are some cases where it logs too much by default, but here are some ways to reduce that.

In the first paragraph (Banner) I'll show you how to control the startup banner during test. 
For all other paragraphs I'll add an `application.properties` to `src/main/java` to represent a more realistic Spring boot application.
In this file I'll turn off the banner.
I wouldn't do this normally, but it is just an easy way to verify the setup.
This means that IF you do see the banner during tests, then there's a classloading issue.

### Banner

This is about the `@SpringBootTest`. Let's start by saying that this annotation is not meant for unittests, but for integration tests.
With this annotation, the complete springboot application will be started, most likely much more than you are going to test.
If your first goal is to speed up your build, consider rewriting the test, e.g. using `SpringExtension`. And yes, it will likely be more code, but it run faster, every time.

If you still want or need to use the `@SpringBootTest`, but want to get rid of the banner during test, there's only a small adjustment you need to do.
You might expect that is using the default or your specified logging framework, but it not. This banner is printed before the logging framework is initialized.
However, the Spring properties are already loaded, and there's one property to control this: `spring.main.banner-mode`.

A small note: in this case adding `application.properties` to `src/test/resources` works, because there's no such file under `src/main/resources`, so it will be the first and only `application.properties` on the classpath. Other spring-boot examples will show you how to solve it if `src/main/resources/application.properties` does exist.

Issue
: https://github.com/rfscholte/lessLogging/tree/issues/spring-boot_banner

Solution 
: https://github.com/rfscholte/lessLogging/tree/solutions/spring-boot_banner

Pull Request
: https://github.com/rfscholte/lessLogging/pull/1

### JPA

If you're using the @DataJpaTest it is likely you'll see output similar to this:

    Hibernate: drop table if exists lorem cascade
    Hibernate: drop sequence if exists lorem_seq
    Hibernate: create sequence lorem_seq start with 1 increment by 50
    Hibernate: create table lorem (id bigint not null, primary key (id))    

There might be a reason to log this in production, but for me there's no reason to do that during tests.
This output looks much different compared to your standard output of your logging framework. 
Trying to suppress it with your logging framework has no effect (check the `logback-test.xml` in the example).
Instead you need to inform your persistence framework to suppress these SQL statements.

The DataJpaTest has a attribute for it called `showSql`, which is set to `true` by default.
Changing its value to `false` will suppress the JPA logging.

Issue
: https://github.com/rfscholte/lessLogging/tree/issues/spring-boot_jpa

Solution 
: https://github.com/rfscholte/lessLogging/tree/solutions/spring-boot_jpa

Pull Request
: https://github.com/rfscholte/lessLogging/pull/2
