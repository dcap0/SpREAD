package org.ddmac.spread;

import org.ddmac.spread.enums.Serializer;
import org.ddmac.spread.repositorydata.RepositoryData;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Class that generates the Reactive Router and Handler at compile time.
 *
 * @author Dennis Capone
 */
@SupportedAnnotationTypes("org.ddmac.spread.SpREAD")
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
                        RepositoryData data = repositoryDataFromElement(element);
                        generateRouter(data);
                        generateHandler(data);
                    }
            }
        });


        return true;
    }

    /**
     * Creates the router file.
     *
     * @param rd RepositoryData: Record containing metadata needed to generate the Router.
     */
    private void generateRouter(RepositoryData rd) {
        String spreadPackage = rd.interfacePackage() + PACKAGE_SUFFIX;
        String routerClassName = rd.entityName() + ROUTER_SUFFIX;
        String handlerClassName = rd.entityName() + HANDLER_SUFFIX;
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

        body.append("   private String path =\"").append(rd.reqPath()).append("\";\n\n");

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
body.append("BodyInserters  .fromValue(\"Invalid request\")\n")
body.append(");\n")
body.append("}\n")
body.append("return null;\n")
body.append("}\n")
    */

    /**
     * Creates the handler file.
     *
     * @param rd RepositoryData: Record containing metadata needed to generate the Router.
     */
    private void generateHandler(
            RepositoryData rd
    ) {
        String spreadPackage = rd.interfacePackage() + PACKAGE_SUFFIX;
        String handlerClassName = rd.entityName() + HANDLER_SUFFIX;

        StringBuilder body = new StringBuilder();
        body.append("package ").append(spreadPackage).append(";\n\n");

        body.append("import ").append(rd.interfacePackage()).append(".").append(rd.repoSimpleName()).append(";\n");
        body.append("import ").append(rd.interfacePackage()).append(".").append(rd.entityName()).append(";\n");
        addSerializerImports(body,rd.serializer());
        body.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        body.append("import org.springframework.stereotype.Component;\n");
        body.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        body.append("import org.springframework.http.MediaType;\n");
        body.append("import org.springframework.web.reactive.function.BodyInserters;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerRequest;\n");
        body.append("import org.springframework.web.reactive.function.server.ServerResponse;\n");
        body.append("import reactor.core.publisher.Mono;\n\n");


        body.append("import java.util.HashMap;\n");
        body.append("import java.util.Map;\n");
        body.append("import java.util.Objects;\n\n");

        body.append("@Component\n");
        body.append("@SuppressWarnings(\"unchecked\")\n");
        body.append("public class ").append(handlerClassName).append("{\n\n");

        body.append("   @Autowired\n");
        body.append("   ").append(rd.repoSimpleName()).append(" repo;\n\n");

        body
                .append("   public Mono<ServerResponse> getAll(ServerRequest serverRequest){\n")
                .append("       return ServerResponse\n")
                .append("           .ok()\n")
                .append("           .contentType(MediaType.APPLICATION_JSON)\n")
                .append("           .body(").append(String.format(getSerializerStatement(rd.serializer()), "repo.findAll()")).append(");\n")
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

    /**
     * Gets the super class of the annotated repository and gets the
     * Entity via TypeMirror.
     * @param element The annotated interface.
     * @return The fully qualified name of the entity.
     */
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


    /**
     * Confirms that the annotated interface directly inherits from JpaRepository.
     *
     * @param element The annotated interface.
     * @return boolean. Does it??
     */
    private boolean doesAnnotatedClassInheritJpaRepo(Element element) {

        List<? extends TypeMirror> mirrors = processingEnv.getTypeUtils().directSupertypes(element.asType());

        for(int i = 0; i < mirrors.size(); i++){
            if(mirrors.get(i).toString().contains(JPA_FQN)){
                return true;
            }
        }

        return false;

    }

    /**
     * Extracts metadata about the annotated repository Interface from
     * the Element class.
     *
     * @param element Interface's Element.
     * @return RepositoryData containing relevant information to generating Router/Handler.
     */
    private RepositoryData repositoryDataFromElement(Element element){
        return new RepositoryData(
                processingEnv
                        .getElementUtils()
                        .getPackageOf(element)
                        .getQualifiedName()
                        .toString(),
                element.getSimpleName().toString(),
                getRepoEntity(element),
                element.getAnnotation(SpREAD.class).path(),
                element.getAnnotation(SpREAD.class).serializer()
        );
    }

    /**
     * Checks the serializer the user provides, and returns their respective serialization.
     *
     * @param s Serializer provided by user.
     * @return String.
     */
    private String getSerializerStatement(Serializer s){
        switch(s){
            case GSON -> {
                return "BodyInserters.fromValue(new Gson().toJson(%s))";
            }
            case KOTLIN -> {
                return "BodyInserters.fromValue(Json.encodeToString(%s))";
            }
            case null, default -> {
                return "BodyInserters.fromValue(%s)";
            }
        }
    }

    /**
     * Adds the correct import statements depending on the user's serializer.
     *
     * @param sb StringBuilder that is building out the handler body.
     * @param serializer Serializer provided by user.
     */
    private void addSerializerImports(StringBuilder sb, Serializer serializer){
        switch (serializer){
            case GSON -> {
                 sb.append("import com.google.gson.Gson;\n");
            }
            case KOTLIN -> {
                sb.append("import kotlinx.serialization.*\n;");
                sb.append("import kotlinx.serialization.json.*\n;");
            }
            case null, default -> { //already handled by spring
            }
        }

    }
}