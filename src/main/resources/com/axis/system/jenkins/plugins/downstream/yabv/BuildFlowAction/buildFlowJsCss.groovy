package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

link(rel: 'stylesheet',
    type: 'text/css',
    href: "${rootURL}/plugin/yet-another-build-visualizer/css/layout.css")

div(id: 'build-flow') {
  div(id: 'build-flow-switches')
  div(id: 'downstream-grid') {

  }
  script("buildFlowRefreshInterval='${System.getProperty('yabv.buildFlowRefreshInterval', '10000')}'")
  script(src: "${rootURL}/plugin/yet-another-build-visualizer/scripts/render.js",
      type: 'text/javascript')
  noscript() {
    include(my, 'buildFlow.groovy')
  }
}
