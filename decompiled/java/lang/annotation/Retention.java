package java.lang.annotation;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retention {
    RetentionPolicy value();
}
