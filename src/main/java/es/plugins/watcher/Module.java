package es.plugins.watcher;

import org.elasticsearch.common.inject.AbstractModule;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(Service.class).asEagerSingleton();
    }
}
