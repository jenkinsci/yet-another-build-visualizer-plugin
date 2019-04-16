package com.axis.system.jenkins.plugins.downstream.yabv

import org.junit.Test

/**
 * @see com.axis.system.jenkins.plugins.downstream.yabv.NameNormalizer
 * @author Gustaf Lundh <gustaf.lundh@axis.com>
 */
class NameNormalizerTest {
  private final static NAME_FUNC = { n -> n[0] }
  private final static PARENT_FUNC = { n -> n.size() > 1 ? n.drop(1) : null }

  @Test
  void singleItemNoCollisions() {
    def itemA = ['a', 'b', 'root']
    def nameNormalizer = new NameNormalizer([itemA].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemA) == 'a'
  }

  @Test
  void multipleItemsNoCollisions() {
    def itemA = ['a', 'b', 'root']
    def itemB = ['b', 'b', 'root']

    def nameNormalizer = new NameNormalizer([itemA, itemB].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemA) == 'a'
    assert nameNormalizer.getNormalizedName(itemB) == 'b'
  }

  @Test
  void multipleItemsCollisions() {
    def itemA = ['a', 'b', 'root']
    def itemB = ['a', 'd', 'root']

    def nameNormalizer = new NameNormalizer([itemA, itemB].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemA) == 'b/a'
    assert nameNormalizer.getNormalizedName(itemB) == 'd/a'
  }

  @Test
  void rootCollisions() {
    def itemA = ['a', 'b', 'root']
    def itemB = ['a', 'root']

    def nameNormalizer = new NameNormalizer([itemA, itemB].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemB) == '/a'
    assert nameNormalizer.getNormalizedName(itemA) == 'b/a'
  }

  @Test
  void innocentBystander() {
    def itemA = ['a', 'b', 'root']
    def itemB = ['a', 'd', 'root']
    def itemC = ['b', 'd', 'root']

    def nameNormalizer = new NameNormalizer([itemA, itemB].toSet(), NAME_FUNC, PARENT_FUNC)
    assert nameNormalizer.getNormalizedName(itemA) == 'b/a'
    assert nameNormalizer.getNormalizedName(itemB) == 'd/a'
    assert nameNormalizer.getNormalizedName(itemC) == 'b'
  }
}
