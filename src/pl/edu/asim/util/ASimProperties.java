package pl.edu.asim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

public class ASimProperties {

    protected static Properties properties = null;

    public ASimProperties() {
        loadProperties();
    }

    /**
     * @param name
     * @return
     */
    public static String getProperty(String name) {
        if (properties == null)
            loadProperties();
        return properties.getProperty(name);
    }

    public static void loadProperties() {
        properties = new Properties();
        InputStream is;
        try {
            File f = new File("./conf/asim.properties");
            is = new FileInputStream(f);
            properties.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
