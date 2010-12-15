package staomp.sets

import java.util.concurrent.locks.ReentrantLock
import staomp.RichLock.lock2RichLock

class CoarseSet[T] extends Set[T] {
  private class Node(val item: T, val key: Int) {
    def this(item: T) = this(item, item.hashCode)
    var next: Node = null
  }
  
  private val head = new Node(null.asInstanceOf[T], Int.MinValue)
  head.next = new Node(null.asInstanceOf[T], Int.MaxValue)
  
  private val lock = new ReentrantLock
  
  def add(elem: T): Boolean = {
    var (pred, curr) = (head, head.next)
    val key = elem.hashCode
    
    lock.withLock {
      val (pred, curr) = find(key)
      if (curr.key == key) {
        return false
      }
      val node = new Node(elem)
      node.next = curr
      pred.next = node
      return true
    } 
  }
  
  def remove(elem: T): Boolean = { 
    var (pred, curr) = (head, head.next)
    val key = elem.hashCode
    
    lock.withLock {
      val (pred, curr) = find(key)
      if (curr.key > key) {
        return false
      } 
      pred.next = curr.next
      return true
    } 
  }
  
  def contains(elem: T): Boolean = { 
    val key = elem.hashCode
    lock.withLock {
      val (pred, curr) = find(key)
      curr.key == key
    }
  }
  
  private def find(key: Int):(Node, Node) = {
    var (pred, curr) = (head, head.next)
    while (curr.key < key) {
      pred = curr
      curr = curr.next
    }
    (pred, curr)
  }
}
