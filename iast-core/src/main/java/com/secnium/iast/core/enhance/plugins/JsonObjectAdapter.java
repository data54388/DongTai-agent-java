package com.secnium.iast.core.enhance.plugins;

import com.secnium.iast.core.enhance.IastContext;
import com.secnium.iast.core.util.LogUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;

/**
 * @author WuHaoyuan
 * @since 2021-05-21 下午2:56
 */
public class JsonObjectAdapter extends AbstractClassVisitor {

    private final Logger logger = LogUtils.getLogger(getClass());

    public JsonObjectAdapter(ClassVisitor classVisitor, IastContext context) {
        super(classVisitor, context);
    }

    /*@Override
    public FieldVisitor visitField(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final Object value) {
        if (name.equals("map")) {
            cv.visitField(Opcodes.ACC_PRIVATE, "map", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            return null;
        }
        return cv.visitField(access, name, descriptor, signature, value);
    }*/

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name) && "()V".equals(desc)) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            transformed = true;
            return new JsonObjectAdviceAdapter(mv);
        }
        if (cv != null) {
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
        return null;
    }


    @Override
    public boolean hasTransformed() {
        return transformed;
    }

    private class JsonObjectAdviceAdapter extends MethodVisitor implements Opcodes {

        public JsonObjectAdviceAdapter(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
        }

        /*@Override
        public void visitCode() {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Enter constructor");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            super.visitCode();
        }*/

       /* @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "java/util/LinkedHashMap");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
                mv.visitFieldInsn(PUTFIELD, "com/secnium/iast/thirdparty/org/json", "map", "Ljava/util/Map;");
            }
            super.visitInsn(opcode);
        }*/

        @Override
        public void visitTypeInsn(final int opcode, final String type) {
            if (opcode == Opcodes.NEW && type.equals("java/util/HashMap")) {
                mv.visitTypeInsn(NEW, "java/util/LinkedHashMap");
            } else {
                super.visitTypeInsn(opcode, type);
            }
        }

        @Override
        public void visitMethodInsn(
                final int opcode,
                final String owner,
                final String name,
                final String descriptor,
                final boolean isInterface) {
            if (opcode == Opcodes.INVOKESPECIAL && owner.equals("java/util/HashMap") && name.equals("<init>")) {
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }
    }
}
