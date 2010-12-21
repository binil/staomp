package staomp.sets

import java.util.concurrent.atomic.AtomicMarkableReference

class LockFreeSet[T] extends Set[T] {
  private class Node(val item: T, val key: Int) {
    def this(item: T) = this(item, item.hashCode)
    var next = new AtomicMarkableReference[Node](null, false)

    // Physically delete all marked nodes from this one
    // until we reach an unmarked one. Set this node's
    // next pointer to that unmarked node and return the
    // pair (true, that node). If interference from some
    // other thread is detected, return the pair (false, null)
    def fix: (Boolean, Node) = {
      val mark = Array(false)
      var curr = next.getReference
      var succ = curr.next.get(mark)
      // INVARIANT: mark(0) always has the mark of node's next ref
      while (mark(0)) {
        val deleted = next.compareAndSet(curr, succ, false, false)
        if (!deleted) return (false, null)
        curr = succ
        succ = curr.next.get(mark)
      }
      (true, curr)
    }
  }

  private val head = new Node(null.asInstanceOf[T], Int.MinValue)
  head.next.set(new Node(null.asInstanceOf[T], Int.MaxValue), false)

  def contains(elem: T): Boolean = {
    val key = elem.hashCode
    // INVARIANT: curr points to a Node and
    // mark(0) is that node's next refs mark
    val mark = Array(false)
    var curr = head
    while (curr.key < key) {
      curr = curr.next.getReference
      curr.next.get(mark)
    }
    curr.key == key && !mark(0)
  }

  def remove(elem: T): Boolean = {
    val key = elem.hashCode
    while (true) {
      val (pred, curr) = find(key)
      if (curr.key != key) {
        return false
      } else {
        val succ = curr.next.getReference
        if (curr.next.compareAndSet(succ, succ, false, true)) {
          // make an effort to physically delete the node
          // OK if this CAS fails because some other thread will
          // handle cleanup after this
          pred.next.compareAndSet(curr, succ, false, false)
          return true
        }
      }
    }
    throw new IllegalStateException
  }

  def add(elem: T): Boolean = {
    val key = elem.hashCode
    while (true) {
      val (pred, curr) = find(key)
      if (curr.key == key) {
        return false
      } else {
        val node = new Node(elem)
        node.next = new AtomicMarkableReference(curr, false)
        if (pred.next.compareAndSet(curr, node, false, false)) return true
      }
    }
    throw new IllegalStateException
  }

  // Find a pair of nodes (pred, curr) such that
  // pred.key < key <= curr.key. Also physically
  // delete any marked nodes on the path from
  // head to curr
  private def find(key: Int): (Node, Node) = {
    var (pred, curr) = (head, head.next.getReference)
    while (true) {
      val (fixed, node) = pred.fix
      if (fixed) {
        if (node.key >= key) {
          return (pred, node)
        } else {
          pred = node
          curr = pred.next.getReference
        }
      } else { // detected interference, start over
        pred = head
        curr = head.next.getReference
      }
    }
    throw new IllegalStateException
  }
}