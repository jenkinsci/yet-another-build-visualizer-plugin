package com.axis.system.jenkins.plugins.downstream.yabv.BuildFlowAction

import com.axis.system.jenkins.plugins.downstream.tree.Matrix
import com.axis.system.jenkins.plugins.downstream.yabv.NameNormalizer
import hudson.model.Item
import hudson.model.Job
import hudson.model.Queue
import hudson.model.Run

import static com.axis.system.jenkins.plugins.downstream.tree.Matrix.Arrow

Matrix matrix = my.buildMatrix()
div(id: 'downstream-grid',
    style: "grid-template-columns: repeat(${matrix.getMaxRowWidth() * 2}, auto);") {
  if (matrix.isEmpty() || matrix.numberOfCells == 1) {
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

  div(style: 'grid-column: 1 / -1') {
    h2('Build Flow')
    span(style: "font-size: 0.7em; ",
        onclick: 'toggleDurationInfo();',
        'Toggle Build Execution Time')
  }
  CssGridCoordinates gridCoords = new CssGridCoordinates()
  matrix.get().each { row ->
    gridCoords.row++
    gridCoords.col = 1
    row.each { cell ->
      if (cell?.arrow) {
        drawArrow(gridCoords, cell.arrow)
      }
      gridCoords.col++
      if (cell?.data) {
        drawCellData(gridCoords, cell.data, nameNormalizer)
      }
      gridCoords.col++
    }
  }
}

script(src: "${rootURL}/plugin/yet-another-build-visualizer/scripts/actions.js",
    type: 'text/javascript')

private void drawCellData(CssGridCoordinates gridCoords, Object data, NameNormalizer
    nameNormalizer) {
  if (data instanceof Run) {
    drawBuildInfo(gridCoords, data, nameNormalizer)
  } else if (data instanceof Queue.Item) {
    drawQueueItemInfo(gridCoords, data, nameNormalizer)
  }
}

private void drawBuildInfo(CssGridCoordinates gridCoords, Run build, NameNormalizer
    nameNormalizer) {
  def color = build.iconColor
  def colorClasses = color.name().replace('_', ' ') + ' ' + (build == my.target ? 'SELECTED' : '')
  div(class: "build-info ${colorClasses}",
      style: gridCoords.cssStyleString) {
    a(class: 'model-link inside', href: "${rootURL}/${build.url}") {
      span("${nameNormalizer.getNormalizedName(build.parent)} ${build.displayName}")
    }
    br()
    span(class: 'duration-info', build.durationString)
  }
}

private void drawQueueItemInfo(CssGridCoordinates gridCoords,
                               Queue.Item item,
                               NameNormalizer nameNormalizer) {
  div(class: 'build-info NOTBUILT ANIME',
      style: gridCoords.cssStyleString) {
    a(class: 'model-link inside', href: "${rootURL}/${item.task.url}") {
      span("${nameNormalizer.getNormalizedName(item.task)} (Queued)")
    }
  }
}

private void drawArrow(CssGridCoordinates gridCoords, Arrow arrow) {
  div(class: 'arrow-wrapper',
      style: gridCoords.cssStyleString) {
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

class CssGridCoordinates {
  int col = 1
  int row = 1

  String getCssStyleString() {
    "grid-row-start: ${row}; grid-column-start: ${col}"
  }
}