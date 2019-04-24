setInterval(loadBuildFlow, buildFlowRefreshInterval);

function loadBuildFlow() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("downstream-table").outerHTML = this.responseText;
    }
  };
  xhttp.open("GET", "yabv/ajaxBuildFlow", true);
  xhttp.send();
}
