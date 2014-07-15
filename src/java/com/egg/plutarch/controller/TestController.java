/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.controller;

import com.egg.plutarch.util.Config;
import com.egg.plutarch.util.ServiceUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Runette Orobia
 */
@Controller
@RequestMapping("/test")
public class TestController {
    
    private String accessToken;
    private OutputStream outputStream;
    
    @RequestMapping(value = "/downloadTest", method = RequestMethod.GET)
    public synchronized void download(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
        @RequestParam("count") int place, @RequestParam("token") final String ACCESSTOKEN) {
        
        String newAccessToken = ServiceUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        try {
            List<String> ids = ServiceUtil.getIds(place);
//            outputStream = response.getOutputStream();
//            
//            String filename = "csvFile_results_" + (place + 1) + "_" + System.currentTimeMillis()+".csv";
//            response.setContentType("text/csv; charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment; filename="+filename);
            
            StringBuilder results = new StringBuilder();
            String details = null;

            System.out.println("PLACE: " + place);
            
            for(String s : ids) {
                details = ServiceUtil.getPageDetails(s,accessToken);
                ServiceUtil.saveDetailsToDb(details);
            } 
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @RequestMapping(value = "/downloadDetails", method = RequestMethod.GET)
    public synchronized void downloadDetails(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
        @RequestParam("count") int place) {
        
        try {
        
            Mongo mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("Plutarch");
            DBCollection collection = db.getCollection("facebook_details");
            DBCursor cursor;
            
            outputStream = response.getOutputStream();
            
            String filename = "csvFile_results_" + (place + 1) + "_" + System.currentTimeMillis()+".csv";
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="+filename);
            
            int count = collection.find().count();
            int limit = count/20;
            
//            if(place == 0) {
//                cursor = collection.find().limit(limit);
//            } else if(place == 19) {
//                cursor = collection.find().skip(limit * place);
//            } else {
//                cursor = collection.find().skip(limit * place).limit(limit);
//            }
            
            cursor = collection.find();
            
            StringBuilder results = new StringBuilder();
            String details = null;

            System.out.println("PLACE: " + place);
            
            while(cursor.hasNext()) {
                
                details = cursor.next().toString();
                
                JSONObject object = JSONObject.fromObject(details);
                object.remove("_id");
                
                details = object.toString();
                details = details.substring(1, details.length()-1);
                
                outputStream.write(details.getBytes());

                if(!details.isEmpty()) {
                    outputStream.write(ServiceUtil.DELIMETER.getBytes());
                    outputStream.write(ServiceUtil.NEW_LINE.getBytes());
                }
                System.out.println(details);
            } 

            outputStream.flush();
            outputStream.close();
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @RequestMapping(value = "saveIds", method = RequestMethod.GET)
    public synchronized void saveIds(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        
        try {
        
            Mongo mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("Plutarch");
            DBCollection originialCollection = db.getCollection("facebook_details");
            DBCollection newCollection = db.getCollection("facebook_ids");
            DBCursor cursor;
            
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("status", "error");
            
            cursor = originialCollection.find(whereQuery);

            while(cursor.hasNext()) {
                JSONObject object = JSONObject.fromObject(cursor.next().toString());
                DBObject dbObject = (DBObject) JSON.parse("{'page_id':'"+ object.get("id").toString() + "'}");
                newCollection.insert(dbObject);
            } 

        } catch (UnknownHostException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   
    
}
