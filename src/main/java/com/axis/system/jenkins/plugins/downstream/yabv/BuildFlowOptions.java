package com.axis.system.jenkins.plugins.downstream.yabv;

public class BuildFlowOptions {
  private boolean showBuildHistory;
  private boolean showDurationInfo;
  private boolean showUpstreamBuilds;
  private boolean flattenView;
  private boolean showFullNames;

  public BuildFlowOptions() {
    showBuildHistory = false;
    showDurationInfo = false;
    showUpstreamBuilds = false;
    flattenView = false;
    showFullNames = false;
  }

  public BuildFlowOptions(
      boolean showBuildHistory,
      boolean showDurationInfo,
      boolean showUpstreamBuilds,
      boolean flattenView,
      boolean showFullNames) {
    this.showBuildHistory = showBuildHistory;
    this.showDurationInfo = showDurationInfo;
    this.showUpstreamBuilds = showUpstreamBuilds;
    this.flattenView = flattenView;
    this.showFullNames = showFullNames;
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

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("BuildFlowOptions{");
    sb.append("showBuildHistory=").append(showBuildHistory);
    sb.append(", showDurationInfo=").append(showDurationInfo);
    sb.append(", showUpstreamBuilds=").append(showUpstreamBuilds);
    sb.append(", flattenView=").append(flattenView);
    sb.append(", showFullNames=").append(showFullNames);
    sb.append('}');
    return sb.toString();
  }
}
