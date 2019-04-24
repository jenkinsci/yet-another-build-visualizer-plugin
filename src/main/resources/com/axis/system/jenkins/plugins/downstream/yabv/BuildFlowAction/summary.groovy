package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

link(rel: 'stylesheet',
    type: 'text/css',
    href: "${rootURL}/plugin/yet-another-build-visualizer/css/layout.css")

def t = namespace(lib.JenkinsTagLib.class)

t.summary(icon: "/plugin/yet-another-build-visualizer/icons/summary-icon.png") {
  include(my, 'buildFlow.groovy')
}

int buildFlowRefreshInterval =
    Integer.parseInt(System.getProperty('yabv.buildFlowRefreshInterval', '10000'))

script("buildFlowRefreshInterval=${buildFlowRefreshInterval}")
script(src: "${rootURL}/plugin/yet-another-build-visualizer/scripts/ajax-build-flow.js",
    type: "text/javascript")
