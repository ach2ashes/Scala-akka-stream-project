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
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import java.lang

object LogFileAnalyzer {
  def main(args: Array[String]): Unit = {
    // Create actor system and materializer
    implicit val system = ActorSystem("LogFileAnalyzer")
    implicit val materializer = ActorMaterializer()

    // Read the log file as a source of lines
    val logFile = Paths.get("./src/main/scala/log-generator.log")
    val source :  Source[ByteString, Future[IOResult]]= FileIO.fromPath(logFile).via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
    // Parse each line of the log file to extract the website name
    val websiteFlow = Flow[ByteString].map { line =>
      val fields = line.utf8String.split(" ")
      val website = fields(2)
      (website,1)
    }.groupBy(Int.MaxValue,_._1).fold(("",0)) { case ((_, count), (website, _)) => (website, count + 1) }
.map { case (website, count) => (website, count) }.mergeSubstreams
  

    // Group the website names and count the number of visits for each website
    //case (website, visits) => (website, visits +1)
    source.via(websiteFlow).runWith(Sink.foreach {
      case (website, count) => println(s"$website: $count")
    })
}
 
}
 /*
    // Write the results to a file
    val outputFile = Paths.get("./file.txt")
    val sink = FileIO.toPath(outputFile)

    // Connect the source, flow, and sink to create a stream
    val stream = source.via(websiteFlow).via(countFlow).to(sink)

    // Run the stream
    stream.run()
  }
}
*/