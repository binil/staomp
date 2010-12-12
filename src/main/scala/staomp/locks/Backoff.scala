package staomp.locks

import java.util.Random
import java.lang.Math.min
import java.lang.Thread.sleep

class Backoff(val maxDelay: Int, val minDelay: Int) {
  var limit = minDelay
  val random = new Random

  @throws(classOf[InterruptedException])
  def backoff() {
    val delay = random nextInt limit
    limit = min(maxDelay, 2*limit)
    sleep(delay)
  }
}
