package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

def t = namespace(lib.JenkinsTagLib)

if (my.shouldDisplayBuildFlow()) {
  t.summary(icon: 'symbol-graph-outline plugin-yet-another-build-visualizer') {
    include(my, 'buildFlowJsCss.groovy')
  }
}
