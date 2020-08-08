package com.axis.system.jenkins.plugins.downstream.yabv;

public class BuildFlowOptions {
  private boolean showBuildHistory;
  private boolean showDurationInfo;
  private boolean showUpstreamBuilds;

  public BuildFlowOptions() {
    showBuildHistory = false;
    showDurationInfo = false;
    showUpstreamBuilds = false;
  }

  public BuildFlowOptions(
      boolean showBuildHistory, boolean showDurationInfo, boolean showUpstreamBuilds) {
    this.showBuildHistory = showBuildHistory;
    this.showDurationInfo = showDurationInfo;
    this.showUpstreamBuilds = showUpstreamBuilds;
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

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("BuildFlowOptions{");
    sb.append("showBuildHistory=").append(showBuildHistory);
    sb.append(", showDurationInfo=").append(showDurationInfo);
    sb.append(", showUpstreamBuilds=").append(showUpstreamBuilds);
    sb.append('}');
    return sb.toString();
  }
}
