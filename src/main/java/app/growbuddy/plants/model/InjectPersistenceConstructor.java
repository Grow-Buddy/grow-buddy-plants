package app.growbuddy.plants.model;

import org.immutables.annotate.InjectAnnotation;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.TextIndexed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@InjectAnnotation(type = PersistenceConstructor.class, target = InjectAnnotation.Where.CONSTRUCTOR)
@Target({ElementType.TYPE})
public @interface InjectPersistenceConstructor {
}
