package com.inplaytime.experiments
import java.io._
import java.net._
import java.util.jar._

object Balance {
  def apply(accountId:String) = {
    val b = new Balance(accountId, 0)
    var f = new File("Balance." + accountId + ".event."+ (b.eventList.size + 1))
    while (f.exists) {
      val fis = new FileInputStream(f)
      val ois = new ObjectInputStream(fis)
      var event = ois.readObject
      println("Loading event " + event)
      b.addEvent(event.asInstanceOf[TransactionEventFunc])
      // nothing to do
      f = new File("Balance." + accountId + ".event."+ (b.eventList.size + 1))
    }
    b
  }
  type TransactionEventFunc = (Balance => Balance)
  case class Deposit(val amount:Double) extends TransactionEvent {
    override def toEvent(balance:Balance):Balance = {
      new Balance(balance.accountId, balance.balance  + amount)
    }
  }
  case class Withdraw(val amount:Double) extends TransactionEvent {
    override def toEvent(balance:Balance):Balance = {
      new Balance(balance.accountId, balance.balance - amount)
    }
  }
  trait TransactionEvent {
    def toEvent(balance:Balance):Balance
    def asEvent:TransactionEventFunc = toEvent _
  }
}

class Balance(val accountId:String, val balance:Double) {
  import com.inplaytime.experiments.Balance._

  def addEvent(t:TransactionEventFunc) = {
    // save to list
    eventList = eventList ::: (t :: Nil)
    // save to file
    appendEventToFile(t)
  }

  /* can't append multiple objects to one file in multiple sessions without some mucking about
  *  since we're going to put them in separate columns anyhow, I'll put them in separate files
  *  for now.
  * */
  def appendEventToFile(t:TransactionEventFunc) = {
    val f = new File("Balance." + accountId + ".event." + eventList.size)
    val fos = new FileOutputStream(f, true)
    val oos = new ObjectOutputStream(fos)
    oos.writeObject(t)
    oos.flush
    fos.flush
    oos.close
    fos.close
  }

  def applyEvents:Balance = {
    eventList.foldLeft(this)((last, next) => next(last))
  }
  var eventList = List[TransactionEventFunc]()


}






class URLJar(val jarURL:String) {

  def loadClassFromJar(className:String) = {
//    val theClassURL:String = "jar:http://search.maven.org/remotecontent" +
//       "?filepath=org/ops4j/pax/url/pax-url-mvn/1.3.4/pax-url-mvn-1.3.4.jar!" +
//       "/org/ops4j/pax/url/mvn/Handler.class"

  // this works for http:, but not for mvn protocol adaptor..
//    val byteCode = new URL("jar:" + jarURL + "!" + className + ".class")
    val byteCode = new URL(jarURL)

    val inputStream = byteCode.openConnection.getInputStream

    val bis = new BufferedInputStream(inputStream)
    val jis = new JarInputStream(bis)
//    var classBytes = new Array[Byte]

    var jarEntry = jis.getNextJarEntry
    while (jarEntry!=null) {
      println("Jar Entry " + jarEntry.getAttributes)
      println("\t " + jarEntry.getName)
      // in the form
      //org/ops4j/util/property/FallbackPropertyResolver.class
      println("\t " + jarEntry.getComment)
      println("\t " + jarEntry.getExtra)
//      classbytes = new Array[Byte](je.getSize())
//      jis.read(classbytes, 0, classbytes.length)

      jarEntry = jis.getNextJarEntry
    }
    bis.close


  }
}

