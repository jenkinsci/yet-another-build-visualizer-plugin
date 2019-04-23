package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

link(rel: 'stylesheet',
    type: 'text/css',
    href: "${rootURL}/plugin/yet-another-build-visualizer/css/layout.css")

include(my, 'buildFlow.groovy')

script(src: "${rootURL}/plugin/yet-another-build-visualizer/scripts/ajax-build-flow.js",
    type: "text/javascript")
