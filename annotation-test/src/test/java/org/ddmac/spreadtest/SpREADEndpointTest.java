package org.ddmac.spreadtest;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Arrays;
import java.util.HashMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SpREADEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    TestEntity t1 = new TestEntity(null,"test",0,true);
    TestEntity t2 = new TestEntity(null,"test1",0,false);
    TestEntity t3 = new TestEntity(null,"test2",1,false);
    TestEntity expectedT1 = new TestEntity(1L,"test",0,true);
    TestEntity expectedT2 = new TestEntity(2L,"test1",0,false);
    TestEntity expectedT3 = new TestEntity(3L,"test2",1,false);
    TestEntity updatedT1 = new TestEntity(1L,"testupdated",0,false);

    @Test
    public void testPut(){
        webTestClient.put()
                .uri("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(t1))
                .exchange()
                .expectAll(
                    responseSpec -> {
                        responseSpec.expectStatus().is2xxSuccessful();
                        responseSpec.expectBody().json(
                                new Gson().toJson(expectedT1)
                        );
                    }
                );
    }

    @Test
    public void testGetOne(){
        populate(t1);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/test")
                        .queryParam("id",1)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectAll(
                        responseSpec -> {
                            responseSpec.expectStatus().is2xxSuccessful();
                            responseSpec.expectBody().json(
                                    new Gson().toJson(expectedT1)
                            );
                        }
                );
    }

    @Test
    public void testPost(){
        populate(t1);

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/test")
                        .queryParam("id",1)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updatedT1))
                .exchange()
                .expectAll(
                        responseSpec -> {
                            responseSpec.expectStatus().is2xxSuccessful();
                            responseSpec.expectBody().json(
                                    new Gson().toJson(updatedT1)
                            );
                        }
                );
    }

    @Test
    public void testDelete(){
        HashMap<String,Integer> expected = new HashMap<>();
        expected.put("Deleted",1);

        populate(t1);

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/test")
                        .queryParam("id",1)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectAll(
                        responseSpec -> {
                            responseSpec.expectStatus().is2xxSuccessful();
                            responseSpec.expectBody().json(new Gson().toJson(expected));
                        }
                );
    }

    @Test
    public void testGetAll(){
        populate(t1,t2,t3);
        webTestClient.get()
                .uri("/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectAll(
                        responseSpec -> {
                            responseSpec.expectStatus().is2xxSuccessful();
                            responseSpec.expectBodyList(TestEntity.class).value((list)-> {
                                assert(list.get(0).equals(expectedT1));
                                assert(list.get(1).equals(expectedT2));
                                assert(list.get(2).equals(expectedT3));
                            });
                        }
                );

    }

    private void populate(TestEntity... entities){
        Arrays.stream(entities).forEach((entity) -> {
            webTestClient.put().uri("/test").contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(entity)).exchange();
        });
    }

}
