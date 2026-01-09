package moe.karla.asm.runtime.multi;

import moe.karla.asm.runtime.multi.*;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;

import java.lang.reflect.Modifier;

public class AssertAllAsmMethodOverrideTest {
    void check(Class<?> target, Class<?> superClass) throws Throwable {
        for (var method : superClass.getDeclaredMethods()) {
            if (method.getName().equals("getDelegate")) continue;

            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }

            target.getDeclaredMethod(method.getName(), method.getParameterTypes());
        }

        if (!Modifier.isFinal(target.getModifiers())) {
            throw new IllegalStateException(target.getName() + " is not final");
        }
    }

    @Test
    void classVisitor() throws Throwable {
        check(MultiClassVisitor.class, ClassVisitor.class);
    }

    @Test
    void fieldVisitor() throws Throwable {
        check(MultiFieldVisitor.class, FieldVisitor.class);
    }

    @Test
    void annoVisitor() throws Throwable {
        check(MultiAnnotationVisitor.class, AnnotationVisitor.class);
    }

    @Test
    void methodVisitor() throws Throwable {
        check(MultiMethodVisitor.class, MethodVisitor.class);
    }

    @Test
    void moduleVisitor() throws Throwable {
        check(MultiModuleVisitor.class, ModuleVisitor.class);
    }

    @Test
    void recordComponentVisitor() throws Throwable {
        check(MultiRecordComponentVisitor.class, RecordComponentVisitor.class);
    }
}
