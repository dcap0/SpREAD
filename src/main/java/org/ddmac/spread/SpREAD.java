package org.ddmac.spread;

import org.ddmac.spread.enums.Serializer;

import java.lang.annotation.*;


/**
 * Spring Reactive Endpoint, Annotation Derived:
 * A class annotation to be used on an Interface that inherits JpaRepository.
 * Generates a basic Reactive Router and a handler
 * with a GET, POST, PUT, and DELETE function targeting the "id" field.
 *
 *  <pre>
 *      {@code
 *      @SpREAD(path="/example")
 *      public interface ExampleClass extends JpaRepository<?,?> { ... }
 *      }
 *  </pre>
 *
 * @author Dennis Capone
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented public @interface SpREAD {

    /**
     * Optional element used to define the Reactive REST API path.
     *
     * @return String
     */
    String path() default "/";

    /**
     * Optional element used to provide implemented serializer.
     *
     * @return Serializer
     */
    Serializer serializer() default Serializer.JACKSON;
}