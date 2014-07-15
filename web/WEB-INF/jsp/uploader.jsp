<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://connect.facebook.net/en_US/all.js"></script>
        <script type="text/javascript">
            var token = "";
            var app_id = ${appId};
            
            FB.init({
                appId: app_id,
                status: true,
                cookie: true,
                xfbml: true
            });

            FB.login(function(response) {
                if (response.authResponse) {
                    var access_token = FB.getAuthResponse()['accessToken'];
                    token = access_token;
                    console.log('Access Token = ' + access_token);
                    enableButton();
                } else {
                    console.log('User cancelled login or did not fully authorize.');
                }
            }, {scope: ''});

            function enableButton() {
                document.getElementById("token").value = token;
                document.getElementById("submitButton").disabled = false;
            }
        </script>
    </head>

    <body>
        <b>Upload File</b> <br/>
        <form method="POST" action="/Plutarch/main/upload" enctype="multipart/form-data" id="uploadForm">
            <input type="file" name="csvFile" value="Select a CSV File to upload"/> 
            <input type="hidden" id="token" name="token" value="" />
            <input type="submit" id="submitButton" value="Upload" disabled="disabled" />
        </form>
        
        <br/>
        
        <b>Process Records</b> <br/>
        <button>Process Details</button>
        <button>Resume Processing of Details</button>
        <button>Process Errors</button>
        
        <br/> <br/>
        <b>Extraction of Data</b> <br/>
        <button>Extract all</button>
        <button>Extract records</button>
        <button>Extract errors</button>

        
    </body>
</html>
