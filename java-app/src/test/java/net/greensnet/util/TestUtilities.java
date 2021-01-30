/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtilities {

    public static void testIsUtilityClassAndWellDefined(final Class<?> c)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assertThat(Modifier.isFinal(c.getModifiers())).isTrue();
        assertThat(c.getDeclaredConstructors().length).isEqualTo(1);
        final Constructor<?> constructor = c.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            fail("constructor is not private");
        }
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
        for (final Method method : c.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(c)) {
                fail("there exists a non-static method:" + method);
            }
        }
    }

}