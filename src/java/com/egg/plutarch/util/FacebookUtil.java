/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import net.sf.json.JSONObject;

/**
 *
 * @author Runette Orobia
 */
public class FacebookUtil {
    
    public static final String FACEBOOK_URL = Config.getProperties("facebook.graph.url");
    public static final String FACEBOOK_NEW_TOKEN_URL = Config.getProperties("facebook.token.url");
    public static final String APP_ID = Config.getProperties("app.id");
    public static final String APP_SECRET = Config.getProperties("app.secret");
    
    public static String getNewToken(String accessToken) {
        
        String result = "";
        
        try {
            URL oracle = new URL(FACEBOOK_NEW_TOKEN_URL + APP_ID + "&client_secret=" + APP_SECRET + "&fb_exchange_token=" + accessToken);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                result = inputLine.toString();
            }
            in.close();
            
            result = result.split("&")[0];
            result = result.substring(result.indexOf("=")+1, result.length());
            
        } catch(Exception e) {
            System.out.println("ERROR in generating new token. | MESSAGE: " + e.getMessage());
        }
        
        System.out.println("NEW TOKEN: " + result);
        
        return result;
        
    }
    
    public static String getPageDetails(String pageId, String accessToken) {
        
        StringBuilder contents = new StringBuilder();
        try {
            URL oracle = new URL(FACEBOOK_URL + pageId + "?access_token=" + accessToken);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                JSONObject object = JSONObject.fromObject(inputLine);
                contents.append(object.toString());
            }
            in.close();
            
        } catch(Exception e) {
            
            JSONObject object = JSONObject.fromObject("{\"id\":\"" + pageId + "\", \"status\":\"error\"}");
            System.out.println("ERROR: " + pageId + " | MESSAGE: " + e.getMessage());
            return  object.toString(); 
        }
//        return contents.substring(1, contents.length()-1);
        return contents.toString();
    }
    
}
