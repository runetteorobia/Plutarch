$(document).ready(function() {
    $.ajaxSetup({cache: true});
    $.getScript('//connect.facebook.net/en_UK/all.js', function() {
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
                enableButton(token);
            } else {
                console.log('User cancelled login or did not fully authorize.');
            }
        }, {scope: ''});
}   );
});
