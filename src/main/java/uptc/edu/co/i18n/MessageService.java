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
        String fileName = "i18n/messages_" + locale + ".properties";
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream(fileName);
            if (input == null) {
                input = getClass().getClassLoader().getResourceAsStream("i18n/messages_es.properties");
            }

            if (input != null) {
                messages.load(input);
                LOGGER.info("Mensajes por defecto cargados para locale {}", locale);
            } else {
                LOGGER.warn("No se encontro archivo de mensajes por defecto.");
            }
        } catch (IOException exception) {
            LOGGER.error("Error cargando mensajes por defecto.", exception);
        } finally {
            closeQuietly(input);
        }
    }

    private void loadExternalMessages(File externalConfigDir, String locale) {
        File externalFile = new File(externalConfigDir, "messages_" + locale + ".properties");
        if (!externalFile.exists()) {
            LOGGER.info("No existe archivo externo de mensajes: {}", externalFile.getAbsolutePath());
            return;
        }

        FileInputStream input = null;
        try {
            input = new FileInputStream(externalFile);
            Properties external = new Properties();
            external.load(input);
            messages.putAll(external);
            LOGGER.info("Mensajes externos cargados desde {}", externalFile.getAbsolutePath());
        } catch (IOException exception) {
            LOGGER.error("Error cargando mensajes externos.", exception);
        } finally {
            closeQuietly(input);
        }
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
