package org.ddmac.spread.test;

import io.toolisticon.cute.Cute;
import io.toolisticon.cute.CuteApi;
import io.toolisticon.cute.JavaFileObjectUtils;
import org.ddmac.spread.SpREADProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProcessorTests {
    @BeforeAll
    static void setup(){
    }

    @Test
    void testSpreadProcessor(){
        Cute.blackBoxTest()
                .given()
                    .processor(SpREADProcessor.class)
                    .andSourceFiles("java/org/ddmac/spread/test/data/TestRepository.java")
                .whenCompiled()
                .thenExpectThat()
                    .compilationSucceeds()
                    .andThat()
                    .generatedSourceFile("org.ddmac.spread.test.data.spread")
                    .matches(
                        CuteApi.ExpectedFileObjectMatcherKind.BINARY,
                            JavaFileObjectUtils.readFromString("org.ddmac.spread.test.data.spread")
                    )
                .executeTest();
    }

}
