package com.axis.system.jenkins.plugins.downstream.yabv

import org.junit.Test

/**
 * @see com.axis.system.jenkins.plugins.downstream.yabv.NameNormalizer
 * @author Gustaf Lundh <gustaf.lundh@axis.com>
 */
class NameNormalizerTest {
  private final static NAME_FUNC = { n -> n.name }
  private final static PARENT_FUNC = { n ->
    n.parents ? item(n.parents[0], *n.parents.drop(1)) : null }

  @Test
  void singleItemNoCollisions() {
    def itemA = item('a', 'b', 'root')
    def nameNormalizer = new NameNormalizer([itemA].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemA) == 'a'
  }

  @Test
  void multipleItemsNoCollisions() {
    def itemA = item('a', 'b', 'root')
    def itemB = item('b', 'b', 'root')

    def nameNormalizer = new NameNormalizer([itemA, itemB].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemA) == 'a'
    assert nameNormalizer.getNormalizedName(itemB) == 'b'
  }

  @Test
  void multipleItemsCollisions() {
    def itemA = item('a', 'b', 'root')
    def itemB = item('a', 'd', 'root')

    def nameNormalizer = new NameNormalizer([itemA, itemB].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemA) == 'b/a'
    assert nameNormalizer.getNormalizedName(itemB) == 'd/a'
  }

  @Test
  void rootCollisions() {
    def itemA = item('a', 'b', 'root')
    def itemB = item('a', 'root')

    def nameNormalizer = new NameNormalizer([itemA, itemB].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemB) == '/a'
    assert nameNormalizer.getNormalizedName(itemA) == 'b/a'
  }

  static Map item(String name, String... parents) {
    return ['name': name, 'parents': parents as List]
  }
}
