package uptc.edu.co.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppConfig {
    private static final Logger LOGGER = LogManager.getLogger(AppConfig.class);

    private final Properties properties;
    private final File externalConfigDir;

    public AppConfig() {
        this.properties = new Properties();
        loadDefaultProperties();

        String configuredDir = System.getProperty("app.config.dir");
        if (configuredDir == null || configuredDir.trim().isEmpty()) {
            configuredDir = System.getenv("APP_CONFIG_DIR");
        }
        if (configuredDir == null || configuredDir.trim().isEmpty()) {
            configuredDir = "config";
        }

        this.externalConfigDir = new File(configuredDir.trim());
        loadExternalProperties();
    }

    private void loadDefaultProperties() {
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream("app-default.properties");
            if (input != null) {
                properties.load(input);
                LOGGER.info("Configuracion por defecto cargada desde JAR.");
            } else {
                LOGGER.warn("No se encontro app-default.properties dentro del JAR.");
            }
        } catch (IOException exception) {
            LOGGER.error("Error cargando configuracion por defecto.", exception);
        } finally {
            closeQuietly(input);
        }
    }

    private void loadExternalProperties() {
        File file = new File(externalConfigDir, "app.properties");
        if (!file.exists()) {
            LOGGER.info("No existe app.properties externo en {}. Se usan valores por defecto.", externalConfigDir.getAbsolutePath());
            return;
        }

        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            Properties external = new Properties();
            external.load(input);
            properties.putAll(external);
            LOGGER.info("Configuracion externa cargada desde {}", file.getAbsolutePath());
        } catch (IOException exception) {
            LOGGER.error("Error cargando configuracion externa desde " + file.getAbsolutePath(), exception);
        } finally {
            closeQuietly(input);
        }
    }

    public String getString(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            LOGGER.warn("Valor invalido para {}: {}. Se usa valor por defecto {}", key, value, Integer.valueOf(defaultValue));
            return defaultValue;
        }
    }

    public File getExternalConfigDir() {
        return externalConfigDir;
    }

    private void closeQuietly(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException ignored) {
                LOGGER.warn("No se pudo cerrar un recurso de configuracion.");
            }
        }
    }
}
