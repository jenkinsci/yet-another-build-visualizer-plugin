package com.axis.system.jenkins.plugins.downstream.yabv;

import com.axis.system.jenkins.plugins.downstream.cache.BuildCache;
import com.axis.system.jenkins.plugins.downstream.tree.Matrix;
import com.axis.system.jenkins.plugins.downstream.tree.TreeLaminator.ChildrenFunction;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Api;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Job;
import hudson.model.Queue;
import hudson.model.Run;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.Nonnull;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.axis.system.jenkins.plugins.downstream.tree.TreeLaminator.layoutTree;

/**
 * Produces Transient Actions for visualizing the flow of downstream builds.
 *
 * @author Gustaf Lundh (C) Axis 2018
 */
@SuppressWarnings("unused")
@ExportedBean
public class BuildFlowAction implements Action {
  private final Run target;
  private final BuildFlowOptions buildFlowOptions;

  private BuildFlowAction(Run run) {
    this.target = run;
    this.buildFlowOptions = new BuildFlowOptions();
  }

  private static final ChildrenFunction getChildrenFunc() {
    final Queue.Item[] items = Queue.getInstance().getItems();
    return build -> {
      List<Object> result = new ArrayList<>();
      if (build instanceof Run) {
        result.addAll(BuildCache.getCache().getDownstreamBuilds((Run) build));
        result.addAll(BuildCache.getDownstreamQueueItems(items, (Run) build));
      }
      return result;
    };
  }

  public Run getRootUpstreamBuild() {
    if (target == null) {
      return null;
    }
    return buildFlowOptions.isShowUpstreamBuilds() ? getRootUpstreamBuild(target) : target;
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
        // We want to ignore rebuilds, rebuilds have the same parent as
        // original build, see BuildCache#updateCache().
        if (upstreamJob == null || build.getParent() == upstreamJob) {
          continue;
        }
        return upstreamJob.getBuildByNumber(upstreamCause.getUpstreamBuild());
      }
    }
    return null;
  }

  public BuildFlowOptions getBuildFlowOptions() {
    return buildFlowOptions;
  }

  @Exported(visibility = 1)
  public boolean isAnyBuildOngoing() {
    return target != null
        && isChildrenStillBuilding(getRootUpstreamBuild(), getChildrenFunc());
  }

  private static boolean isChildrenStillBuilding(Object current, ChildrenFunction children) {
    Iterator childIter = children.children(current).iterator();
    while (childIter.hasNext()) {
      Object child = childIter.next();
      if (child instanceof Queue.Item) {
        return true;
      }
      if (child instanceof Run && ((Run) child).isBuilding()) {
        return true;
      }
      return isChildrenStillBuilding(child, children);
    }
    return false;
  }

  @Exported(visibility = 1)
  public boolean isCacheRefreshing() {
    return BuildCache.getCache().isCacheRefreshing();
  }

  public boolean shouldDisplayBuildFlow() {
    return target != null
        && (hasUpstreamOrDownstreamBuilds(target)
            || hasUpstreamOrDownstreamBuilds(target.getParent().getLastCompletedBuild())
            || hasUpstreamOrDownstreamBuilds(target.getParent().getLastBuild()));
  }

  public static boolean hasUpstreamOrDownstreamBuilds(Run target) {
    if (target == null) {
      return false;
    }
    return BuildCache.getCache().getDownstreamBuilds(target).size() > 0
        || BuildCache.getDownstreamQueueItems(target).size() > 0
        || getUpstreamBuild(target) != null;
  }

  public Run getTarget() {
    return target;
  }

  public Matrix buildMatrix() {
    Run root = getRootUpstreamBuild();
    if (root == null) {
      return new Matrix();
    }
    return layoutTree(root, getChildrenFunc());
  }

  /**
   * Returns all items in the build flow, populated from the root. Which target is root depends on
   * {@link BuildFlowOptions#isShowUpstreamBuilds()}.
   *
   * @param lookBack number of historic build flows to fetch, based on the root target's previous
   *     builds.
   * @return A list of sets of Objects. Each set represents all items in a flow, starting from the
   *     root. The first set is calculated from the root, the next set is from the root target's
   *     previous build and so on.
   */
  public List<Set<Object>> getAllItemsInFlow(int lookBack) {
    Run root = getRootUpstreamBuild();
    if (root == null) {
      return Collections.emptyList();
    }
    List<Set<Object>> result = new ArrayList<>();
    for (; lookBack > 0 && root != null; lookBack--) {
      Set<Object> itemsInFlow = getAllDownstreamItems(root);
      itemsInFlow.add(root);
      result.add(itemsInFlow);
      root = root.getPreviousBuild();
    }
    return result;
  }

  private static Set<Object> getAllDownstreamItems(Run current) {
    Set<Object> resultSet = new HashSet<>();
    addAllDownstreamItems(resultSet, current, getChildrenFunc());
    return resultSet;
  }

  private static void addAllDownstreamItems(
      Set<Object> resultSet, Object current, ChildrenFunction children) {
    Iterator childIter = children.children(current).iterator();
    while (childIter.hasNext()) {
      Object child = childIter.next();
      resultSet.add(child);
      addAllDownstreamItems(resultSet, child, children);
    }
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

  public void doBuildFlow(StaplerRequest2 req, StaplerResponse2 rsp)
      throws IOException, ServletException {
    buildFlowOptions.setShowDescription(
        Boolean.parseBoolean(req.getParameter("showDescription")));
    buildFlowOptions.setShowDurationInfo(
        Boolean.parseBoolean(req.getParameter("showDurationInfo")));
    buildFlowOptions.setShowBuildHistory(
        Boolean.parseBoolean(req.getParameter("showBuildHistory")));
    buildFlowOptions.setShowUpstreamBuilds(
        Boolean.parseBoolean(req.getParameter("showUpstreamBuilds")));
    buildFlowOptions.setFlattenView(Boolean.parseBoolean(req.getParameter("flattenView")));
    buildFlowOptions.setShowFullNames(Boolean.parseBoolean(req.getParameter("showFullNames")));
    rsp.setContentType("text/html;charset=UTF-8");
    req.getView(this, "buildFlow.groovy").forward(req, rsp);
  }

  /** Remote API access. */
  public Api getApi() {
    return new Api(this);
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
      Run build = target.getLastBuild();
      if (build == null) {
        return Collections.emptyList();
      } else {
        return Collections.singleton(new BuildFlowAction(build));
      }
    }
  }
}
