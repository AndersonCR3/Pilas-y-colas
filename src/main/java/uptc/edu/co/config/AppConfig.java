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
        this.externalConfigDir = new File(resolveConfigDir());
        loadExternalProperties();
    }

    private void loadDefaultProperties() {
        InputStream input = openDefaultProperties();
        try {
            loadPropertiesOrWarn(input, "No se encontro app-default.properties dentro del JAR.");
            LOGGER.info("Configuracion por defecto cargada desde JAR.");
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
        loadExternalFromFile(file);
    }

    private void loadExternalFromFile(File file) {
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            mergeProperties(input);
            LOGGER.info("Configuracion externa cargada desde {}", file.getAbsolutePath());
        } catch (IOException exception) {
            LOGGER.error("Error cargando configuracion externa desde " + file.getAbsolutePath(), exception);
        } finally {
            closeQuietly(input);
        }
    }

    private void mergeProperties(InputStream input) throws IOException {
        Properties external = new Properties();
        external.load(input);
        properties.putAll(external);
    }

    private String resolveConfigDir() {
        String configuredDir = getConfiguredDir("app.config.dir", "APP_CONFIG_DIR");
        if (isBlank(configuredDir)) {
            return "config";
        }
        return configuredDir.trim();
    }

    private String getConfiguredDir(String propertyKey, String envKey) {
        String configuredDir = System.getProperty(propertyKey);
        if (!isBlank(configuredDir)) {
            return configuredDir;
        }
        return System.getenv(envKey);
    }

    private InputStream openDefaultProperties() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("app-default.properties");
        if (input != null) {
            return input;
        }
        return openFallbackDefaultProperties();
    }

    private InputStream openFallbackDefaultProperties() {
        File fallbackFile = new File("src/main/resources/app-default.properties");
        if (!fallbackFile.exists()) {
            return null;
        }
        try {
            LOGGER.info("Configuracion por defecto cargada desde archivo local {}", fallbackFile.getAbsolutePath());
            return new FileInputStream(fallbackFile);
        } catch (IOException exception) {
            LOGGER.error("No se pudo abrir app-default.properties local.", exception);
            return null;
        }
    }

    private void loadPropertiesOrWarn(InputStream input, String warningMessage) throws IOException {
        if (input == null) {
            LOGGER.warn(warningMessage);
            return;
        }
        properties.load(input);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
