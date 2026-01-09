package moe.karla.asm.runtime.dumper;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

class DummyClassPrinterTest {
    @Test
    void testDump() throws Exception {
        new ClassReader("moe.karla.asm.runtime.multi.MultiClassVisitor")
                .accept(new TraceClassVisitor(
                        null,
                        new DummyClassPrinter(),
                        new PrintWriter(System.out)

                ), 0);
    }
}