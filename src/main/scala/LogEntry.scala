case class LogEntry(httpcode: Int, url: String, path: String, ip: String)
object LogEntry {
  def parse(line: String): LogEntry = {
    val fields = line.split(" ")
    LogEntry(fields(1).toInt, fields(2), fields(3) ,fields(4))
  }
}
