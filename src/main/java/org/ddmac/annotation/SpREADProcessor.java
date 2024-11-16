package org.ddmac.annotation;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("org.ddmac.annotation.SpREAD")
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_21)
public class SpREADProcessor extends AbstractProcessor {
    private final String ROUTER_SUFFIX = "SpREADRouterImpl";
    private final String HANDLER_SUFFIX = "SpREADReactiveEndpointHandlerImpl";
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<Element> elements = new HashSet<>();

        annotations.forEach((annotation) -> {
            elements.addAll(roundEnv.getElementsAnnotatedWith(annotation));
        });

        elements.forEach((cls) -> {
            if(cls.getKind().isClass()) {
                generateRouter(cls);
                generateHandler(cls);
            }
        });



        return true;
    }

    private void generateRouter(Element cls) {
        String packageQName = processingEnv.getElementUtils().getPackageOf(cls).getQualifiedName().toString();
        String routerClassName = cls.getSimpleName() + ROUTER_SUFFIX;
        String handlerClassName = cls.getSimpleName() + HANDLER_SUFFIX;
        String reqPath = cls.getAnnotation(SpREAD.class).path();
        StringBuilder body = new StringBuilder();
        body.append("package ").append(packageQName).append(".").append(handlerClassName).append(";\n\n"); //import full package of handler class
        body.append("import ").append(packageQName).append(".").append(cls.getSimpleName()).append(";\n");
        body.append("import org.springframework.context.annotation.Bean;\n");
        body.append("import org.springframework.web.reactive.function.server.RouterFunction;\n");
        body.append("import org.springframework.web.reactive.function.server.RouterFunctions;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerResponse;\n\n");

        body.append("public class ").append(routerClassName).append("{\n\n");

        body.append("   private String path =").append(reqPath).append(";\n\n");

        body.append("   @Bean\n");
        body.append("   RouterFunction<ServerResponse> routeBase(").append(handlerClassName).append(" handler").append("){\n");
        body.append("       return RouterFunctions\n");
        body.append("           .route(GET(this.path),handler::getAll);\n");
//        body.append(".route(GET(this.path).and(RequestPredicateUtils.idQueryParamPredicate()), getHandler()::getOneById)\n");
//        body.append(".andRoute(POST(this.path).and(RequestPredicateUtils.idQueryParamPredicate()) , getHandler()::postById)\n");
//        body.append(".andRoute(PUT(this.path), getHandler()::put)\n");
//        body.append(".andRoute(DELETE(this.path).and(RequestPredicateUtils.idQueryParamPredicate()), getHandler()::deleteById);\n");
        body.append("       }\n\n");
        body.append("}");

        try {
            Writer writer = processingEnv.getFiler()
                    .createSourceFile(packageQName + "." + routerClassName)
                    .openWriter();
            writer.write(body.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
body.append("Mono<ServerResponse> checkId(ServerRequest request){\n")
body.append("if(request.queryParam("id").isEmpty()){\n")
body.append("return ServerResponse\n")
body.append(".badRequest()\n")
body.append(".body(\n")
body.append("BodyInserters.fromValue(\"Invalid request\")\n")
body.append(");\n")
body.append("}\n")
body.append("return null;\n")
body.append("}\n")
    */

    private void generateHandler(Element cls) {
        String packageQName = processingEnv.getElementUtils().getPackageOf(cls).getQualifiedName().toString();
        String handlerClassName = cls.getSimpleName() + HANDLER_SUFFIX;

        StringBuilder body = new StringBuilder();
        body.append("package ").append(packageQName).append(";\n\n");

        body.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        body.append("import org.springframework.http.MediaType;\n");
        body.append("import org.springframework.web.reactive.function.BodyInserters;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerRequest;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerResponse;\n");
        body.append("import reactor.core.publisher.Mono;\n");

        body.append("import java.util.HashMap;\n");
        body.append("import java.util.Map;\n");
        body.append("import java.util.Objects;\n\n");

        body.append("@Supresswarning(\"unchecked\")\n");

        body.append("public class ").append(handlerClassName).append("{\n\n");

        body
                .append("   public Mono<ServerResponse> getAll(ServerRequest serverRequest){\n")
                .append("       return ServerResponse\n")
                .append("           .ok()\n")
                .append("           .contentType(MediaType.APPLICATION_JSON)\n")
                .append("           .body(BodyInserters.fromValue(\"{\"hello\";\"world\"}\"))")
                .append("   }");
//        body.append("Mono<ServerResponse> checkId(ServerRequest request){\n");
//        body.append("if(request.queryParam(\"id\").isEmpty()){\n");
//        body.append("return ServerResponse\n");
//        body.append(".badRequest()\n");
//        body.append(".body(\n");
//        body.append("BodyInserters.fromValue(\"Invalid request\")\n");
//        body.append(");\n");
//        body.append("}\n");
//        body.append("return null;\n");
//        body.append("}\n");

//        body.append("public Mono<ServerResponse> getAll(ServerRequest request){\n");
//        body.append("return ServerResponse\n");
//        body.append(".ok()\n");
//        body.append(".contentType(MediaType.APPLICATION_JSON)\n");
//        body.append(".body(BodyInserters.fromValue(\n");
//        body.append("getRepo().findAll()");

        body.append("}");

        try {
            Writer writer = processingEnv.getFiler()
                    .createSourceFile(packageQName + "." + handlerClassName)
                    .openWriter();
            writer.write(body.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}