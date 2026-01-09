package moe.karla.asm.runtime.multi;

import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Collection;
import java.util.function.Function;

public final class MultiModuleVisitor extends ModuleVisitor {
    public static final Function<Collection<ModuleVisitor>, ModuleVisitor> CREATOR = MultiModuleVisitor::new;

    private final Collection<ModuleVisitor> mvs;

    public MultiModuleVisitor(Collection<ModuleVisitor> mvs) {
        super(Opcodes.ASM9);
        this.mvs = mvs;
    }

    @Override
    public void visitMainClass(String mainClass) {
        for (ModuleVisitor mv : mvs) {
            mv.visitMainClass(mainClass);
        }
    }

    @Override
    public void visitPackage(String packaze) {
        for (ModuleVisitor mv : mvs) {
            mv.visitPackage(packaze);
        }
    }

    @Override
    public void visitRequire(String module, int access, String version) {
        for (ModuleVisitor mv : mvs) {
            mv.visitRequire(module, access, version);
        }
    }

    @Override
    public void visitExport(String packaze, int access, String... modules) {
        for (ModuleVisitor mv : mvs) {
            mv.visitExport(packaze, access, modules);
        }
    }

    @Override
    public void visitOpen(String packaze, int access, String... modules) {
        for (ModuleVisitor mv : mvs) {
            mv.visitOpen(packaze, access, modules);
        }
    }

    @Override
    public void visitUse(String service) {
        for (ModuleVisitor mv : mvs) {
            mv.visitUse(service);
        }
    }

    @Override
    public void visitProvide(String service, String... providers) {
        for (ModuleVisitor mv : mvs) {
            mv.visitProvide(service, providers);
        }
    }

    @Override
    public void visitEnd() {
        for (ModuleVisitor mv : mvs) {
            mv.visitEnd();
        }
    }
}
