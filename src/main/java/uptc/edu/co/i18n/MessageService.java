package uptc.edu.co.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uptc.edu.co.config.AppConfig;

public class MessageService {
    private static final Logger LOGGER = LogManager.getLogger(MessageService.class);

    private final Properties messages;

    public MessageService(AppConfig appConfig) {
        this.messages = new Properties();

        String locale = appConfig.getString("app.locale", "es");
        loadDefaultMessages(locale);
        loadExternalMessages(appConfig.getExternalConfigDir(), locale);
    }

    private void loadDefaultMessages(String locale) {
        InputStream input = openDefaultMessages(locale);
        if (input == null) {
            LOGGER.warn("No se encontro archivo de mensajes por defecto.");
            return;
        }
        loadDefaultMessagesFromStream(input, locale);
    }

    private void loadDefaultMessagesFromStream(InputStream input, String locale) {
        try {
            messages.load(input);
            LOGGER.info("Mensajes por defecto cargados para locale {}", locale);
        } catch (IOException exception) {
            LOGGER.error("Error cargando mensajes por defecto.", exception);
        } finally {
            closeQuietly(input);
        }
    }

    private InputStream openDefaultMessages(String locale) {
        InputStream input = openClassPathMessages(locale);
        if (input != null) {
            return input;
        }
        return openFileSystemMessages(locale);
    }

    private InputStream openClassPathMessages(String locale) {
        String fileName = buildLocaleFileName(locale);
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        if (input != null) {
            return input;
        }
        return getClass().getClassLoader().getResourceAsStream("i18n/messages_es.properties");
    }

    private InputStream openFileSystemMessages(String locale) {
        InputStream input = openFile("src/main/resources/" + buildLocaleFileName(locale));
        if (input != null) {
            return input;
        }
        return openFile("src/main/resources/i18n/messages_es.properties");
    }

    private InputStream openFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (IOException exception) {
            LOGGER.error("No se pudo abrir archivo de mensajes {}", path, exception);
            return null;
        }
    }

    private String buildLocaleFileName(String locale) {
        return "i18n/messages_" + locale + ".properties";
    }

    private void loadExternalMessages(File externalConfigDir, String locale) {
        File externalFile = new File(externalConfigDir, "messages_" + locale + ".properties");
        if (!externalFile.exists()) {
            LOGGER.info("No existe archivo externo de mensajes: {}", externalFile.getAbsolutePath());
            return;
        }
        loadExternalMessagesFromFile(externalFile);
    }

    private void loadExternalMessagesFromFile(File externalFile) {
        InputStream input = null;
        try {
            input = new FileInputStream(externalFile);
            mergeExternalMessages(input);
            LOGGER.info("Mensajes externos cargados desde {}", externalFile.getAbsolutePath());
        } catch (IOException exception) {
            LOGGER.error("Error cargando mensajes externos.", exception);
        } finally {
            closeQuietly(input);
        }
    }

    private void mergeExternalMessages(InputStream input) throws IOException {
        Properties external = new Properties();
        external.load(input);
        messages.putAll(external);
    }

    public String get(String key) {
        String value = messages.getProperty(key);
        if (value == null) {
            return "!" + key + "!";
        }
        return value;
    }

    private void closeQuietly(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException ignored) {
                LOGGER.warn("No se pudo cerrar un recurso de mensajes.");
            }
        }
    }
}
