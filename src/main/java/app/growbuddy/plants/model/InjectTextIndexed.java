package app.growbuddy.plants.model;

import org.immutables.annotate.InjectAnnotation;
import org.springframework.data.mongodb.core.index.TextIndexed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@InjectAnnotation(type = TextIndexed.class, target = InjectAnnotation.Where.FIELD)
@Target({ElementType.METHOD})
public @interface InjectTextIndexed {
}
