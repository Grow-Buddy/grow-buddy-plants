package app.growbuddy.plants.batch;

public class AbstractBatchComponentFactory {

    protected String getBatchComponentBaseName() {
        return getClass().getSimpleName().replace("Factory", "");
    }

}
