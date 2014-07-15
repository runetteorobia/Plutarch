/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.util;

import static com.egg.plutarch.util.ServiceUtil.FACEBOOK_URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import net.sf.json.JSONObject;

/**
 *
 * @author Runette Orobia
 */
public class TestUtil {
    
    public static List<String> parseCSVFile(InputStream is) throws IOException {
        List<String> ids = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        String line = null;
        Scanner scanner = null;
        
        while((line = reader.readLine()) != null) {
            scanner = new Scanner(line);
            scanner.useDelimiter("^");
            while(scanner.hasNext()) {
                String data = scanner.next();
                String id = data.substring(0, data.indexOf("^"));
                
                ids.add(id);
//                saveIdsToDb(id);
            }
        }
        reader.close();
        
        return ids;
    }
    
    public static JSONObject getPageDetailsJson(String pageId, String accessToken) {
        StringBuilder contents = new StringBuilder();
        try {
            URL oracle = new URL(FACEBOOK_URL + pageId + "?access_token=" + accessToken);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) 
                contents.append(inputLine);
            in.close();
            
        } catch(Exception e) {
            System.out.println("ERROR: " + pageId + " | MESSAGE: " + e.getMessage());
            return null; 
        }
        
        return JSONObject.fromObject(contents.toString().trim());
    }
    
//    public static void saveUnprocessedIdsToNewCollection() {
//        
//        try {
//        
//            if(mongo == null) {
//                initializeDb();
//            }
//            
//            DBCollection newCollection = db.getCollection("facebook_ids");
//            DBCursor cursor;
//            
//            BasicDBObject whereQuery = new BasicDBObject();
//            whereQuery.put("status", "error");
//            
//            cursor = collection.find(whereQuery);
//            
//            String details = null;
//
//            while(cursor.hasNext()) {
//                JSONObject object = JSONObject.fromObject(cursor.next().toString());
//                DBObject dbObject = (DBObject) JSON.parse("{'page_id':'"+ object.get("id").toString() + "'}");
//                newCollection.insert(dbObject);
//            } 
//
//            
//        } catch (UnknownHostException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        
//    }
    
}
