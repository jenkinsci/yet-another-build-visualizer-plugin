package com.axis.system.jenkins.plugins.downstream.yabv;

public class BuildFlowOptions {
  private boolean showBuildHistory;
  private boolean showDurationInfo;

  public BuildFlowOptions() {
    showBuildHistory = false;
    showDurationInfo = false;
  }

  public BuildFlowOptions(boolean showBuildHistory, boolean showDurationInfo) {
    this.showBuildHistory = showBuildHistory;
    this.showDurationInfo = showDurationInfo;
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

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("BuildFlowOptions{");
    sb.append("showBuildHistory=").append(showBuildHistory);
    sb.append(", showDurationInfo=").append(showDurationInfo);
    sb.append('}');
    return sb.toString();
  }
}
