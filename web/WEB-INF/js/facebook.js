var token = "";
FB.init({
    appId: '1444675909116090',
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
    document.getElementById("fb_at").value = token;
    document.getElementById("submitButton").disabled = false;
}