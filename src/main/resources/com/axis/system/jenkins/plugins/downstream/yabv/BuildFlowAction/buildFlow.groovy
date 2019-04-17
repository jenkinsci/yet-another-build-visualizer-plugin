package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

import com.axis.system.jenkins.plugins.downstream.tree.Matrix
import com.axis.system.jenkins.plugins.downstream.yabv.NameNormalizer
import hudson.model.Item
import hudson.model.Job
import hudson.model.Queue
import hudson.model.Run

import static com.axis.system.jenkins.plugins.downstream.tree.Matrix.Arrow

link(rel: 'stylesheet',
    type: 'text/css',
    href: "${rootURL}/plugin/yet-another-build-visualizer/css/layout.css")

table(class: 'downstream-table', cellspacing: 0, cellpadding: 0) {
  Matrix matrix = my.buildMatrix()
  if (!matrix || (matrix.get().size() == 1 && matrix.get(0).size() == 1)) {
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

  tr {
    th(align: 'left', colspan: '100%') {
      h2('Build Flow')
    }
  }
  matrix.get().each { row ->
    tr {
      row.each { cell ->
        td {
          if (cell?.arrow) {
            drawArrow(cell.arrow)
          }
        }
        td {
          if (cell?.data) {
            drawCellData(cell.data, nameNormalizer)
          }
        }
      }
    }
  }
}

private void drawCellData(Object data, NameNormalizer nameNormalizer) {
  div(class: 'build-wrapper') {
    if (data instanceof Run) {
      drawBuildInfo(data, nameNormalizer)
    } else if (data instanceof Queue.Item) {
      drawQueueItemInfo(data, nameNormalizer)
    }
  }
}

private void drawBuildInfo(Run build, NameNormalizer nameNormalizer) {
  def color = build.iconColor
  def colorClasses = color.name().replace('_', ' ') + ' ' + (build == my.target ? 'SELECTED' : '')
  div(class: "build-info ${colorClasses}") {
    a(class: 'model-link inside', href: "${rootURL}/${build.url}") {
      span("${nameNormalizer.getNormalizedName(build.parent)} ${build.displayName}")
    }
  }
}

private void drawQueueItemInfo(Queue.Item item, NameNormalizer nameNormalizer) {
  div(class: 'build-info NOTBUILT ANIME') {
    a(class: 'model-link inside', href: "${rootURL}/${item.task.url}") {
      span("${nameNormalizer.getNormalizedName(item.task)} (Queued)")
    }
  }
}

private void drawArrow(Arrow arrow) {
  div(class: 'arrow-wrapper') {
    svg(viewBox: '0 0 100 100',
        height: '100%',
        width: '100%',
        style: 'display: block;',
        preserveAspectRatio: 'none') {
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
