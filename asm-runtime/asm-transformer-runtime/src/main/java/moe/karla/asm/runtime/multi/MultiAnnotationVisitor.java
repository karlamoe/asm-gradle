package moe.karla.asm.runtime.multi;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Collection;
import java.util.function.Function;

public final class MultiAnnotationVisitor extends AnnotationVisitor {
    public static final Function<Collection<AnnotationVisitor>, AnnotationVisitor> CREATOR = MultiAnnotationVisitor::new;

    private final Collection<AnnotationVisitor> avs;

    public MultiAnnotationVisitor(Collection<AnnotationVisitor> avs) {
        super(Opcodes.ASM9);
        this.avs = avs;
    }

    @Override
    public void visit(String name, Object value) {
        for (AnnotationVisitor av : avs) {
            av.visit(name, value);
        }
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        for (AnnotationVisitor av : avs) {
            av.visitEnum(name, descriptor, value);
        }
    }

    @Override
    public void visitEnd() {
        for (AnnotationVisitor av : avs) {
            av.visitEnd();
        }
    }


    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        return MultiCreator.createMulti(
                avs,
                it -> it.visitAnnotation(name, descriptor),
                CREATOR
        );
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return MultiCreator.createMulti(
                avs,
                it -> it.visitArray(name),
                CREATOR
        );
    }
}
