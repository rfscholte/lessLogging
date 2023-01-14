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
