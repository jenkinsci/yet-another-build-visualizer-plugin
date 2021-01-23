package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

if (my.shouldDisplayBuildFlow()) {
  h2('Build Flow')
  include(my, 'buildFlowJsCss.groovy')
}
