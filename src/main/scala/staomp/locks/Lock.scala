package staomp.locks

trait Lock {
    def lock(): Unit
    
    def unlock(): Unit
}
