package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

import com.axis.system.jenkins.plugins.downstream.cache.BuildCache
import com.axis.system.jenkins.plugins.downstream.tree.Matrix
import com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowOptions
import com.axis.system.jenkins.plugins.downstream.yabv.NameNormalizer
import hudson.Util
import hudson.model.Item
import hudson.model.Job
import hudson.model.Queue
import hudson.model.Run

import static com.axis.system.jenkins.plugins.downstream.tree.Matrix.Arrow

div(id: 'build-flow-cache-refreshing-warning') {
  if (BuildCache.cache.isCacheRefreshing()) {
    span('⚠ Cache is still refreshing, the Build Flow graph may not be complete!')
  }
}

// To get rid of warnings where argument types could not be inferred
BuildFlowOptions options = my.buildFlowOptions

if (options.flattenView) {
  List<Set<Object>> itemsInFlow = my.getAllItemsInFlow(options.showBuildHistory ? 10 : 1)
  Job rootJob = my.rootUpstreamBuild?.parent
  List<Job> allJobs = itemsInFlow.flatten().collect { data ->
    getJob(data)
  }.unique().sort { a, b ->
    if (a == rootJob) { return -1 }
    if (b == rootJob) { return 1 }
    return a.fullDisplayName <=> b.fullDisplayName
  }
  div(id: 'build-flow-grid',
      style: "grid-auto-flow: column; grid-template-rows: repeat(${allJobs.size()}, auto);") {
    if (itemsInFlow.isEmpty()) {
      return
    }

    NameNormalizer nameNormalizer = getNameNormalizer(allJobs.toSet())

    allJobs.each { job ->
      drawJobInfo(job, nameNormalizer)
    }
    itemsInFlow.each { items ->
      allJobs.each { job ->
        div(style: 'display: flex; flex-direction: column; margin: 0.2em 0;') {
          items.findAll { item ->
            job == getJob(item)
          }.each { item ->
            drawCellData(item, nameNormalizer, options)
          }
        }
      }
    }
  }
} else {
  Matrix matrix = my.buildMatrix()
  div(id: 'build-flow-grid',
      style: "grid-template-columns: 0 0 repeat(${matrix.maxRowWidth * 2 - 1}, auto);") {
    if (matrix.isEmpty()) {
      return
    }

    Set<Job> jobs = matrix.cellDataAsSet.collect { data ->
      getJob(data)
    }.toSet()

    NameNormalizer nameNormalizer = getNameNormalizer(jobs)

    matrix.get().each { row ->
      div(style: 'grid-column-start: 1') { }
      row.each { Matrix.Entry cell ->
        drawArrow(cell?.arrow)
        drawCellData(cell?.data, nameNormalizer, options)
      }
    }
  }
}

private static Job getJob(Object data) {
  if (data instanceof Run) {
    return data.parent
  } else if (data instanceof Queue.Item && data.task instanceof Job) {
    return (Job) data.task
  }
  return null
}

private static NameNormalizer getNameNormalizer(Set<Job> jobs) {
  return new NameNormalizer(jobs, {
    it.displayName
  }, {
    it instanceof Item ? it.parent : null
  })
}

private void drawCellData(Object data, NameNormalizer nameNormalizer, BuildFlowOptions options) {
  if (data instanceof Run) {
    drawBuildInfo(data, nameNormalizer, options)
  } else if (data instanceof Queue.Item) {
    drawQueueItemInfo(data, nameNormalizer, options)
  } else {
    div { }
  }
}

private static String getCssColorFromBuild(Run build) {
  return build.iconColor.name().replace('_', ' ')
}

private void drawBuildInfo(Run build, NameNormalizer nameNormalizer, BuildFlowOptions options) {
  def colorClasses = getCssColorFromBuild(build) +
      (build == my.target ? ' SELECTED' : '') +
      (options.flattenView ? ' FLAT' : '')

  div(class: "build-info ${colorClasses}") {
    a(class: 'model-link inside', href: "${rootURL}/${build.url}") {
      if (options.flattenView) {
        span("${build.displayName}")
      } else {
        span("${nameNormalizer.getNormalizedName(build.parent)} ${build.displayName}")
      }
    }
    if (options.showDurationInfo) {
      span(class: 'duration-info', build.durationString)
    }
    if (options.showBuildHistory && !my.buildFlowOptions.flattenView) {
      div(class: 'build-flow-build-history') {
        currentBuild = build.previousBuild
        for (int i = 0; i < 5 && currentBuild != null; i++) {
          a(href: "${rootURL}/${currentBuild.url}") {
            def currentColor = currentBuild.iconColor
            div(class: 'build-flow-build-history-dot build-info ' +
                currentColor.name().replace('_', ' '),
                tooltip: Util.xmlEscape(currentBuild.displayName))
          }
          currentBuild = currentBuild.previousBuild
        }
      }
    }
  }
}

private void drawQueueItemInfo(Queue.Item item, NameNormalizer nameNormalizer, BuildFlowOptions
    options) {
  div(class: "build-info NOTBUILT ANIME ${options.flattenView ? 'FLAT' : ''}") {
    a(class: 'model-link inside', href: "${rootURL}/${item.task.url}") {
      if (options.flattenView) {
        span('Queued')
      } else {
        span("${nameNormalizer.getNormalizedName(item.task)} (Queued)")
      }
    }
    if (options.showDurationInfo) {
      span(class: 'duration-info', item.inQueueForString)
    }
  }
}

private void drawJobInfo(Job job, NameNormalizer nameNormalizer) {
  def colorClasses = getCssColorFromBuild(job.lastBuild)
  div(class: "job-info ${colorClasses}") {
    a(class: 'model-link inside', href: "${rootURL}/${job.url}") {
      span("${nameNormalizer.getNormalizedName(job)}")
    }
  }
}

private void drawArrow(Arrow arrow) {
  if (arrow == null) {
    div { }
    return
  }
  div(class: 'arrow-wrapper') {
    svg(viewBox: '0 0 100 100',
        preserveAspectRatio: 'none',
        width: '100%',
        height: '100%') {
      def pathDefinition
      switch (arrow) {
        case Arrow.NS:
          pathDefinition = 'M 50 0 L 50 100'
          break
        case Arrow.WE:
          pathDefinition = 'M 0 50 L 100 50'
          break
        case Arrow.WES:
          pathDefinition = 'M 0 50 L 100 50 M 0 50 Q 50 50 50 100'
          break
        case Arrow.NES:
          pathDefinition = 'M 50 0 L 50 100 M 50 0 Q 50 50 100 50'
          break
        case Arrow.NE:
          pathDefinition = 'M 50 0 Q 50 50 100 50'
          break
        default:
          pathDefinition = ''
          break
      }
      path(d: pathDefinition,
          'vector-effect': 'non-scaling-stroke',
          'stroke-width': '2',
          stroke: '#333',
          fill: 'transparent')
    }
  }
}
