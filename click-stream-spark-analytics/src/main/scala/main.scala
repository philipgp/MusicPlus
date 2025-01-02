
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{ForeachWriter, Row, SparkSession}
import org.apache.spark.sql.types.StructType

import java.io.FileWriter
class ConsoleWriter extends ForeachWriter[Row]{

  override def open(partitionId: Long, epochId: Long): Boolean = {
  print("open called")
    true
  }

  override def process(value: Row): Unit = {
    print(value.prettyJson)
    val file = new FileWriter("C:\\workspace\\sparkdestination\\result")
      file.write(value.prettyJson)
    file.close()
//    println("title : "  + value.getAs("title"))
//    println("count : "  + value.getAs("count"))
  }

  override def close(errorOrNull: Throwable): Unit = {

  }
}
object SimpleApp2 {
  //@main
  def main2(args: Array[String]): Unit = {
    val logFile = "C:\\Users\\phili\\Downloads\\spark-3.5.3-bin-hadoop3-scala2.13\\spark-3.5.3-bin-hadoop3-scala2.13\\README.md" // Should be some file on your system
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val userSchema = new StructType().add("title", "string").add("progressPercent", "integer")
    val stream = spark.readStream
      .schema(userSchema).json("file:///C:\\workspace\\sparksource")
    val stOut = stream.groupBy("title").count()
    stOut.writeStream.foreach(new ConsoleWriter())
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .start()
      .awaitTermination()
    //    stream.
    /*stream.writeStream.format("json")
      .option("checkpointLocation", "C:\\workspace\\sparkcheckpoint")
      .option("path","C:\\workspace\\sparkdestination")
      .outputMode("append") // Try "update" and "complete" mode.
//      .option("truncate", false)
//      .format("console")
      .start()
      .awaitTermination()*/
//    val logData = spark.read.textFile(logFile).cache()
//    val numAs = logData.filter(line => line.contains("a")).count()
//    val numBs = logData.filter(line => line.contains("b")).count()
//    println("------------------------------")
//    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}