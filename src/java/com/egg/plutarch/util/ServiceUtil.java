/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import net.sf.json.JSONObject;

/**
 *
 * @author Runette Orobia
 */
public class ServiceUtil {
    
    public static final String FACEBOOK_URL = Config.getProperties("facebook.graph.url");
    public static final String FACEBOOK_NEW_TOKEN_URL = Config.getProperties("facebook.token.url");
    public static final String DELIMETER = Config.getProperties("^");
    public static final String NEW_LINE = Config.getProperties("");
    
    public static final String APP_ID = Config.getProperties("app.id");
    public static final String APP_SECRET = Config.getProperties("app.secret");
    
    private static Mongo mongo;
    private static DB db;
    private static DBCollection collection;
    
    
    private static void initializeDb() throws UnknownHostException {
        System.out.println("initialize db.");
        mongo = new MongoClient("localhost", 27017);
        db = mongo.getDB("Plutarch");
        collection = db.getCollection("facebook_details");
    }
    
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
    
    public static void saveIdsToDb(InputStream is) throws UnknownHostException, IOException {
        
        if(mongo == null) {
            initializeDb();
        }
        
        DBCollection idsCollection = db.getCollection("facebook_page");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        String line = null;
        Scanner scanner = null;
        
        while((line = reader.readLine()) != null) {
            scanner = new Scanner(line);
            scanner.useDelimiter("^");
            while(scanner.hasNext()) {
                String data = scanner.next();
                String id = data.substring(0, data.indexOf("^"));
                DBObject object = (DBObject) JSON.parse("{'page_id':'" + id + "'}");
                idsCollection.insert(object);
                
            }
        }
        reader.close();
        
    }
    
    public static List<String> getAllIds() throws UnknownHostException {
        
        if(mongo == null) {
            initializeDb();
        }

        List<String> ids = new ArrayList<String>();
        
        DBCollection idsCollection = db.getCollection("facebook_page");
        DBCursor cursor = idsCollection.find();
        
        while(cursor.hasNext()) {
            ids.add(cursor.next().get("page_id").toString());
        }
        
        return ids;
        
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
    
    public static void saveDetailsToDb(String details) {
        
        DBObject object = (DBObject) JSON.parse(details);
        try {
            collection.insert(object);
            System.out.println("[" + object.get("id") + "] record successfully created.");
        } catch(Exception e) {
            System.out.println("Error in inserting [" + object.get("id") + "] record. MESSAGE: " + e.getMessage());
        }
        
    }
    
    
     /***********METHODS USED FOR TESTING************/
    public static List<String> getIds(int place) throws UnknownHostException {
        
        initializeDb();
        List<String> ids = new ArrayList<String>();
        
        DBCollection idsCollection = db.getCollection("facebook_page");
        DBCursor cursor;
        
        int count = idsCollection.find().count();
        int limit = count/20;
        
        System.out.println("UTIL. count: " + count + " | place: " + place + " | limit: " + limit);
        
        cursor = idsCollection.find();
        
        if(place == 0) {
            cursor = idsCollection.find().limit(limit);
        } else if(place == 19) {
            cursor = idsCollection.find().skip(limit * place);
        } else {
            cursor = idsCollection.find().skip(limit * place).limit(limit);
        }
        
        while(cursor.hasNext()) {
            ids.add(cursor.next().get("page_id").toString());
        }
        
        return ids;
    }
    
    public static List<String> getRemainingIds(int skip) throws UnknownHostException {
        
        initializeDb();
        List<String> ids = new ArrayList<String>();
        
        DBCollection idsCollection = db.getCollection("facebook_page");
        DBCursor cursor;
        
        cursor = idsCollection.find().skip(skip);
        
        while(cursor.hasNext()) {
            ids.add(cursor.next().get("page_id").toString());
        }
        
        return ids;
    }
   
    public static void updateDbDetails(String details, String id) {
        BasicDBObject document = new BasicDBObject();
	document.put("id", id);
        document.put("status", "error");
        try {
            
            System.out.println("Deleting record: " + document.toString());
            collection.remove(document);
            System.out.println("[" + id + "] record successfully deleted.");
        } catch(Exception e) {
            System.out.println("ERROR in deleting [" + id + "] record. MESSSAGE: " + e.getMessage());
        }
        
        saveDetailsToDb(details);
    }
    
    public static List<String> getIdsWithErrors() throws UnknownHostException {
        
        if(mongo == null) {
            initializeDb();
        }
        
        List<String> ids = new ArrayList<String>();
        
        DBCollection idsCollection = db.getCollection("facebook_details");
        DBCursor cursor;
        
        BasicDBObject whereQuery = new BasicDBObject();
	whereQuery.put("status", "error");
        
        cursor = idsCollection.find(whereQuery);
        
        while(cursor.hasNext()) {
            ids.add(cursor.next().get("id").toString());
        }
        
        return ids;
        
    }
    
}
