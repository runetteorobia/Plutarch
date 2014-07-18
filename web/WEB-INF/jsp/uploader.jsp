<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script src="http://connect.facebook.net/en_US/all.js"></script>
        
        <script type="text/javascript">
            var app_id = ${appId};
        </script>
        
        <script src="<c:url value="/js/facebook.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/js/userInterface.js"/>" type="text/javascript"></script>

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
        <button onclick="">Extract all</button>
        <button onclick="">Extract records</button>
        <button onclick="">Extract errors</button>


    </body>
</html>
