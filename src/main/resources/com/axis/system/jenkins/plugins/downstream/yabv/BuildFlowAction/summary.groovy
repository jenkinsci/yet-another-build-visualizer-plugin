package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

def t = namespace(lib.JenkinsTagLib)

if (my.hasUpstreamOrDownstreamBuilds()) {
  t.summary(icon: '/plugin/yet-another-build-visualizer/icons/summary-icon.png') {
    include(my, 'buildFlowJsCss.groovy')
  }
}
