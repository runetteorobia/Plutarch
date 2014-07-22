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
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Runette Orobia
 */
public class DbUtil {

    public static final String DELIMETER = Config.getProperties("csv.delimeter");
    public static final String NEW_LINE = Config.getProperties("csv.new.line");
    
    public static final String DB_URL = Config.getProperties("datasource.url");
    public static final int DB_PORT = Integer.parseInt(Config.getProperties("datasource.port"));
    public static final String DB_NAME = Config.getProperties("datasource.database");
    public static final String DB_ID_COLLECTION = Config.getProperties("datasource.ids.collection");
    public static final String DB_DETAILS_COLLECTION = Config.getProperties("datasource.details.collection");
    
    private static Mongo mongo;
    private static DB db;
    private static DBCollection idsCollection;
    private static DBCollection detailsCollection;

    
    private static void initializeDb() throws UnknownHostException {
        System.out.println("initialize db.");
        mongo = new MongoClient(DB_URL, DB_PORT);
        db = mongo.getDB(DB_NAME);
        idsCollection = db.getCollection(DB_ID_COLLECTION);
        detailsCollection = db.getCollection(DB_DETAILS_COLLECTION);
    }

    public static List<String> getAllIds() throws UnknownHostException {

        if (mongo == null) {
            initializeDb();
        }

        List<String> ids = new ArrayList<String>();
        DBCursor cursor = idsCollection.find();

        while (cursor.hasNext()) {
            ids.add(cursor.next().get("page_id").toString());
        }

        return ids;

    }

    public static List<String> getRemainingIds(int skip) throws UnknownHostException {

        if(mongo == null) {
            initializeDb();
        }
        
        List<String> ids = new ArrayList<String>();

        DBCursor cursor;

        skip = skip == 0 ? getDetailsCount() : skip;
        cursor = idsCollection.find().skip(skip);

        while (cursor.hasNext()) {
            ids.add(cursor.next().get("page_id").toString());
        }

        return ids;
    }

    public static List<String> getIdsWithErrors() throws UnknownHostException {

        if (mongo == null) {
            initializeDb();
        }

        List<String> ids = new ArrayList<String>();

        DBCollection idsCollection = db.getCollection("facebook_details");
        DBCursor cursor;

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("status", "error");

        cursor = idsCollection.find(whereQuery);

        while (cursor.hasNext()) {
            ids.add(cursor.next().get("id").toString());
        }

        return ids;

    }

    public static int getIdsCount() throws UnknownHostException {
        if (mongo == null) initializeDb();
        return (int) idsCollection.count();
    }
    
    public static int getDetailsCount() throws UnknownHostException {
        if (mongo == null) initializeDb();
        return (int) detailsCollection.count();
    }
    
    public static void updateDbDetails(String details, String id) throws UnknownHostException {
        
        if (mongo == null) initializeDb();
        
        BasicDBObject document = new BasicDBObject();
        document.put("id", id);
        document.put("status", "error");
        try {

            System.out.println("Deleting record: " + document.toString());
            detailsCollection.remove(document);
            System.out.println("[" + id + "] record successfully deleted.");
        } catch (Exception e) {
            System.out.println("ERROR in deleting [" + id + "] record. MESSSAGE: " + e.getMessage());
        }

        saveDetailsToDb(details);
    }

    public static void saveIdsToDb(InputStream is) throws UnknownHostException, IOException {

        if (mongo == null) {
            initializeDb();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line = null;
        Scanner scanner = null;

        while ((line = reader.readLine()) != null) {
            scanner = new Scanner(line);
            scanner.useDelimiter("^");
            while (scanner.hasNext()) {
                String data = scanner.next();
                String id = data.substring(0, data.indexOf("^"));
                DBObject object = (DBObject) JSON.parse("{'page_id':'" + id + "'}");
                idsCollection.insert(object);

            }
        }
        reader.close();

    }

    public static void saveDetailsToDb(String details) throws UnknownHostException {

        if (mongo == null) {
            initializeDb();
        }
        
        DBObject object = (DBObject) JSON.parse(details);
        try {
            detailsCollection.insert(object);
            System.out.println("[" + object.get("id") + "] record successfully created.");
        } catch (Exception e) {
            System.out.println("Error in inserting [" + object.get("id") + "] record. MESSAGE: " + e.getMessage());
        }

    }

    public static void cloneCollection() throws UnknownHostException {
        
        if(mongo == null) initializeDb();
        
        Date now = new Date();
        DateFormat shortDf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        
        String dateStr = shortDf.format(now);
        
        List<DBObject> idsList = new ArrayList<DBObject>();
        idsList.add(new BasicDBObject("$out", idsCollection.getName() + "_" + dateStr));
        
        List<DBObject> detailsList = new ArrayList<DBObject>();
        detailsList.add(new BasicDBObject("$out", detailsCollection.getName() + "_" + dateStr));
        
        idsCollection.aggregate(idsList);
        detailsCollection.aggregate(detailsList);
        
        idsCollection.remove(new BasicDBObject());
        detailsCollection.remove(new BasicDBObject());
        
        System.out.println("DB UTIL. CLONE COLLECTION.");
        
    }
    
    public static void main(String[] args) {
        
        try {
            
            if(mongo == null) initializeDb();
            
            List<DBObject> ops = new ArrayList<DBObject>();
            ops.add(new BasicDBObject("$out", "testCol3"));
            
            DBCollection testCol = db.getCollection("testCol");
            testCol.aggregate(null, (DBObject[]) ops.toArray());
        
        } catch (UnknownHostException ex) {
            Logger.getLogger(DbUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
