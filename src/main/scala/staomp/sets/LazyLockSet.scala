package staomp.sets

import java.util.concurrent.locks.ReentrantLock

class LazyLockSet[T] extends Set[T] {
  private class Node(val item: T, val key: Int) {
    def this(item: T) = this(item, item.hashCode)
    var next: Node = null
    var marked: Boolean = false

    private val lck = new ReentrantLock
    def lock { lck.lock }
    def unlock { lck.unlock }

    def ->(n: Node): Node = { next = n; n }
  }

  private val head = new Node(null.asInstanceOf[T], Int.MinValue)
  head.next = new Node(null.asInstanceOf[T], Int.MaxValue)

  def contains(elem: T): Boolean = {
    val key = elem.hashCode
    var curr = head
    while (curr.key < key) curr = curr.next
    curr.key == key && !curr.marked
  }

  def remove(elem: T): Boolean = {
    val key = elem.hashCode
    while (true) {
      val (pred, curr) = findKey(key)
      pred.lock
      try {
        curr.lock
        try {
          if (validate(pred, curr)) {
            if (curr.key == key) {
              curr.marked = true
              pred -> curr.next
              return true
            } else {
              return false
            }
          }
        } finally {
          curr.unlock
        }
      } finally {
        pred.unlock
      }
    }
    throw new IllegalStateException
  }

  def add(elem: T): Boolean = {
    val key = elem.hashCode
    while (true) {
      val (pred, curr) = findKey(key)
      pred.lock
      try {
        curr.lock
        try {
          if (validate(pred, curr)) {
            if (curr.key == key) {
              return false
            } else {
              pred -> new Node(elem) -> curr
              return true
            }
          }
        } finally {
          curr.unlock
        }
      } finally {
        pred.unlock
      }
    }
    throw new IllegalStateException
  }

  private def validate(pred: Node, curr: Node): Boolean = !pred.marked && !curr.marked && pred.next == curr

  private def findKey(key: Int):(Node, Node) = {
    var (pred, curr) = (head, head.next)
    while (curr.key < key) {
      pred = curr
      curr = curr.next
    }
    (pred, curr)
  }
}