package moe.karla.asm.launcher;

import lombok.val;
import moe.karla.asm.generator.GeneratorContext;
import moe.karla.asm.generator.SimpleClassGenerator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ProtoParser extends SimpleClassGenerator {
    private final String path;

    public ProtoParser(String path) {
        this.path = path;
    }

    private static String parseSignature(String signature) {
        return signature.isEmpty() ? null : signature;
    }

    private static String[] split(String input) {
        ArrayList<String> result = new ArrayList<>();
        int idx = 0;
        while (true) {
            int next = input.indexOf(',', idx);
            if (next == -1) {
                result.add(input.substring(idx));
                return result.toArray(new String[0]);
            }

            result.add(input.substring(idx, next));
            idx = next + 1;
        }
    }

    @Override
    public void generate(ClassVisitor cv, GeneratorContext context) throws Throwable {
        try (val reader = new BufferedReader(new FileReader(path))) {
            val firstLine = split(reader.readLine());

            cv.visit(
                    Integer.parseInt(firstLine[0]),
                    Integer.parseInt(firstLine[1]),
                    firstLine[2],
                    parseSignature(firstLine[4]),
                    firstLine[3],
                    Arrays.copyOfRange(firstLine, 5, firstLine.length)
            );

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                val data = split(nextLine);
                if (data[2].charAt(0) == '(') {
                    val mod = Integer.parseInt(data[0]);
                    val met = cv.visitMethod(
                            mod,
                            data[1],
                            data[2],
                            parseSignature(data[3]),
                            Arrays.copyOfRange(firstLine, 4, firstLine.length)
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
                            parseSignature(data[3]), null
                    );
                }
            }
        }
    }
}
