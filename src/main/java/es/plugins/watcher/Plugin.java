package es.plugins.watcher;

import com.google.common.collect.Lists;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

public class Plugin extends org.elasticsearch.plugins.Plugin {

    private final static ESLogger LOGGER = Slf4jESLoggerFactory.getLogger(Plugin.class.getName());

    public Plugin() {
        LOGGER.debug("### es-plugin.watcher created");
    }

    @Override
    public String name() {
        return "es.plugins.watcher";
    }

    @Override
    public String description() {
        return "Watch Events for index, docs, etc.";
    }

    @Override
    public Collection<org.elasticsearch.common.inject.Module> nodeModules() {
        LOGGER.debug("nodeModules on loaded.");
        List<org.elasticsearch.common.inject.Module> modules = Lists.newArrayList();
        modules.add(createModule(Module.class));
        return modules;
    }

//    @Override
//    public Collection<org.elasticsearch.common.inject.Module> shardModules(Settings indexSettings) {
//        LOGGER.debug("shardModules on loaded.");
//        List<org.elasticsearch.common.inject.Module> modules = Lists.newArrayList();
//        modules.add(createModule(Module.class, indexSettings));
//        return modules;
//    }

    private static org.elasticsearch.common.inject.Module createModule(Class<? extends org.elasticsearch.common.inject.Module> moduleClass) {
        Constructor<? extends org.elasticsearch.common.inject.Module> constructor;
        try {
            constructor = moduleClass.getConstructor(Settings.class);
            try {
                return constructor.newInstance();
            } catch (Exception e) {
                throw new ElasticsearchException("Failed to create module [" + moduleClass + "]", e);
            }
        } catch (NoSuchMethodException e) {
            try {
                constructor = moduleClass.getConstructor();
                try {
                    return constructor.newInstance();
                } catch (Exception e1) {
                    throw new ElasticsearchException("Failed to create module [" + moduleClass + "]", e);
                }
            } catch (NoSuchMethodException e1) {
                throw new ElasticsearchException("No constructor for [" + moduleClass + "]");
            }
        }
    }

    private static org.elasticsearch.common.inject.Module createModule(Class<? extends org.elasticsearch.common.inject.Module> moduleClass, @Nullable Settings settings) {
        Constructor<? extends org.elasticsearch.common.inject.Module> constructor;
        try {
            constructor = moduleClass.getConstructor(Settings.class);
            try {
                return constructor.newInstance(settings);
            } catch (Exception e) {
                throw new ElasticsearchException("Failed to create module [" + moduleClass + "]", e);
            }
        } catch (NoSuchMethodException e) {
            try {
                constructor = moduleClass.getConstructor();
                try {
                    return constructor.newInstance();
                } catch (Exception e1) {
                    throw new ElasticsearchException("Failed to create module [" + moduleClass + "]", e);
                }
            } catch (NoSuchMethodException e1) {
                throw new ElasticsearchException("No constructor for [" + moduleClass + "]");
            }
        }
    }
}
