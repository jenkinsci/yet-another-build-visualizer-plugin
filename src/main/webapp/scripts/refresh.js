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
      var currentGrid = document.getElementById("downstream-grid");
      var template = document.createElement("template");
      template.innerHTML = this.responseText;
      var newGrid = template.content.firstChild;
      currentGrid.parentNode.replaceChild(newGrid, currentGrid);
      Behaviour.applySubtree(newGrid);
    }
  };
  xhttp.open("GET", "yabv/buildFlow", true);
  xhttp.send();
}
