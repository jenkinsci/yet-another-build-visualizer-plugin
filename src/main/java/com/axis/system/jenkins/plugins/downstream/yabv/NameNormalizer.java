package com.axis.system.jenkins.plugins.downstream.yabv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NameNormalizer {
  Set<String> blacklist;
  NameFunction nameFunc;
  ParentFunction parentFunc;

  public <T> NameNormalizer(Set<T> items, NameFunction nameFunc, ParentFunction parentFunc) {
    this.nameFunc = nameFunc;
    this.parentFunc = parentFunc;
    blacklist = generateBlacklist(items, nameFunc, parentFunc);
  }

  /**
   * Build a list of blacklisted display names. I.e. names not unique enough to be used displayed
   * without additional path components (parents).
   *
   * @param items Items to generate blacklist from
   * @param nameFunc NameFunction for extracting names from items
   * @param parentFunc ParentFunction for walking the parent chain
   * @return Set of names not unique enough for visualization
   */
  private static <T> Set<String> generateBlacklist(
      Set<T> items, NameFunction<T> nameFunc, ParentFunction<T> parentFunc) {
    Set<String> blacklist = new HashSet<>(), reserved = new HashSet<>();

    for (T item : items) {
      for (int nrOfParents = 0; ; nrOfParents++) {
        String name = nameFunc.name(item);

        String formattedName = getFullerDisplayName(item, nrOfParents, nameFunc, parentFunc);
        // Every proposed display name is added to the reserved list. If we stumble upon an already
        // reserved name, we have a job name collision. This means the name is not unique enough to
        // be used for visualization (hence blacklisted). Add path elements (parents) to the name
        // until we find a name previously not reserved.
        //
        // Later on, when visualizing a job name, we will add path elements (parents) to the job
        // name until we find a name which is not blacklisted. Wh   ven found, we have a job name
        // that we know will be unique in the build flow graph and easily identifiable to the user.
        if (reserved.add(formattedName) || formattedName.startsWith("/")) {
          break;
        } else {
          blacklist.add(formattedName);
        }
      }
    }

    return blacklist;
  }

  private static <T> String getFullerDisplayName(
      T item, int nrOfParents, NameFunction<T> nameFunc, ParentFunction<T> parentFunc) {
    List<String> nameSegments = new ArrayList();
    for (int i = 0; i <= nrOfParents && item != null; i++) {
      nameSegments.add(0, parentFunc.parent(item) != null ? nameFunc.name(item) : "");
      item = parentFunc.parent(item);
    }
    return String.join("/", nameSegments);
  }

  public <T> String getNormalizedName(T item) {
    for (int nrOfParents = 0; ; nrOfParents++) {
      String normalizedName = getFullerDisplayName(item, nrOfParents, nameFunc, parentFunc);
      if (!blacklist.contains(normalizedName) || normalizedName.startsWith("/")) {
        return normalizedName;
      }
    }
  }

  @FunctionalInterface
  public interface NameFunction<T> {
    String name(T item);
  }

  @FunctionalInterface
  public interface ParentFunction<T> {
    T parent(T item);
  }
}
