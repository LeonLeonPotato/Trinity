package me.leon.trinity.utils.misc;

import me.leon.trinity.main.Trinity;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Method;

public class ReflectionUtils {
	public static Object readPrivateField(Object object, String name, String obfName) {
		Object ret = null;
		try {
			ret = FieldUtils.getField(object.getClass(), Trinity.isObfEnv() ? name : obfName, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static boolean invokeMethod(Object object, String name, String obfName, Object... params) {
		boolean done = false;
		try {
			Method method = object.getClass().getMethod(Trinity.isObfEnv() ? name : obfName, params.getClass());
			method.setAccessible(true);
			method.invoke(object, params);
			done = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return done;
	}
}
