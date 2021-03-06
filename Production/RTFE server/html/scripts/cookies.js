
function setCookie(name, api, building_name) {
    var d = new Date;
    d.setTime(d.getTime() + 24*60*60*1000);
    document.cookie =  "email =" + name + ";path=/;expires=" + d.toGMTString() + ";path=/;";
    document.cookie =  "apiKey=" + api + ";path=/;expires=" + d.toGMTString() + ";path=/;";
    document.cookie =  "building_name=" + building_name+";expires=" + d.toGMTString() + ";path=/;"
}

function createCookie(name, value) {
    var d = new Date;
    d.setTime(d.getTime() + 24*60*60*1000);
    document.cookie =  name+"=" + value + ";path=/;expires=" + d.toGMTString() + ";path=/;";
}

function changeCookie(name, value)
{
    deleteCookie(name);
    createCookie(name,value);
}

function getCookie(name) {
    var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return v ? v[2] : null;
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}


function putCookie(form)
//this should set the UserName cookie to the proper value;
{
    setCookie("userName", form[0].userEmail.value);

    return true;
}
function deleteCookie(name) {
    createCookie(name,"",-1);
}
