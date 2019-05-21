function setCookie(cname, cvalue) {
  var date = new Date();
  date.setDate(date.getDate() + 365);
  var expires = "expires=" + date.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function toggleDurationInfo() {
  var show = getCookie("yabv.showDurationInfo");
  show = (show === "true" ? show = "false" : show = "true")
  setCookie("yabv.showDurationInfo", show);
  displayDurationInfo(show);
}

function displayDurationInfo(show) {
  Array.from(document.getElementsByClassName("duration-info")).forEach(
    function(element, index, array) {
      element.style.display =  show === "true" ? 'inline-block' : 'none';
    }
  );
}

var show = getCookie("yabv.showDurationInfo");
displayDurationInfo(show);
