package staomp.sets

import java.util.concurrent.locks.ReentrantLock

class OptimisticLockSet[T] extends Set[T] {
  private class Node(val item: T, val key: Int) {
    private val lck = new ReentrantLock
    var next: Node = null
    def this(item: T) = this(item, item.hashCode)

    def lock { lck.lock }
    def unlock { lck.unlock }

    def ->(n: Node): Node = {
      n.next = this
      this
    }
  }

  private val head = new Node(null.asInstanceOf[T], Int.MinValue)
  head.next = new Node(null.asInstanceOf[T], Int.MaxValue)

  def add(item: T): Boolean = {
    val key = item.hashCode
    while (true) {
      val (pred, curr) = findKey(key)

      pred.lock
      curr.lock
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) {
            return false
          } else {
            pred -> new Node(item) -> curr
            return true
          }
        }
      } finally {
        pred.unlock
        curr.unlock
      }
    }
    throw new IllegalStateException
  }

  def contains(item: T): Boolean = {
    val key = item.hashCode
    while (true) {
      val (pred, curr) = findKey(key)

      pred.lock
      curr.lock
      try {
        return curr.key == key
      } finally {
        pred.unlock
        curr.unlock
      }
    }
    throw new IllegalStateException
  }

  def remove(item: T): Boolean = {
    val key = item.hashCode
    while (true) {
      val (pred, curr) = findKey(key)

      pred.lock
      curr.lock
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) {
            pred.next = curr.next
            return true
          } else {
            return false
          }
        }
      } finally {
        pred.unlock
        curr.unlock
      }
    }
    throw new IllegalStateException
  }

  private def findKey(key: Int):(Node, Node) = {
    var (pred, curr) = (head, head.next)
    while (curr.key <= key) {
      pred = curr
      curr = curr.next
    }
    (pred, curr)
  }

  private def validate(pred: Node, curr: Node): Boolean = {
    var node = head
    while (node.key <= pred.key) {
      if (node == pred) {
        return pred.next == curr
      }
      node = node.next
    }
    false
  }
}