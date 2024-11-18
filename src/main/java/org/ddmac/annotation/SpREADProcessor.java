package org.ddmac.annotation;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


/**
 * Class that generates the Reactive Router and Handler at compile time.
 *
 * @author Dennis Capone
 */
@SupportedAnnotationTypes("org.ddmac.annotation.SpREAD")
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_21)
public class SpREADProcessor extends AbstractProcessor {

    private final String JPA_FQN = "org.springframework.data.jpa.repository.JpaRepository";
    private final String ROUTER_SUFFIX = "SpREADRouterImpl";
    private final String HANDLER_SUFFIX = "SpREADHandlerImpl";
    private final String PACKAGE_SUFFIX = ".spread";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<Element> elements = new HashSet<>();
        annotations.forEach((annotation) -> {
            elements.addAll(roundEnv.getElementsAnnotatedWith(annotation));
        });

        elements.forEach((element) -> {
            if (element.getKind().isInterface()) {
                    if (doesAnnotatedClassInheritJpaRepo(element)) {
                        String clsPackage = processingEnv
                                .getElementUtils()
                                .getPackageOf(element)
                                .getQualifiedName()
                                .toString();
                        String repoSimpleName = element.getSimpleName().toString();
                        String entityName = getRepoEntity(element);
                        String reqPath = element.getAnnotation(SpREAD.class).path();
                        generateRouter(clsPackage, entityName, reqPath);
                        generateHandler(clsPackage, repoSimpleName, entityName);
                    }
            }
        });


        return true;
    }

    /**
     * Creates the router file.
     *
     * @param clsPackage The package of the class being processed.
     * @param entityName Name of the entity type parameter of the repository. Used in naming the generated class.
     * @param reqPath    Path that the endpoint will have.
     */
    private void generateRouter(String clsPackage, String entityName, String reqPath) {
        String spreadPackage = clsPackage + PACKAGE_SUFFIX;
        String routerClassName = entityName + ROUTER_SUFFIX;
        String handlerClassName = entityName + HANDLER_SUFFIX;
        StringBuilder body = new StringBuilder();
        body.append("package ").append(spreadPackage).append(";\n\n"); //import full package of handler class
        body.append("import ").append(spreadPackage).append(".").append(handlerClassName).append(";\n");
        body.append("import org.springframework.context.annotation.Bean;\n");
        body.append("import org.springframework.context.annotation.Configuration;\n");
        body.append("import org.springframework.web.reactive.function.server.RouterFunction;\n");
        body.append("import org.springframework.web.reactive.function.server.RouterFunctions;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerResponse;\n\n");

        body.append("import static org.springframework.web.reactive.function.server.RequestPredicates.GET;\n\n");

        body.append("@Configuration(proxyBeanMethods = false)\n");
        body.append("public class ").append(routerClassName).append("{\n\n");

        body.append("   private String path =\"").append(reqPath).append("\";\n\n");

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
                    .createSourceFile(spreadPackage + "." + routerClassName)
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

    /**
     * Creates the handler file.
     *
     * @param clsPackage     Package of the class being processed.
     * @param repoSimpleName Simple name of the repository.
     * @param entityName     Name of the entity type parameter of the repository. Used in naming generated class.
     */
    private void generateHandler(String clsPackage, String repoSimpleName, String entityName) {
        String spreadPackage = clsPackage + PACKAGE_SUFFIX;
        String handlerClassName = entityName + HANDLER_SUFFIX;

        StringBuilder body = new StringBuilder();
        body.append("package ").append(spreadPackage).append(";\n\n");

        body.append("import ").append(clsPackage).append(".").append(repoSimpleName).append(";\n");
        body.append("import ").append(clsPackage).append(".").append(entityName).append(";\n");
        body.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        body.append("import org.springframework.stereotype.Component;\n");
        body.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        body.append("import org.springframework.http.MediaType;\n");
        body.append("import org.springframework.web.reactive.function.BodyInserters;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerRequest;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerResponse;\n");
        body.append("import reactor.core.publisher.Mono;\n");

        body.append("import java.util.HashMap;\n");
        body.append("import java.util.Map;\n");
        body.append("import java.util.Objects;\n\n");

        body.append("@Component\n");
        body.append("@SuppressWarnings(\"unchecked\")\n");
        body.append("public class ").append(handlerClassName).append("{\n\n");

        body.append("   @Autowired\n");
        body.append("   ").append(repoSimpleName).append(" repo;\n\n");

        body
                .append("   public Mono<ServerResponse> getAll(ServerRequest serverRequest){\n")
                .append("       return ServerResponse\n")
                .append("           .ok()\n")
                .append("           .contentType(MediaType.APPLICATION_JSON)\n")
                .append("           .body(BodyInserters.fromValue(repo.findAll().toString()));\n")
                .append("   }\n\n");

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
                    .createSourceFile(spreadPackage + "." + handlerClassName)
                    .openWriter();
            writer.write(body.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getRepoEntity(Element element){
        String[] fqn = ((DeclaredType) processingEnv
                    .getTypeUtils()
                    .directSupertypes(element.asType())
                    .stream()
                    .filter(type -> type.toString().contains(JPA_FQN))
                    .findFirst()
                    .orElseThrow())
                .getTypeArguments()
                .getFirst()
                .toString().split("\\.");
        return fqn[fqn.length-1];
    }


    private boolean doesAnnotatedClassInheritJpaRepo(Element element) {

        List<? extends TypeMirror> mirrors = processingEnv.getTypeUtils().directSupertypes(element.asType());

        for(int i = 0; i < mirrors.size(); i++){
            if(mirrors.get(i).toString().contains(JPA_FQN)){
                return true;
            }
        }

        return false;

    }


}