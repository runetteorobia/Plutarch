/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.util;

import java.util.Properties;

/**
 *
 * @author Runette Orobia
 */
public class Config {
    
    private static Properties properties;

    public static Properties getProperties() {
        return properties;
    }

    public static String getProperties(String key) {
        return getProperties().getProperty(key);
    }

    public void setProperties(Properties properties) {
        Config.properties = properties;
    }
    
}
