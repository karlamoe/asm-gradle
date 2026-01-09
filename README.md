# asm-gradle

Here is a Gradle plugin designed for generating classes that is hard to written in classic java.

This plugin will create a source set with name `asm`. You can write your classes generators at
`src/asm/java`.

Here is an example that generated an empty class:

```java
package test;

import moe.karla.asm.generator.ClassGenerator;
import moe.karla.asm.generator.GeneratorContext;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class TestGenerator extends ClassGenerator {
    @Override
    public void generate(GeneratorContext context) throws Throwable {
        context.addClass(this::generate);
    }

    public void generate(ClassVisitor cv) throws Throwable {
        cv.visit(Opcodes.V1_8,
                Opcodes.ACC_PUBLIC,
                "hello/HelloWorld",
                null,
                "java/lang/Object", null
        );

        generateConstructor(cv, false, null);
    }
}
```

Please note that the class name and package name are not important, and you do not need to perform any registration
operations.

The plugin will scan all classes and try to load it.

The following are the reminders for generators:

- Must be `public class`
- Must contain a **public no-arg** constructor
- Generators must extends `moe.karla.asm.generator.ClassGenerator`
- All classes will be loaded by `Class#forName(String)`
- Only classes from `src/asm` will be scanned. Classes from libraries will be ignored.
- At least Java 11 is required for running generators.         

# Applying

```kotlin

plugins {
    id("moe.karla.asm")
    java
}

repositories {
    mavenCentral()
}

dependencies {
    // Add dependency to asm generator
    asm("some.other.library.that.need:some.name:version")
}


// You can change the java toolchain for your main code.
// This will not have any effect on the asm source set.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

// Modify the toolchain used by asm
// By default, it is same as the java toolchain that
// used to boot Gradle.
asm {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

```
