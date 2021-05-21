package com.secnium.iast.core.enhance.plugins;

import com.secnium.iast.core.util.LogUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;

/**
 * @author WuHaoyuan
 * @since 2021-05-21 下午2:56
 */
public class JsonObjectAdapter extends ClassVisitor {

    private final Logger logger = LogUtils.getLogger(getClass());

    public JsonObjectAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public FieldVisitor visitField(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final Object value) {
        if (name.equals("map")) {
            cv.visitField(Opcodes.ACC_PRIVATE, "map", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
        }
        return cv.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            return new JsonObjectAdviceAdapter(mv);
        }
        if (cv != null) {
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
        return null;
    }

    private class JsonObjectAdviceAdapter extends MethodVisitor implements Opcodes {

        public JsonObjectAdviceAdapter(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
        }

        @Override
        public void visitCode() {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Enter constructor");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "java/util/LinkedHashMap");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
                mv.visitFieldInsn(PUTFIELD, "org/json/JSONObject", "map", "Ljava/util/Map;");
            }
            super.visitInsn(opcode);
        }
    }
}
