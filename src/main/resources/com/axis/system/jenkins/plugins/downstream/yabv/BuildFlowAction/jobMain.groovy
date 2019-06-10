package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

import hudson.model.Run

Run lastCompletedBuild = my.target?.parent?.lastCompletedBuild
Run lastBuild = my.target?.parent?.lastBuild

if (my.hasUpstreamOrDownstreamBuilds(my.target) ||
    my.hasUpstreamOrDownstreamBuilds(lastCompletedBuild) ||
    my.hasUpstreamOrDownstreamBuilds(lastBuild)) {
  h2('Build Flow')
  include(my, 'buildFlowJsCss.groovy')
}