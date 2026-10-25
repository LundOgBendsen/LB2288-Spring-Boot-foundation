# Person-Service-Starter

## Exercises

In these exercises you build a Spring Boot starter for the person-service library.

The library is already created for you. You will:

1. Install `person-service-lib` into your local Maven repository.
2. Create a **new empty Java Maven project** that becomes the starter.
3. Add dependencies, a configuration class, and auto-configuration.
4. Install the starter and try it in a small Spring Boot application.

If you get stuck, see [SOLUTIONS.md](SOLUTIONS.md).

Maven terms used below:

- **Coordinates** (`groupId`, `artifactId`, `version`) uniquely identify a library, like a name and version on a package.
- **`mvn install`** compiles the project and copies the result into your **local Maven repository** (`~/.m2/repository` on your machine). Other projects on the same computer can then depend on it.

---

### Exercise 1: Build and install person-service-lib

Maven can only use `person-service-lib` after it is installed locally.

1. Open the [person-service-lib project](../../person-service-lib).
2. In a terminal at the **course repository root**, run:

```text
cd person-service-lib
mvn install
```

You should see `BUILD SUCCESS`. After that, the library is available as:

```xml
<groupId>dk.lundogbendsen</groupId>
<artifactId>person-service-lib</artifactId>
<version>1.0</version>
```

---

### Exercise 2: Create a Java Maven project

This step creates the **starter project itself**. It must be a **Java project based on Maven** (not Gradle, not a Spring Initializr app yet).

1. In your IDE, create a **new Java Maven project**.
2. Use **JDK 25**.
3. Set the Maven coordinates to match the later exercises:

   - `groupId`: `org.example`
   - `artifactId`: `person-service-starter`
   - `version`: `1.0-SNAPSHOT`

You now have an empty Maven project: a `pom.xml`, no Java sources, no tests, no extra dependencies. That is expected.

---

### Exercise 3: Add dependencies

Open `pom.xml` and add **two** dependencies.

**1. `spring-boot-starter`**

This brings in Spring and the `@Conditional…` annotations used later.

Hint: copy the `spring-boot-starter` dependency from another Spring Boot module in this repository. Use version `4.1.2` if you type it by hand.

**2. `person-service-lib`**

```xml
<dependency>
    <groupId>dk.lundogbendsen</groupId>
    <artifactId>person-service-lib</artifactId>
    <version>1.0</version>
    <!-- optional: true means this library is *not* passed on to applications
         that only depend on the starter. They must add person-service-lib
         themselves. That is normal for Spring Boot starters: the starter
         wires beans; the application chooses the library version. -->
    <optional>true</optional>
</dependency>
```

Without `<optional>true</optional>`, every project that depends on the starter would also get `person-service-lib` automatically. Making it optional keeps the starter as “wiring only”.

---

### Exercise 4: Create a configuration class that exposes PersonService

Create a Spring configuration class that defines a `PersonService` bean.

1. Create package `org.example.personservicestarter`.
2. Create class `PersonServiceConfiguration`.
3. Annotate the class with `@Configuration`.
4. Add a method annotated with `@Bean` that returns `new PersonService()`.

---

### Exercise 5: Make bean creation conditional

A well-behaved starter only creates a bean when it is needed, and it does not override a bean the application already defined.

On `PersonServiceConfiguration`:

- Class: `@ConditionalOnClass(PersonService.class)` — only if `PersonService` is on the classpath.
- Bean method: `@ConditionalOnMissingBean(PersonService.class)` — only if the application did not already define a `PersonService` bean.

---

### Exercise 6: Register the auto-configuration

Spring Boot 4 reads a file named:

`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

1. Create that folder and file (the file name is long and must match exactly).
2. Ensure that the folder structure is preserved!!!
3. Put this single line in the file:

```text
org.example.personservicestarter.PersonServiceConfiguration
```

That is the fully qualified class name of your configuration class.

---

### Exercise 7: Build and install the starter

Pick **one** of these two options.

**Option 1 — from course repository root**

```text
cd PATH_TO_YOUR_PROJECT
mvn install
```

**Option 2 — if you are already in your project**

```text
mvn install
```

You should see `BUILD SUCCESS`. After that, other projects can depend on:

```xml
<groupId>org.example</groupId>
<artifactId>person-service-starter</artifactId>
<version>1.0-SNAPSHOT</version>
```

These coordinates are for the **starter**, not for `person-service-lib`.

---

### Exercise 8: Try the starter in a new Spring Boot project

1. Create a **new** Spring Boot application (Initializr / “naked” Boot project is fine).
2. In that application’s `pom.xml`, add **both**:
   - the starter coordinates from exercise 7
   - [person-service-lib](../../person-service-lib) (`dk.lundogbendsen:person-service-lib:1.0`)

   You need both because the starter marked the library as **optional**.
3. In `main`, get the `PersonService` bean from the application context and call `list()` (or similar) to confirm it works.
