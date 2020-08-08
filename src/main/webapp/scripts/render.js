function loadBuildFlow() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      var currentGrid = document.getElementById("build-flow-grid-holder");
      var newGrid = currentGrid.cloneNode(false)
      newGrid.innerHTML = this.responseText;
      currentGrid.parentNode.replaceChild(newGrid, currentGrid)
      Behaviour.applySubtree(newGrid);
    }
  };
  var queryParams = ["showDurationInfo", "showBuildHistory", "showUpstreamBuilds"].map(function(name) {
    return `${name}=${isOptionActive(name)}`
  });
  xhttp.open("GET", `yabv/buildFlow?${queryParams.join("&")}`, true);
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

function isOptionActive(option) {
  return getCookie("yabv." + option) === "true";
}
function toggleOption(switchA, option) {
  setCookie("yabv." + option, isOptionActive(option) ? "false" : "true");
  setSwitchActiveState(switchA, option);
  loadBuildFlow();
}

function setSwitchActiveState(switchA, option) {
  if (isOptionActive(option)) {
    switchA.classList.add('ACTIVE');
  } else {
    switchA.classList.remove('ACTIVE');
  }
}

function createOptionSwitch(name, option) {
  var switchA = document.createElement("a");
  switchA.onclick = function() { toggleOption(switchA, option); return false; };
  switchA.classList.add("build-flow-switch");
  setSwitchActiveState(switchA, option);
  switchA.href = "#";

  var switchSpan = document.createElement("span");
  switchSpan.innerHTML = name;

  switchA.appendChild(switchSpan);
  document.getElementById("build-flow-switches").appendChild(switchA);
}

createOptionSwitch("Toggle Time", "showDurationInfo");
createOptionSwitch("Toggle Build History", "showBuildHistory");
createOptionSwitch("Toggle Upstream Builds", "showUpstreamBuilds");

if (typeof buildFlowRefreshInterval !== 'undefined' &&
  Number.isInteger(parseInt(buildFlowRefreshInterval)) &&
  buildFlowRefreshInterval > 0) {
  // For performance reasons, do not allow intervals smaller than 500.
  setInterval(loadBuildFlow, Math.max(buildFlowRefreshInterval, 500));
}

loadBuildFlow();
