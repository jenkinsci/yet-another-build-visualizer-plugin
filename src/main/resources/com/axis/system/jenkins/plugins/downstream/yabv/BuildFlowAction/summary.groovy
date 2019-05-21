package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

def t = namespace(lib.JenkinsTagLib)

t.summary(icon: '/plugin/yet-another-build-visualizer/icons/summary-icon.png') {
  include(my, 'buildFlowRefresh.groovy')
}
