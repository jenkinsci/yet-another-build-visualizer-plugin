package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

base(href: '..')

def st = namespace('jelly:stapler')
st.contentType(value:'text/html;charset=UTF-8')

include(my, 'buildFlowJsCss.groovy')
