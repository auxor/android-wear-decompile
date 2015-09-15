package dalvik.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface TestTarget {
    String conceptName() default "";

    Class<?>[] methodArgs() default {};

    String methodName() default "";
}
