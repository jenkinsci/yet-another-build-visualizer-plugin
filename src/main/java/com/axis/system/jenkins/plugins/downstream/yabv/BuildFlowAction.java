package com.axis.system.jenkins.plugins.downstream.yabv;

import static com.axis.system.jenkins.plugins.downstream.tree.TreeLaminator.layoutTree;

import com.axis.system.jenkins.plugins.downstream.cache.BuildCache;
import com.axis.system.jenkins.plugins.downstream.tree.Matrix;
import hudson.Extension;
import hudson.model.*;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;

/**
 * Produces Transient Actions for visualizing the flow of downstream builds.
 *
 * @author Gustaf Lundh (C) Axis 2018
 */
@SuppressWarnings("unused")
public class BuildFlowAction implements Action {
  private final Run target;

  private BuildFlowAction(Run run) {
    this.target = run;
  }

  private static Run getRootUpstreamBuild(@Nonnull Run build) {
    Run parentBuild;
    while ((parentBuild = getUpstreamBuild(build)) != null) {
      build = parentBuild;
    }
    return build;
  }

  private static Run getUpstreamBuild(@Nonnull Run build) {
    CauseAction causeAction = build.getAction(CauseAction.class);
    for (Cause cause : causeAction.getCauses()) {
      if (cause instanceof Cause.UpstreamCause) {
        Cause.UpstreamCause upstreamCause = (Cause.UpstreamCause) cause;
        Job upstreamJob =
            Jenkins.getInstance().getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
        if (upstreamJob == null) {
          continue;
        }
        return upstreamJob.getBuildByNumber(upstreamCause.getUpstreamBuild());
      }
    }
    return null;
  }

  public Run getTarget() {
    return target;
  }

  public Matrix buildMatrix() {
    if (target == null) {
      return null;
    }
    return layoutTree(
        getRootUpstreamBuild(target), b -> BuildCache.getCache().getDownstreamBuilds(b));
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return null;
  }

  @Override
  public String getIconFileName() {
    return null;
  }

  @Extension
  public static class BuildActionFactory extends TransientActionFactory<Run> {

    @Override
    public Class<Run> type() {
      return Run.class;
    }

    @Override
    public Collection<? extends Action> createFor(Run run) {
      return Collections.singleton(new BuildFlowAction(run));
    }
  }

  @Extension(ordinal = 1000)
  public static class ProjectActionFactory extends TransientActionFactory<Job> {

    @Override
    public Class<Job> type() {
      return Job.class;
    }

    @Override
    public Collection<? extends Action> createFor(@Nonnull Job target) {
      return Collections.singleton(new BuildFlowAction(target.getLastBuild()));
    }
  }
}
