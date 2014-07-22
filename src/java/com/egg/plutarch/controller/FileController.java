/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.controller;

import com.egg.plutarch.util.Config;
import com.egg.plutarch.util.DbUtil;
import com.egg.plutarch.util.FacebookUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 *
 * @author Runette Orobia
 */
@Controller
@RequestMapping("/main")
public class FileController {
    
    private String accessToken = "";
    private OutputStream outputStream;
    
    @RequestMapping(value="/form")
    public String displayMessage(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        try {
            model.addAttribute("appId", Config.getProperties("app.id"));
            model.addAttribute("idCount", DbUtil.getIdsCount());
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "uploader";
    }
    
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public synchronized void upload(MultipartHttpServletRequest request, HttpServletResponse response, 
        ModelMap model, @RequestParam("token") final String ACCESSTOKEN) throws IOException {
        
        String newAccessToken = FacebookUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        MultipartFile csvFile = request.getFile("csvFile");
        InputStream is = csvFile.getInputStream();
        
        try{
            
            DbUtil.saveIdsToDb(is);
            List<String> ids = DbUtil.getAllIds();
            StringBuilder results = new StringBuilder();
            String details = null;
            
            for(String s : ids) {
                details = FacebookUtil.getPageDetails(s, accessToken);
                DbUtil.saveDetailsToDb(details);
            } 
            
            this.exportToCSV(request, response, model);
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public synchronized void exportToCSV(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        
        try {
        
            Mongo mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("Plutarch");
            DBCollection collection = db.getCollection("facebook_details");
            DBCursor cursor;
            
            outputStream = response.getOutputStream();
            
            String filename = "csvFile_results_" + System.currentTimeMillis()+".csv";
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="+filename);
            
            cursor = collection.find();
            
            StringBuilder results = new StringBuilder();
            String details = null;

            while(cursor.hasNext()) {
                
                details = cursor.next().toString();
                
                JSONObject object = JSONObject.fromObject(details);
                object.remove("_id");
                details = object.toString();
                
                System.out.println(details);
                
                details = details.substring(1, details.length()-1);
                
                outputStream.write(details.getBytes());

                if(!details.isEmpty()) {
                    outputStream.write(DbUtil.DELIMETER.getBytes());
                    outputStream.write(DbUtil.NEW_LINE.getBytes());
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
    
    @RequestMapping(value = "/cleanRecords/export", method = RequestMethod.GET)
    public synchronized void exportCleanRecordsToCSV(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        
        try {
        
            Mongo mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("Plutarch");
            DBCollection collection = db.getCollection("facebook_details");
            DBCursor cursor;
            
            outputStream = response.getOutputStream();
            
            String filename = "csvFile_clean_results_" + System.currentTimeMillis()+".csv";
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="+filename);
            
            BasicDBObject notErrorQuery = new BasicDBObject();
            notErrorQuery.put("$ne", "error");
            
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("status", notErrorQuery);
            
            cursor = collection.find(whereQuery);
            
            StringBuilder results = new StringBuilder();
            String details = null;

            while(cursor.hasNext()) {
                
                details = cursor.next().toString();
                
                JSONObject object = JSONObject.fromObject(details);
                object.remove("_id");
                details = object.toString();
                
                System.out.println(details);
                
                details = details.substring(1, details.length()-1);
                
                outputStream.write(details.getBytes());

                if(!details.isEmpty()) {
                    outputStream.write(DbUtil.DELIMETER.getBytes());
                    outputStream.write(DbUtil.NEW_LINE.getBytes());
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
    
    @RequestMapping(value = "/errors/export", method = RequestMethod.GET)
    public synchronized void exportErrorsToCSV(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        
        try {
        
            Mongo mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("Plutarch");
            DBCollection collection = db.getCollection("facebook_details");
            DBCursor cursor;
            
            outputStream = response.getOutputStream();
            
            String filename = "csvFile_error_results_" + System.currentTimeMillis()+".csv";
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="+filename);
            
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("status", "error");
            
            cursor = collection.find(whereQuery);
            
            StringBuilder results = new StringBuilder();
            String details = null;

            while(cursor.hasNext()) {
                
                details = cursor.next().toString();
                
                JSONObject object = JSONObject.fromObject(details);
                object.remove("_id");
                details = object.toString();
                
                System.out.println(details);
                
                details = details.substring(1, details.length()-1);
                
                outputStream.write(details.getBytes());

                if(!details.isEmpty()) {
                    outputStream.write(DbUtil.DELIMETER.getBytes());
                    outputStream.write(DbUtil.NEW_LINE.getBytes());
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
    
}
