function loadBuildFlow() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      var currentGrid = document.getElementById("build-flow-grid");
      var template = document.createElement("template");
      template.innerHTML = this.responseText;
      var newGrid = template.content.firstChild;
      currentGrid.parentNode.replaceChild(newGrid, currentGrid);
      Behaviour.applySubtree(newGrid);
    }
  };
  var showDurationInfo = getCookie("yabv.showDurationInfo");
  var showBuildHistory = getCookie("yabv.showBuildHistory");
  xhttp.open("GET",
    `yabv/buildFlow?showDurationInfo=${showDurationInfo}&showBuildHistory=${showBuildHistory}`,
    true);
  xhttp.send();
}

function setCookie(name, value) {
  var date = new Date();
  date.setDate(date.getDate() + 365);
  var expires = "expires=" + date.toUTCString();
  document.cookie = name + "=" + value + ";" + expires + ";path=/";
}

function getCookie(name) {
    var value = document.cookie.match('(^|[^;]+)\\s*' + name + '\\s*=\\s*([^;]+)');
    return value ? value.pop() : '';
}

function toggleOption(name) {
  var show = getCookie("yabv." + name);
  show = (show === "true" ? show = "false" : show = "true")
  setCookie("yabv." + name, show);
  loadBuildFlow();
}

function createOptionSwitch(name, option) {
  var switchA = document.createElement("a");
  switchA.onclick = function() { toggleOption(option); return false; };
  switchA.classList.add("build-flow-switch");
  switchA.href = "#";

  var switchSpan = document.createElement("span");
  switchSpan.innerHTML = name;

  switchA.appendChild(switchSpan);
  document.getElementById("build-flow-switches").appendChild(switchA);
}

createOptionSwitch("Toggle Time", "showDurationInfo");
createOptionSwitch("Toggle Build History", "showBuildHistory");

if (typeof buildFlowRefreshInterval !== 'undefined' &&
  Number.isInteger(parseInt(buildFlowRefreshInterval)) &&
  buildFlowRefreshInterval > 0) {
  // For performance reasons, do not allow intervals smaller than 500.
  setInterval(loadBuildFlow, Math.max(buildFlowRefreshInterval, 500));
}

loadBuildFlow();
