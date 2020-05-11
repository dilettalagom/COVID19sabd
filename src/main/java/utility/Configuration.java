package utility;

import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private Properties properties;
    private static Configuration instance;
    private String sparkExecuteContext;

    @Getter
    private String applicationName;

    private Configuration(String fileConf) {

        try {
            InputStream input = this.getClass().getResourceAsStream("/"+fileConf);
            properties = new Properties();
            properties.load(input);
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found: "+fileConf);
        }
        catch (IOException e) {
            System.err.println("Error reading conf file: "+fileConf);
        }
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration("configuration.properties");
        }
        return instance;
    }

    public static String getProperty(String confKey) {
        return getInstance().properties.getProperty(confKey);
    }

    public static Configuration setSparkExecutionContext(String sec) {
        Configuration appConfiguration = Configuration.getInstance();
        appConfiguration.sparkExecuteContext = sec;
        return appConfiguration;
    }

    public static Configuration setApplicationName(String appName) {
        Configuration appConfiguration = Configuration.getInstance();
        appConfiguration.applicationName = appName;
        return appConfiguration;
    }

    public static String getSparkExecuteContext() {
        return Configuration.getInstance().sparkExecuteContext;
    }

    public static String getApplicationName() {
        return Configuration.getInstance().applicationName;
    }
}
