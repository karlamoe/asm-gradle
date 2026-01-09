package moe.karla.asm.gradle.internal;

import moe.karla.asm.gradle.AsmGradlePlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AsmProperties {
    public static final Properties PROPERTIES;
    public static final String VERSION;

    static {
        Properties prop = new Properties();
        try (InputStream in = AsmGradlePlugin.class.getResourceAsStream("metadata.properties")) {
            if (in != null) {
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    prop.load(reader);
                }
            }
        } catch (Exception ignored) {
        }
        PROPERTIES = prop;
        VERSION = prop.getProperty("version", "1.0.0-dev-SNAPSHOT");
    }
}
