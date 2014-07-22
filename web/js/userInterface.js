$(document).ready(function() {
    
    if(count > 0) {
        $("#promptDiv").show();
        $("#uploadForm").hide();
        $("#backBtn").hide();
    } else {
        $("#promptDiv").hide();
        $("#uploadForm").show();
        $("#backBtn").hide();
    }
    
    $("#uploadButton").click(function() {
        $("#uploadDiv").slideToggle();
    });
    
    $("#processButton").click(function() {
        $("#processDiv").slideToggle();
    });
    
    $("#exportButton").click(function() {
        $("#exportDiv").slideToggle();
    });
    
    $("#addMoreBtn").click(function() {
        $("#promptDiv").hide();
        $("#uploadForm").show();
        $("#backBtn").show();
    });
    
    $("#backBtn").click(function() {
        $("#promptDiv").show();
        $("#uploadForm").hide();
        $("#backBtn").hide();
    });
    
});

function enableButton(token) {
    $("#fb_at").val(token);
    $("#submitButton").removeAttr("disabled");
    
    $("#processDetailsBtn").attr("onClick", "location.href='/Plutarch/main/download?token=" + token + "'");
    $("#resumeBtn").attr("onClick", "location.href='/Plutarch/main/resume?token=" + token + "'");
    $("#processErrorsBtn").attr("onClick", "location.href='/Plutarch/main/process?token=" + token + "'");
}