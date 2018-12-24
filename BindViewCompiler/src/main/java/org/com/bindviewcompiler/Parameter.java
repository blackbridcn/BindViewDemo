package org.com.bindviewcompiler;

import com.squareup.javapoet.TypeName;

/**
 * Represents a parameter type and its position in the listener method.
 * 监听事件函数参数
 */
final class Parameter {
    static final Parameter[] NONE = new Parameter[0];
    //参数 position
    private final int listenerPosition;
    //参数类型
    private final TypeName type;

    Parameter(int listenerPosition, TypeName type) {
        this.listenerPosition = listenerPosition;
        this.type = type;
    }

    int getListenerPosition() {
        return listenerPosition;
    }

    TypeName getType() {
        return type;
    }

    public boolean requiresCast(String toType) {
        return !type.toString().equals(toType);
    }
}
