package com.secnium.iast.core.enhance.plugins;

import com.secnium.iast.core.enhance.IastContext;
import org.objectweb.asm.ClassVisitor;

/**
 * @author WuHaoyuan
 * @since 2021-05-21 下午2:50
 */
public class DispatchJsonObject implements DispatchPlugin {

    String matchclass = "org/json/JSONObject";

    @Override
    public ClassVisitor dispatch(ClassVisitor classVisitor, IastContext context) {
        if (context.getClassName().equals(isMatch())) {
            return new JsonObjectAdapter(classVisitor, context);
        }
        return classVisitor;
    }

    @Override
    public String isMatch() {
        return matchclass;
    }
}
