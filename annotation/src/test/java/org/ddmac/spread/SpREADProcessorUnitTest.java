package org.ddmac.spread;


import org.ddmac.spread.enums.Serializer;
import org.ddmac.spread.repositorydata.RepositoryData;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Element;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpREADProcessorUnitTest {
    SpREADProcessor processor = new SpREADProcessor();
    RepositoryData repositoryData = new RepositoryData(
            "com.ddmac.test",
            "TestRepository",
            "TestEntity",
            "/test",
            Serializer.GSON
    );

    @Test
    void testGetPredicates() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = SpREADProcessor.class.getDeclaredMethod("getPredicates");
        m.setAccessible(true);
        String result = (String) m.invoke(processor);
        assert(result.contains("validId()"));
        assert(result.contains("noQueryParam()"));
    }

    @Test
    void testGetGSONSerializerImports() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Method m = SpREADProcessor.class.getDeclaredMethod(
                "addSerializerImports",
                StringBuilder.class,
                Serializer.class
        );
        m.setAccessible(true);
        m.invoke(processor,sb,Serializer.GSON);
        assert(sb.toString().contains("com.google.gson.Gson"));
    }


    @Test
    void testGetKotlinSerializerImports() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Method m = SpREADProcessor.class.getDeclaredMethod(
                "addSerializerImports",
                StringBuilder.class,
                Serializer.class
        );
        m.setAccessible(true);
        m.invoke(processor,sb,Serializer.KOTLIN);
        assert(sb.toString().contains("kotlinx.serialization"));
    }

    @Test
    void testGetJacksonSerializerImports() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Method m = SpREADProcessor.class.getDeclaredMethod(
                "addSerializerImports",
                StringBuilder.class,
                Serializer.class
        );
        m.setAccessible(true);
        m.invoke(processor,sb,Serializer.JACKSON);
        assert(sb.toString().isEmpty());
    }

    @Test
    void testGetGsonSerializerStatement() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = SpREADProcessor.class.getDeclaredMethod("getSerializerStatement", Serializer.class);
        m.setAccessible(true);
        String result = (String) m.invoke(processor,Serializer.GSON);
        assert(result.contains("Gson().toJson"));
    }

    @Test
    void testGetKotlinSerializerStatement() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = SpREADProcessor.class.getDeclaredMethod("getSerializerStatement", Serializer.class);
        m.setAccessible(true);
        String result = (String) m.invoke(processor,Serializer.KOTLIN);
        assert(result.contains("Json.encodeToString"));
    }

    @Test
    void testGetJacksonSerializerStatement() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = SpREADProcessor.class.getDeclaredMethod("getSerializerStatement", Serializer.class);
        m.setAccessible(true);
        String result = (String) m.invoke(processor,Serializer.JACKSON);
        assert(result.contains("BodyInserters.fromValue(%s)"));
    }

}
