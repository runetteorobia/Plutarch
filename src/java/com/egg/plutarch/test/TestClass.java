/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import java.net.UnknownHostException;

/**
 *
 * @author Runette Orobia
 */
public class TestClass {

    private static final String FACEBOOK = "https://graph.facebook.com/";
    private static String accessToken = "";

    public static void main(String[] args) {

        try {

            Mongo mongo = new Mongo("localhost", 27017);
            DB db = mongo.getDB("Plutarch");
            DBCollection collection = db.getCollection("testCollection");

            // convert JSON to DBObject directly
            DBObject dbObject = (DBObject) JSON
                    .parse("{'name':'mkyong', 'age':30}");

            collection.insert(dbObject);

            DBCursor cursorDoc = collection.find();
            while (cursorDoc.hasNext()) {
                System.out.println(cursorDoc.next());
            }

            System.out.println("Done");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }


    }
}
