package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

link(rel: 'stylesheet',
    type: 'text/css',
    href: "${rootURL}/plugin/yet-another-build-visualizer/css/layout.css")

div(id: 'build-flow-root', 'data-refresh-interval': System.getProperty('yabv.buildFlowRefreshInterval', '10000')) {
  div(id: 'build-flow-switches')
  div(id: 'build-flow-grid-holder') {
    noscript {
      include(my, 'buildFlow.groovy')
    }
  }
  script(src: "${rootURL}/plugin/yet-another-build-visualizer/scripts/render.js",
      type: 'text/javascript')
}
