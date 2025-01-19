

  import org.apache.spark.sql.SparkSession

  import org.apache.spark.ml.linalg.{Matrix, Vectors}
  import org.apache.spark.ml.stat.Correlation
  import org.apache.spark.sql.Row
  import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}

  object SimpleApp {


    def main(args: Array[String]): Unit = {


      val logFile = "C:\\Users\\phili\\Downloads\\spark-3.5.3-bin-hadoop3-scala2.13\\spark-3.5.3-bin-hadoop3-scala2.13\\README.md" // Should be some file on your system
      val spark = SparkSession.builder.appName("Simple Application").getOrCreate()




      val sentenceData = spark.createDataFrame(Seq(
        (0.0, "Hi I heard about Spark"),
        (0.0, "I wish Java could use case classes"),
        (1.0, "Logistic regression models are neat")
      )).toDF("label", "sentence")

      val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
      val wordsData = tokenizer.transform(sentenceData)

      val hashingTF = new HashingTF()
        .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20)

      val featurizedData = hashingTF.transform(wordsData)
      // alternatively, CountVectorizer can also be used to get term frequency vectors

      val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
      val idfModel = idf.fit(featurizedData)

      val rescaledData = idfModel.transform(featurizedData)
      rescaledData.select("label", "features").show()


      val df2 = spark.read.format("bigquery").load("main-prod-391012.task.music-play-progress")
      val logData = spark.read.textFile(logFile).cache()
      val numAs = logData.filter(line => line.contains("a")).count()
      val numBs = logData.filter(line => line.contains("b")).count()
      println(s"Lines with a: $numAs, Lines with b: $numBs")
      println(s"count is ==============>${df2.count()}")
      spark.stop()
    }
  }


