package com.libs.brut.androlib;

import org.jf.baksmali.baksmali;
import org.jf.smali.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ApktoolProperties {

    public static String get(String key) {
        return get().getProperty(key);
    }

    public static Properties get() {
        if (sProps == null) {
            loadProps();
        }
        return sProps;
    }

    private static void loadProps() {
        InputStream in = ApktoolProperties.class.getResourceAsStream("/properties/apktool.properties");
        sProps = new Properties();
        try {
            sProps.load(in);
            in.close();
        } catch (IOException ex) {
            LOGGER.warning("Can't load properties.");
        }

        InputStream templateStream = null;
        try {
            templateStream = baksmali.class.getClassLoader().getResourceAsStream("baksmali.properties");
        } catch (NoClassDefFoundError ex) {
            LOGGER.warning("Can't load baksmali properties.");
        }
        Properties properties = new Properties();
        String version = "(unknown)";

        if (templateStream != null) {
            try {
                properties.load(templateStream);
                version = properties.getProperty("application.version");
                templateStream.close();
            } catch (IOException ignored) {
            }
        }
        sProps.put("baksmaliVersion", version);

        templateStream = null;
        try {
            templateStream = main.class.getClassLoader().getResourceAsStream("smali.properties");
        } catch (NoClassDefFoundError ex) {
            LOGGER.warning("Can't load smali properties.");
        }
        properties = new Properties();
        version = "(unknown)";

        if (templateStream != null) {
            try {
                properties.load(templateStream);
                version = properties.getProperty("application.version");
                templateStream.close();
            } catch (IOException ignored) {
            }
        }
        sProps.put("smaliVersion", version);
    }

    private static Properties sProps;

    private static final Logger LOGGER = Logger.getLogger(ApktoolProperties.class.getName());
}