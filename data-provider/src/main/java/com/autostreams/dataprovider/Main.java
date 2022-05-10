package com.autostreams.dataprovider;

import com.autostreams.utils.dataprovider.DataProvider;
import com.autostreams.utils.fileutils.FileUtils;
import java.util.Properties;

/**
 * The class containing the main entry point of the data producer application.
 *
 * @version 1.0
 * @since 1.0
 */
public class Main {
    private static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";
    private static final String HOST_PROPERTIES_VARIABLE_NAME = "producer.url";
    private static final String PORT_PROPERTIES_VARIABLE_NAME = "producer.port";

    private static final String HOST_ENVIRONMENT_VARIABLE_NAME = "PRODUCER_URL";
    private static final String PORT_ENVIRONMENT_VARIABLE_NAME = "PRODUCER_PORT";

    private static String host;
    private static Integer port;

    /**
     * The main entry point of the application.
     *
     * @param args the commandline arguments.
     */
    public static void main(String[] args) {
        setValues();

        DataProvider dataProvider = DataProvider.fromHostAndPort(host, port);
        if (dataProvider.initialize()) {
            dataProvider.run();
        }
    }

    private static void setValues() {
        if (canSetValuesFromEnvironmentVariables()) {
            setValuesFromEnvironmentVariables();
            return;
        }

        if (canSetValuesFromPropertiesFile()) {
            setValuesFromPropertiesFile();
            return;
        }

        setDefaultValues();
    }

    private static boolean canSetValuesFromEnvironmentVariables() {
        if (!System.getenv().containsKey(HOST_ENVIRONMENT_VARIABLE_NAME)) {
            return false;
        }

        if (!System.getenv().containsKey(PORT_ENVIRONMENT_VARIABLE_NAME)) {
            return false;
        }

        return true;
    }

    private static void setValuesFromEnvironmentVariables() {
        host = System.getenv().get(HOST_ENVIRONMENT_VARIABLE_NAME);
        port = Integer.parseInt(System.getenv().get(PORT_ENVIRONMENT_VARIABLE_NAME));
    }

    private static boolean canSetValuesFromPropertiesFile() {
        Properties props = FileUtils.loadPropertiesFromFile(CONFIG_PROPERTIES_FILE_NAME);

        if (!props.containsKey(HOST_PROPERTIES_VARIABLE_NAME)) {
            return false;
        }

        if (!props.containsKey(PORT_PROPERTIES_VARIABLE_NAME)) {
            return false;
        }

        return true;
    }

    private static void setValuesFromPropertiesFile() {
        Properties props = FileUtils.loadPropertiesFromFile(CONFIG_PROPERTIES_FILE_NAME);

        host = props.getProperty(HOST_PROPERTIES_VARIABLE_NAME);
        port = Integer.parseInt(props.getProperty(PORT_PROPERTIES_VARIABLE_NAME));
    }

    private static void setDefaultValues() {
        host = "127.0.0.1";
        port = 8992;
    }
}