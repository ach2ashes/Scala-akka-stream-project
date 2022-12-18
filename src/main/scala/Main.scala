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
    val source =   FileTailSource(logFile,maxChunkSize = 4096,startingPosition=0L,pollingInterval = 250.millis).via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 4096, allowTruncation = true)).map { line =>
      val fields = line.utf8String.split(" ")
      val website = fields(2)
      (website,1)
    }.groupBy(Int.MaxValue,_._1)//.fold(0){_+_._2}
    //.reduce { (entry1, entry2) =>val (website1, visits1) = entry1 
      //val (website2, visits2) = entry2
     // (website1, visits1 + visits2)}

    //.map { count=> s"(website, $count)" }
    .mergeSubstreams
    


    // Parse each line of the log file to extract the website name
    val websiteFlow = Flow[ByteString]
    
  
val sink = FileIO.toPath(Paths.get("./f.txt")) 
    // Group the website names and count the number of visits for each website
    //case (website, visits) => (website, visits +1)
val f=    source.runWith(Sink.foreach(println))
Await.result(f,10.second)
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
