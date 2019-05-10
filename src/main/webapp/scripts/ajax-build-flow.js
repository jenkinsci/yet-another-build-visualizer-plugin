if (typeof buildFlowRefreshInterval !== 'undefined' &&
  Number.isInteger(parseInt(buildFlowRefreshInterval)) &&
  buildFlowRefreshInterval > 0) {
  // For performance reasons, do not allow intervals smaller than 500.
  setInterval(loadBuildFlow, Math.max(buildFlowRefreshInterval, 500));
}

function loadBuildFlow() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("downstream-grid").outerHTML = this.responseText;
    }
  };
  xhttp.open("GET", "yabv/ajaxBuildFlow", true);
  xhttp.send();
}
