package moe.karla.asm.launcher;

import lombok.val;
import moe.karla.asm.generator.GeneratorContext;
import moe.karla.asm.generator.SimpleClassGenerator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class ProtoParser extends SimpleClassGenerator {
    private final String path;

    public ProtoParser(String path) {
        this.path = path;
    }

    @Override
    public void generate(ClassVisitor cv, GeneratorContext context) throws Throwable {
        try (val reader = new BufferedReader(new FileReader(path))) {
            val firstLine = reader.readLine().split(",");

            cv.visit(
                    Integer.parseInt(firstLine[0]),
                    Integer.parseInt(firstLine[1]),
                    firstLine[2],
                    null,
                    firstLine[3],
                    Arrays.copyOfRange(firstLine, 4, firstLine.length)
            );

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                val data = nextLine.split(",");
                if (data[2].charAt(0) == '(') {
                    val mod = Integer.parseInt(data[0]);
                    val met = cv.visitMethod(
                            mod,
                            data[1],
                            data[2],
                            null, null
                    );
                    if ((mod & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) != 0) {
                        met.visitInsn(Opcodes.ACONST_NULL);
                        met.visitInsn(Opcodes.ATHROW);
                        met.visitMaxs(0, 0);
                        met.visitEnd();
                    }
                } else {
                    cv.visitField(
                            Integer.parseInt(data[0]),
                            data[1],
                            data[2],
                            null, null
                    );
                }
            }
        }
    }
}
