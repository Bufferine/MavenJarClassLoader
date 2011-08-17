package com.inplaytime.experiments

import java.io._
import java.net.URL
/**
 * Created by IntelliJ IDEA.
 * User: bufferine
 * Date: 11-08-17
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */

object GetClassFromMavenJar {
  def getJarAndClass = {

    // first the whole jar
    val theJarURL:String = "http://search.maven.org/remotecontent" +
      "?filepath=org/ops4j/pax/url/pax-url-mvn/1.3.4/pax-url-mvn-1.3.4.jar"

    getJar(theJarURL, "pax-url-mvn-1.3.4.jar.new")

    // now just the class
    val theClassURL:String = "jar:http://search.maven.org/remotecontent" +
       "?filepath=org/ops4j/pax/url/pax-url-mvn/1.3.4/pax-url-mvn-1.3.4.jar!" +
       "/org/ops4j/pax/url/mvn/Handler.class"

    val byteCode = new URL(theClassURL)

    val inputStream = byteCode.openConnection.getInputStream

    val bis = new BufferedInputStream(inputStream)
    val fos = new FileOutputStream("Handler.class")

    var temp:Int = 0
    temp = bis.read()
    while(temp != -1 )
    {
      fos.write(temp);
      temp = bis.read()
    }
    fos.close
    bis.close
  }

  def getJar(url:String, filename:String) = {
    val theJar = new URL(url)
    val in = theJar.openConnection.getInputStream
    getWithInputStream(in, filename)
    in.close();

  }

  def getWithInputStream(in:InputStream, filename:String) = {
    val fout = new FileOutputStream(filename);

    var b = new Array[Byte](1024);
    var noOfBytes = 0;

    noOfBytes = in.read(b)
    while(noOfBytes != -1 ){
      fout.write(b, 0, noOfBytes);
      noOfBytes = in.read(b)
    }

    fout.close();

  }

  def getFromMavenWithPax = {
    val paxURL = "mvn:org.ops4j.pax.url/pax-url-mvn/1.3.2"
    val theJar = new URL(null, paxURL, new org.ops4j.pax.url.mvn.Handler)
    val in = theJar.openConnection.getInputStream
    getWithInputStream(in, "jarFromPax.jar")
    in.close();
  }
  def getClassFromMavenWithPax = {
    // doesn't work - '!' is already used by the mvn protocol handler
    // need to try swapping it out.
    // although caching the jar would probably be sensible anyhow.
    // this protocol handler seems to use mvn, but not really interact
    // so it doesn't put it in your local cache for you.

    val paxURL = "mvn:org.ops4j.pax.url/pax-url-mvn/1.3.4"
    val theClassURL:String = "jar:" + paxURL + "!" +
      "/org/ops4j/pax/url/mvn/Handler.class"
    val theClass = new URL(null, theClassURL, new org.ops4j.pax.url.mvn.Handler)
    val in = theClass.openConnection.getInputStream
    getWithInputStream(in, "HandlerFromPax.class")
    in.close();

  }
}