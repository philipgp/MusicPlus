
import org.apache.spark.sql.SparkSession
object SimpleApp {
  //@main
  def main(args: Array[String]): Unit = {
    val logFile = "C:\\Users\\phili\\Downloads\\spark-3.5.3-bin-hadoop3\\spark-3.5.3-bin-hadoop3\\README.md" // Should be some file on your system
    val spark = SparkSession.builder.master("spark://192.168.1.75:7077").appName("Simple Application").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("------------------------------")
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}