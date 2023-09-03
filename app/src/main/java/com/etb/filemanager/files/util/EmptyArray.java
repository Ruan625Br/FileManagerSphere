/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - EmptyArray.java
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util;



public final class EmptyArray {
    private EmptyArray() {}

    public static final boolean[] BOOLEAN = new boolean[0];
    public static final byte[] BYTE = new byte[0];
    public static final char[] CHAR = new char[0];
    public static final double[] DOUBLE = new double[0];
    public static final float[] FLOAT = new float[0];
    public static final int[] INT = new int[0];
    public static final long[] LONG = new long[0];

    public static final Class<?>[] CLASS = new Class[0];
    public static final Object[] OBJECT = new Object[0];
    public static final String[] STRING = new String[0];
    public static final Throwable[] THROWABLE = new Throwable[0];
    public static final StackTraceElement[] STACK_TRACE_ELEMENT = new StackTraceElement[0];
    public static final java.lang.reflect.Type[] TYPE = new java.lang.reflect.Type[0];
    @SuppressWarnings("rawtypes")
    public static final java.lang.reflect.TypeVariable[] TYPE_VARIABLE =
            new java.lang.reflect.TypeVariable[0];
}
