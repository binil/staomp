package staomp.locks

import java.util.Random

object LockTest {
  
  
  def main(args: Array[String]) {
    val random = new Random
    val lock = new MCSLock
  
    class TestThread(val id: Int) extends Thread {
      setName("Thread(" + id + ")")
      
      override def run() {
        val head = ("   " * id) + getName
        for (i <- 1 to 3) {
          println(head + " attempting lock")
          lock.lock
          try {
            println(head + " got lock")
            val delay = random.nextInt(2000)
            println(head + " going to sleep for " + delay + " ms")
            try {
              Thread.sleep(delay)
            } catch {
              case e: InterruptedException => e.printStackTrace
            }
          } finally {
            println(head + " unlocking")
            lock.unlock
          }
        }
      }
    }
    
    println("Starting")
    for (i <- 1 to 3) new TestThread(i).start
  }
}
