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
    span("⚠ Cache is still refreshing, the Build Flow graph may not be complete!")
  }
}

Matrix matrix = my.buildMatrix()
div(id: 'build-flow-grid',
    style: "grid-template-columns: repeat(${matrix.getMaxRowWidth() * 2}, auto);") {
  if (matrix.isEmpty()) {
    return
  }
  Set<Job> jobs = matrix.cellDataAsSet.collect { data ->
    if (data instanceof Run) {
      data.parent
    } else if (data instanceof Queue.Item) {
      data.task
    }
  }.toSet()

  NameNormalizer nameNormalizer = new NameNormalizer(jobs,
      { it.displayName },
      { it instanceof Item ? it.parent : null }
  )

  matrix.get().each { row ->
    row.each { cell ->
      if (cell?.arrow) {
        drawArrow(cell.arrow)
      }
      if (cell?.data) {
        drawCellData(cell.data, nameNormalizer, my.getBuildFlowOptions())
      }
      div() {}
    }
  }
}

private void drawCellData(Object data, NameNormalizer
    nameNormalizer, BuildFlowOptions options) {
  if (data instanceof Run) {
    drawBuildInfo(data, nameNormalizer, options)
  } else if (data instanceof Queue.Item) {
    drawQueueItemInfo(data, nameNormalizer)
  }
}

private void drawBuildInfo(Run build, NameNormalizer
    nameNormalizer, BuildFlowOptions options) {
  def color = build.iconColor
  def colorClasses = color.name().replace('_', ' ') + ' ' + (build == my.target ? 'SELECTED' : '')
  div(class: "build-info ${colorClasses}") {
    a(class: 'model-link inside', href: "${rootURL}/${build.url}") {
      span("${nameNormalizer.getNormalizedName(build.parent)} ${build.displayName}")
    }
    if (options.showDurationInfo) {
      span(class: 'duration-info', build.durationString)
    }
    if (options.showBuildHistory) {
      div(class: "build-flow-build-history") {
        currentBuild = build.previousBuild
        for (int i = 0; i < 5 && currentBuild != null; i++) {
          a(href: "${rootURL}/${currentBuild.url}") {
            def currentColor = currentBuild.iconColor
            div(class: "build-flow-build-history-dot build-info ${currentColor.name().replace('_', ' ')}",
                tooltip: Util.xmlEscape(currentBuild.displayName))
          }
          currentBuild = currentBuild.previousBuild
        }
      }
    }
  }
}

private void drawQueueItemInfo(Queue.Item item,
                               NameNormalizer nameNormalizer) {
  div(class: 'build-info NOTBUILT ANIME') {
    a(class: 'model-link inside', href: "${rootURL}/${item.task.url}") {
      span("${nameNormalizer.getNormalizedName(item.task)} (Queued)")
    }
  }
}

private void drawArrow(Arrow arrow) {
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
          'stroke-width': 2,
          stroke: '#333',
          fill: 'transparent')
    }
  }
}
