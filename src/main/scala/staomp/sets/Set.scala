package staomp.sets

trait Set[T] {
  def add(elem: T): Boolean
  
  def remove(elem: T): Boolean
  
  def contains(elem: T): Boolean
}
