package app.growbuddy.plants.model;

import org.immutables.annotate.InjectAnnotation;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@InjectAnnotation(type = TextScore.class, target = InjectAnnotation.Where.FIELD)
@Target({ElementType.METHOD})
public @interface InjectTextScore {
}
