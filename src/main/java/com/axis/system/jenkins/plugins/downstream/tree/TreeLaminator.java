package com.axis.system.jenkins.plugins.downstream.tree;

import static com.axis.system.jenkins.plugins.downstream.tree.Matrix.Arrow;
import static com.axis.system.jenkins.plugins.downstream.tree.Matrix.Entry;

import java.util.Iterator;

/** Layouts a data graph and "laminates" it into a matrix of rows, cols. */
public class TreeLaminator {
  public static <T> Matrix<T> layoutTree(T rootNode, ChildrenFunction<T> children) {
    Matrix<T> matrix = new Matrix<>();
    layoutTree(rootNode, children, matrix, 0, 0, Arrow.NONE);
    return matrix;
  }

  private static <T> void layoutTree(
      T node, ChildrenFunction<T> children, Matrix<T> matrix, int row, int col, Arrow arrow) {
    matrix.put(row, col, new Entry<>(arrow, node));
    col++;
    boolean firstChild = true;
    Iterator<? extends T> childIter = children.children(node).iterator();
    while (childIter.hasNext()) {
      T child = childIter.next();
      if (firstChild) {
        // The first child of node can either be "┳" (have siblings) or "━" (have no siblings)
        arrow = childIter.hasNext() ? Arrow.WES : Arrow.WE;
      } else {
        // Non-first children of node will either be "┣" (middle) or "┗" (end)
        arrow = childIter.hasNext() ? Arrow.NES : Arrow.NE;
        // Skip rows until we can fit our sub-tree
        while (!shallowFit(row, col, child, children, matrix)) {
          // We wont fit our sub-tree. Try next line and mark this cell as "┃"
          matrix.put(row, col, new Entry<>(Arrow.NS));
          row++;
        }
      }
      layoutTree(child, children, matrix, row, col, arrow);
      firstChild = false;
      row++;
    }
  }

  protected static <T> int getShallowDepth(T node, ChildrenFunction<T> childNodes) {
    int depth = 1;
    Iterator<? extends T> iter = childNodes.children(node).iterator();
    while (iter.hasNext()) {
      iter = childNodes.children(iter.next()).iterator();
      depth++;
    }
    return depth;
  }

  @SuppressWarnings("WeakerAccess")
  protected static <T> boolean shallowFit(
      int row, int pos, T node, ChildrenFunction<T> childNodes, Matrix<T> matrix) {
    if (row == matrix.get().size()) return true;
    int depth = getShallowDepth(node, childNodes);
    for (int i = pos; i < matrix.get(row).size() && i < pos + depth; i++) {
      if (matrix.get(row, i) != null) {
        return false;
      }
    }
    return true;
  }

  @FunctionalInterface
  public interface ChildrenFunction<N> {
    Iterable<? extends N> children(N node);
  }
}
