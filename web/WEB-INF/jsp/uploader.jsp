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
            var count = ${idCount};
        </script>

        <script src="<c:url value="/js/facebook.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/js/userInterface.js"/>" type="text/javascript"></script>

        <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">

    </head>

    <body>

        <h2>FB Likes</h2>

        <div id="uploadButton" class="buttons">Upload CSV File</div>
        <div id="uploadDiv" class="divs">

            <div id="promptDiv">
                There are ${idCount} pending records. <br/>
                <button id="addMoreBtn">Add more records</button>
                <button id="backupDbBtn" onclick="location.href = '/Plutarch/main/clone'">Process new batch</button>
            </div>

            <form method="POST" action="/Plutarch/main/upload" enctype="multipart/form-data" id="uploadForm">
                <input type="file" name="csvFile" value="Select a CSV File to upload"/>
                <input type="hidden" id="token" name="token" value="" />
                <input type="submit" id="submitButton" value="Upload" disabled="disabled" />
                <br/>
                <button id="backBtn">Back</button>
            </form>

        </div>

        <br/>

        <div id="processButton" class="buttons">Process Records</div>
        <div id="processDiv" class="divs">
            <button id="resumeBtn" class="actionBtn">Resume Processing of Details</button> <br />
            <button id="processErrorsBtn" class="actionBtn">Process Errors</button>
        </div>

        <br/>

        <div id="exportButton" class="buttons">Extract Data</div>
        <div id="exportDiv" class="divs">
            <button onclick="location.href = '/Plutarch/main/export'" class="actionBtn">Extract all</button> <br />
            <button onclick="location.href = '/Plutarch/main/cleanRecords/export'" class="actionBtn">Extract records</button> <br />
            <button onclick="location.href = '/Plutarch/main/errors/export'" class="actionBtn">Extract errors</button>
        </div>

    </body>
</html>
