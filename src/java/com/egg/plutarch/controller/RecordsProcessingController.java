/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.plutarch.controller;

import com.egg.plutarch.util.ServiceUtil;
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
    
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public synchronized void downloadRemaining(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
        @RequestParam("token") final String ACCESSTOKEN) {
        
        String newAccessToken = ServiceUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        try {
            List<String> ids = ServiceUtil.getAllIds();
            StringBuilder results = new StringBuilder();
            String details = null;
            
            for(String s : ids) {
                details = ServiceUtil.getPageDetails(s, accessToken);
                ServiceUtil.saveDetailsToDb(details);
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
        
        List<String> ids = ServiceUtil.getIdsWithErrors();
        StringBuilder results = new StringBuilder();
        String details = null;

        String newAccessToken = ServiceUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        for(String s : ids) {
            details = ServiceUtil.getPageDetails(s,accessToken);
            if(!details.contains("\"status\":\"error\"")) {
                ServiceUtil.updateDbDetails(details, s);
            }
        } 
        
    }
 
    @RequestMapping(value = "/resume", method = RequestMethod.GET)
    public synchronized void resumeDownload(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
        @RequestParam("skip") int skip, @RequestParam("token") final String ACCESSTOKEN) {
        
        String newAccessToken = ServiceUtil.getNewToken(ACCESSTOKEN);
        
        if(!newAccessToken.isEmpty()) {
            accessToken = newAccessToken;
            System.out.println("using LONG LIVED token.");
        } else {
            accessToken = ACCESSTOKEN;
            System.out.println("using SHORT LIVED token.");
        }
        
        try {
            List<String> ids = ServiceUtil.getRemainingIds(skip);
            
            StringBuilder results = new StringBuilder();
            String details = null;

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
    
    
}
