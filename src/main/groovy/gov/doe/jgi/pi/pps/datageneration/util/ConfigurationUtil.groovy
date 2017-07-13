package gov.doe.jgi.pi.pps.datageneration.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by dscott on 7/7/2017.
 */
class ConfigurationUtil {

    static Logger logger = LoggerFactory.getLogger(ConfigurationUtil.class.name)

    private static OnDemandCacheMapped<String,ConfigObject> cachedConfig = new OnDemandCacheMapped<>()
    private static String configurationFileName = 'config/DataGenerationConfig.groovy'

    private static URL getConfigUrl() {
        String configPath = Thread.currentThread().contextClassLoader.getResource(configurationFileName).file
        if (!configPath ) {
            throw new RuntimeException('failed to load Config.groovy')
        }
        URL url = new File(configPath).toURI().toURL()
        if (!url) {
            throw new RuntimeException('failed to load Config.groovy')
        }
        return url
    }

    static ConfigObject loadConfig(String environment) {
        logger.info "loading config file for env [${environment}]"
        return cachedConfig.fetch(environment) {
            new ConfigSlurper(environment).parse(configUrl)
        }
    }
}
