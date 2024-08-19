package com.axis.system.jenkins.plugins.downstream.yabv;

public class BuildFlowOptions {
  private boolean showBuildHistory;
  private boolean showDurationInfo;
  private boolean showUpstreamBuilds;
  private boolean flattenView;
  private boolean showFullNames;
  private boolean showDescription;

  public BuildFlowOptions() {
    showBuildHistory = false;
    showDurationInfo = false;
    showUpstreamBuilds = false;
    flattenView = false;
    showFullNames = false;
    showDescription = false;
  }

  public BuildFlowOptions(
      boolean showBuildHistory,
      boolean showDurationInfo,
      boolean showUpstreamBuilds,
      boolean flattenView,
      boolean showFullNames,
      boolean showDescription) {
    this.showBuildHistory = showBuildHistory;
    this.showDurationInfo = showDurationInfo;
    this.showUpstreamBuilds = showUpstreamBuilds;
    this.flattenView = flattenView;
    this.showFullNames = showFullNames;
    this.showDescription = showDescription;
  }

  public boolean isShowBuildHistory() {
    return showBuildHistory;
  }

  public void setShowBuildHistory(boolean showBuildHistory) {
    this.showBuildHistory = showBuildHistory;
  }

  public boolean isShowDurationInfo() {
    return showDurationInfo;
  }

  public void setShowDurationInfo(boolean showDurationInfo) {
    this.showDurationInfo = showDurationInfo;
  }

  public boolean isShowUpstreamBuilds() {
    return showUpstreamBuilds;
  }

  public void setShowUpstreamBuilds(boolean showUpstreamBuilds) {
    this.showUpstreamBuilds = showUpstreamBuilds;
  }

  public boolean isFlattenView() {
    return flattenView;
  }

  public void setFlattenView(boolean flattenView) {
    this.flattenView = flattenView;
  }

  public boolean isShowFullNames() {
    return showFullNames;
  }

  public void setShowFullNames(boolean showFullNames) {
    this.showFullNames = showFullNames;
  }

  public boolean isShowDescription() {
    return showDescription;
  }

  public void setShowDescription(boolean showDescription) {
    this.showDescription = showDescription;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("BuildFlowOptions{");
    sb.append("showBuildHistory=").append(showBuildHistory);
    sb.append(", showDurationInfo=").append(showDurationInfo);
    sb.append(", showUpstreamBuilds=").append(showUpstreamBuilds);
    sb.append(", flattenView=").append(flattenView);
    sb.append(", showFullNames=").append(showFullNames);
    sb.append(", showDescription=").append(showDescription);
    sb.append('}');
    return sb.toString();
  }
}
