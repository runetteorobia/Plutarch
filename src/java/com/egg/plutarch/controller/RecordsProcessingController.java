/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.controller;

import com.egg.plutarch.util.Config;
import com.egg.plutarch.util.DbUtil;
import com.egg.plutarch.util.FacebookUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/main")
public class RecordsProcessingController {
    
    private String accessToken = "";
    private OutputStream outputStream;
    
    @RequestMapping(value = "/clone", method = RequestMethod.GET)
    public synchronized String cloneCollection(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        try {
            
            System.out.println("CLONING... ");
            DbUtil.cloneCollection();
            System.out.println("DONE.");
            
            model.addAttribute("appId", Config.getProperties("app.id"));
            model.addAttribute("idCount", DbUtil.getIdsCount());
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(RecordsProcessingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "uploader";
    }
    
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public synchronized void downloadRemaining(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
        @RequestParam("token") final String ACCESSTOKEN) {
        
        String newAccessToken = FacebookUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        try {
            List<String> ids = DbUtil.getAllIds();
            StringBuilder results = new StringBuilder();
            String details = null;
            
            for(String s : ids) {
                details = FacebookUtil.getPageDetails(s, accessToken);
                DbUtil.saveDetailsToDb(details);
            } 
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @RequestMapping(value = "/process", method = RequestMethod.GET)
    public synchronized void proccessErrors(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
        @RequestParam("token")  final String ACCESSTOKEN) throws UnknownHostException {
        
        List<String> ids = DbUtil.getIdsWithErrors();
        StringBuilder results = new StringBuilder();
        String details = null;

        String newAccessToken = FacebookUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        for(String s : ids) {
            details = FacebookUtil.getPageDetails(s,accessToken);
            if(!details.contains("\"status\":\"error\"")) {
                DbUtil.updateDbDetails(details, s);
            }
        } 
        
    }
 
    @RequestMapping(value = "/resume", method = RequestMethod.GET)
    public synchronized void resumeDownload(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
        @RequestParam(value = "skip", required = false, defaultValue = "0") int skip, 
        @RequestParam("token") final String ACCESSTOKEN) {
        
        String newAccessToken = FacebookUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        try {
            List<String> ids = DbUtil.getRemainingIds(skip);
            
            StringBuilder results = new StringBuilder();
            String details = null;

            for(String s : ids) {
                details = FacebookUtil.getPageDetails(s,accessToken);
                DbUtil.saveDetailsToDb(details);
            } 
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
