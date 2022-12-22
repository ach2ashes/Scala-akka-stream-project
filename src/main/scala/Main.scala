import akka.actor._
import akka._
import scala.concurrent._
import java.nio.file._
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO,Flow, Sink, Source}
import akka.stream.alpakka.file.scaladsl.FileTailSource
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.lang

object LogFileAnalyzer {
  def main(args: Array[String]): Unit = {
    // Create actor system and materializer
    implicit val system = ActorSystem("LogFileAnalyzer")
    implicit val materializer = ActorMaterializer()

    // Read the log file as a source of lines
    val logFile = Paths.get("./src/main/scala/log-generator.log")
    val source =   FileTailSource(logFile,maxChunkSize = 4096,startingPosition=0L,pollingInterval = 5000.millis).via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 4096, allowTruncation = true))


    // Parse each line of the log file to extract the website name
    val websiteFlow = Flow[ByteString].map { line =>
      val fields = line.utf8String.split(" ")
      val website = fields(2)
      (website,1)
    }.groupBy(Int.MaxValue,_._1)
    .scan(("",0)) { case ((_, count), (website, _)) => (website, count + 1) }
    .filter{case (website, count) => count > 0}
    .map { case (website,count) => s"($website, $count)\n" }
    .map(s=>ByteString(s))
    .mergeSubstreams
    
    val httpFlow = Flow[ByteString].map { line =>
      val fields = line.utf8String.split(" ")
      val http = fields(1)
      (http,1)
    }.groupBy(Int.MaxValue,_._1)
    .scan(("",0)) { case ((_, count), (http, _)) => (http, count + 1) }
    .filter{case (http, count) => count > 0}
    .map { case (http,count) => s"( HTTP $http, $count)\n" }
    .map(s=>ByteString(s))
    .mergeSubstreams
  
val sink_website = FileIO.toPath(Paths.get("./websites.csv")) 
val sink_http = FileIO.toPath(Paths.get("./http.csv")) 

val f = source.via(websiteFlow).runWith(sink_website)
val ff = source.via(httpFlow).runWith(sink_http)

while (true){
Await.result(f,60.second)
Await.result(ff,60.second)
}
}
 
}
 
