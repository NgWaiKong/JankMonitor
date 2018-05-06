package ngwaikong.com.jankmonitor.toolbox.extra;

/**
 * Created by weijiangwu on 2018/4/30.
 * from
 * http://weishu.me/2017/11/23/dexposed-on-art/
 * in kotlin still not work , so keep the .java
 * do not delete or rename "traceBegin" or "traceEnd"
 */

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HookJava {

    private static final String TAG = "HookJava";
    private static onTraceHook sOnTraceHook;

    public static void setOnTraceHook(onTraceHook sOnTraceHook) {
        HookJava.sOnTraceHook = sOnTraceHook;
    }

    public static void hook(Method origin, Method replace) {
        Memory.memcpy(MethodInspect.getMethodAddress(origin), MethodInspect.getMethodAddress(replace),
                MethodInspect.getArtMethodSize());
        Log.i(TAG, "hook: MethodInspect.getMethodAddress(origin):" + MethodInspect.getMethodAddress(origin));
        Log.i(TAG, "hook: MethodInspect.getMethodAddress(replace):" + MethodInspect.getMethodAddress(replace));
        Log.i(TAG, "hook: MethodInspect.getArtMethodSize():" + MethodInspect.getArtMethodSize());
    }

    //do not delete or rename , this method is for the hook
    public static void traceBegin(long traceTag, String methodName) {
        sOnTraceHook.onTraceBegin(traceTag, methodName);
    }

    //do not delete or rename , this method is for the hook
    public static void traceEnd(long traceTag) {
        sOnTraceHook.onTraceEnd(traceTag);
    }

    public interface onTraceHook {
        void onTraceBegin(long traceTag, String methodName);

        void onTraceEnd(long traceTag);
    }

    private static class Reflection {
        static Object call(Class<?> clazz, String className, String methodName, Object receiver,
                           Class[] types, Object[] params) throws UnsupportedException {
            try {
                if (clazz == null) clazz = Class.forName(className);
                Method method = clazz.getDeclaredMethod(methodName, types);
                method.setAccessible(true);
                return method.invoke(receiver, params);
            } catch (Throwable throwable) {
                throw new UnsupportedException("reflection error:", throwable);
            }
        }

        static Object get(Class<?> clazz, String className, String fieldName, Object receiver) {
            try {
                if (clazz == null) clazz = Class.forName(className);
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(receiver);
            } catch (Throwable e) {
                throw new UnsupportedException("reflection error:", e);
            }
        }
    }

    public static class MethodInspect {

        static long sMethodSize = -1;

        static void ruler1() {
        }

        static void ruler2() {
        }

        static long getMethodAddress(Method method) {

            Object mirrorMethod = Reflection.get(Method.class.getSuperclass(), null, "artMethod", method);
            if (mirrorMethod.getClass().equals(Long.class)) {
                return (Long) mirrorMethod;
            }
            return Unsafe.getObjectAddress(mirrorMethod);
        }

        static long getArtMethodSize() {
            if (sMethodSize > 0) {
                return sMethodSize;
            }

            try {
                Method f1 = MethodInspect.class.getDeclaredMethod("ruler1");
                Method f2 = MethodInspect.class.getDeclaredMethod("ruler2");
                sMethodSize = getMethodAddress(f2) - getMethodAddress(f1);
                return sMethodSize;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static byte[] getMethodBytes(Method method) {
            if (method == null) {
                return null;
            }
            byte[] ret = new byte[(int) getArtMethodSize()];
            long baseAddr = getMethodAddress(method);
            for (int i = 0; i < ret.length; i++) {
                ret[i] = Memory.peekByte(baseAddr + i);
            }
            return ret;
        }
    }

    private static class Memory {

        // libcode.io.Memory#peekByte
        static byte peekByte(long address) {
            return (Byte) Reflection.call(null, "libcore.io.Memory", "peekByte", null, new Class[]{long.class}, new Object[]{address});
        }

        static void pokeByte(long address, byte value) {
            Reflection.call(null, "libcore.io.Memory", "pokeByte", null, new Class[]{long.class, byte.class}, new Object[]{address, value});
        }

        static void memcpy(long dst, long src, long length) {
            for (long i = 0; i < length; i++) {
                pokeByte(dst, peekByte(src));
                dst++;
                src++;
            }
        }
    }

    static class Unsafe {

        static final String UNSAFE_CLASS = "sun.misc.Unsafe";
        static Object THE_UNSAFE = Reflection.get(null, UNSAFE_CLASS, "THE_ONE", null);

        static long getObjectAddress(Object o) {
            Object[] objects = {o};
            Integer baseOffset = (Integer) Reflection.call(null, UNSAFE_CLASS,
                    "arrayBaseOffset", THE_UNSAFE, new Class[]{Class.class}, new Object[]{Object[].class});
            return ((Number) Reflection.call(null, UNSAFE_CLASS, "getInt", THE_UNSAFE,
                    new Class[]{Object.class, long.class}, new Object[]{objects, baseOffset.longValue()})).longValue();
        }
    }

    private static class UnsupportedException extends RuntimeException {
        UnsupportedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}