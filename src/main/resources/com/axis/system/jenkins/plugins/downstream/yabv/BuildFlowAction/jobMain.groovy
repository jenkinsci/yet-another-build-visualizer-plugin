package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

import com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction
import hudson.model.Run

Run lastCompletedBuild = my.target?.parent?.lastCompletedBuild
Run lastBuild = my.target?.parent?.lastBuild

if (BuildFlowAction.hasUpstreamOrDownstreamBuilds(my.target) ||
    BuildFlowAction.hasUpstreamOrDownstreamBuilds(lastCompletedBuild) ||
    BuildFlowAction.hasUpstreamOrDownstreamBuilds(lastBuild)) {
  h2('Build Flow')
  include(my, 'buildFlowJsCss.groovy')
}