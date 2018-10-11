package com.axis.system.jenkins.plugins.downstream.tree

import org.junit.Test

import static com.axis.system.jenkins.plugins.downstream.tree.TreeLaminator.getShallowDepth
import static com.axis.system.jenkins.plugins.downstream.tree.TreeLaminator.layoutTree

/**
 * @see TreeLaminator
 *
 * @author Gustaf Lundh <gustaf.lundh@axis.com>
 */
class TreeLaminatorTest {
  private final static CHILDREN_FUNC = { n -> n.children }

  @Test
  void singleNodeTest() {
    def tree = node('Root')
    Matrix matrix = layoutTree(tree, CHILDREN_FUNC)
    assert toAscii(matrix) == ' {Root      }'
  }

  @Test
  void oneChildTest() {
    def tree =
        node('Root',
            node('Child')
        )
    Matrix matrix = layoutTree(tree, CHILDREN_FUNC)
    assert toAscii(matrix) == ' {Root      }━{Child     }'
  }

  @Test
  void severalDirectChildren() {
    def tree =
        node('Root',
            node('ChildA'),
            node('ChildB'),
            node('ChildC')
        )
    Matrix matrix = layoutTree(tree, CHILDREN_FUNC)
    assert toAscii(matrix) ==
        '''| {Root      }┳{ChildA    }
           |             ┣{ChildB    }
           |             ┗{ChildC    }'''.stripMargin()
  }

  @Test
  void deepTreeNoSiblings() {
    def tree =
        node('Root',
            node('ChildA',
                node('ChildAA',
                    node('ChildAAA')
                )
            )
        )
    Matrix matrix = layoutTree(tree, CHILDREN_FUNC)
    assert toAscii(matrix) == ' {Root      }━{ChildA    }━{ChildAA   }━{ChildAAA  }'
  }

  @Test
  void successfullyCompactedMatrix() {
    def tree =
        node('Root',
            node('ChildA',
                node('ChildAA')
            ),
            node('ChildB',
                node('ChildBA'),
            )
        )
    Matrix matrix = layoutTree(tree, CHILDREN_FUNC)
    assert toAscii(matrix) ==
        '''| {Root      }┳{ChildA    }━{ChildAA   }
           |             ┗{ChildB    }━{ChildBA   }'''.stripMargin()
  }

  @Test
  void nonSuccessfullyCompactedMatrix() {
    def tree =
        node('Root',
            node('ChildA',
                node('ChildAA'),
                node('ChildAB')
            ),
            node('ChildB',
                node('ChildBA'),
            )
        )
    Matrix matrix = layoutTree(tree, CHILDREN_FUNC)
    assert toAscii(matrix) ==
        '''| {Root      }┳{ChildA    }┳{ChildAA   }
           |             ┃            ┗{ChildAB   }
           |             ┗{ChildB    }━{ChildBA   }'''.stripMargin()
  }

  @SuppressWarnings('GroovyAssignabilityCheck')
  @Test
  void shallowDepth() {
    def tree =
        node('Root',
            node('ChildA',
                node('ChildAA'),
                node('ChildAB')
            ),
            node('ChildB',
                node('ChildBA'),
            )
        )
    assert getShallowDepth(tree, CHILDREN_FUNC) == 3
    assert getShallowDepth(tree.children[0], CHILDREN_FUNC) == 2
    assert getShallowDepth(tree.children[0].children[0], CHILDREN_FUNC) == 1
    tree =
        node('Root',
            node('ChildA'),
            node('ChildB',
                node('ChildBA'),
            )
        )
    assert getShallowDepth(tree, CHILDREN_FUNC) == 2
  }

  private static String toAscii(Matrix matrix) {
    StringBuilder sb = new StringBuilder()
    for (List<Matrix.Entry> row : matrix.get()) {
      for (Matrix.Entry entry : row) {
        sb.append(entryToAsciiCell(entry))
      }
      sb.append('\n')
    }
    sb.length = sb.length() - 1
    return sb.toString()
  }

  @SuppressWarnings('GroovyAssignabilityCheck')
  private static String entryToAsciiCell(Matrix.Entry<Map> entry) {
    StringBuilder sb = new StringBuilder()
    if (entry) {
      sb.append(entry.arrow ? entry.arrow : " ")
      if (entry.data) {
        sb.append(String.format('{%-10.10s}', entry.data.name))
      } else {
        sb.append(' '.padRight(12))
      }
    } else {
      sb.append(' '.padRight(13))
    }
    return sb.toString()
  }

  static Map node(String name, Map... children) {
    return ['name': name, 'children': children as List]
  }
}
