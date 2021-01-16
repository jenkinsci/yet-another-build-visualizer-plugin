package com.axis.system.jenkins.plugins.downstream.yabv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NameNormalizer<T> {
  private static String NAME_SEPARATOR = "/";
  private static String ROOT_NAME = "";

  Set<String> blacklist;
  NameFunction<T> nameFunc;
  ParentFunction<T> parentFunc;

  public NameNormalizer(Set<T> items, NameFunction<T> nameFunc, ParentFunction<T> parentFunc) {
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
      List<String> nameSegments = new ArrayList<>();
      for (; item != null; item = parentFunc.parent(item)) {
        nameSegments.add(0, parentFunc.parent(item) != null ? nameFunc.name(item) : ROOT_NAME);
        String formattedName = String.join(NAME_SEPARATOR, nameSegments);
        // Every proposed display name is added to the reserved list. If we stumble upon an already
        // reserved name, we have a job name collision. This means the name is not unique enough to
        // be used for visualization (hence blacklisted). Add path elements (parents) to the name
        // until we find a name previously not reserved.
        //
        // Later on, when visualizing a job name, we will add path elements (parents) to the job
        // name until we find a name which is not blacklisted. When found, we have a job name that
        // we know will be unique in the build flow graph and easily identifiable to the user.
        if (reserved.add(formattedName)) {
          break;
        } else {
          blacklist.add(formattedName);
        }
      }
    }
    return blacklist;
  }

  public String getNormalizedName(T item) {
    List<String> nameSegments = new ArrayList<>();
    for (; item != null; item = parentFunc.parent(item)) {
      nameSegments.add(0, parentFunc.parent(item) != null ? nameFunc.name(item) : ROOT_NAME);
      String formattedName = String.join(NAME_SEPARATOR, nameSegments);
      if (!blacklist.contains(formattedName)) {
        return formattedName;
      }
    }
    // Should never happen unless an item we iterated over provided an empty String for name.
    // In this case we just return what we have.
    return String.join(NAME_SEPARATOR, nameSegments);
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
