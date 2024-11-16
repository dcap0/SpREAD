package org.ddmac.annotation;

import java.lang.annotation.*;


/**
 * Spring Reactive Endpoint, Annotation Derived
 * A class annotation to be used on an @Entity class.
 * Generates a basic Reactive Router and a handler
 * with a GET, POST, PUT, and DELETE function targeting the "id" field.
 *
 *  Parameters:
 *  - path: Sets the path for the endpoint.
 *
 *  <pre>
 *      {@code
 *      @SpREAD(path="/example")
 *      public class ExampleClass { ... }
 *      }
 *  </pre>
 *
 * @author Dennis Capone
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented public @interface SpREAD {
    String path() default "/";
}