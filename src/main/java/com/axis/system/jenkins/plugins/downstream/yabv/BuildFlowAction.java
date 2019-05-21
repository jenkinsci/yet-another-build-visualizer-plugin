package com.axis.system.jenkins.plugins.downstream.yabv;

import static com.axis.system.jenkins.plugins.downstream.tree.TreeLaminator.layoutTree;

import com.axis.system.jenkins.plugins.downstream.cache.BuildCache;
import com.axis.system.jenkins.plugins.downstream.tree.Matrix;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Job;
import hudson.model.Queue;
import hudson.model.Run;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

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
    if (causeAction == null) {
      return null;
    }
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

  public boolean hasUpstreamOrDownstreamBuilds() {
    return BuildCache.getCache().getDownstreamBuilds(target).size() > 0
        || BuildCache.getDownstreamQueueItems(target).size() > 0
        || getUpstreamBuild(target) != null;
  }

  public Run getTarget() {
    return target;
  }

  public Matrix buildMatrix() {
    if (target == null) {
      return new Matrix();
    }
    final Queue.Item[] items = Queue.getInstance().getItems();
    return layoutTree(
        (Object) getRootUpstreamBuild(target),
        b -> {
          List<Object> result = new ArrayList<>();
          if (b instanceof Run) {
            result.addAll(BuildCache.getCache().getDownstreamBuilds((Run) b));
            result.addAll(BuildCache.getDownstreamQueueItems(items, (Run) b));
          }
          return result;
        });
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return "yabv";
  }

  @Override
  public String getIconFileName() {
    return null;
  }

  public void doBuildFlow(StaplerRequest req, StaplerResponse rsp)
      throws IOException, ServletException {
    rsp.setContentType("text/html;charset=UTF-8");
    req.getView(this, "buildFlow.groovy").forward(req, rsp);
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
