"""SimpleApp.py"""
from pyspark.sql import SparkSession

logFile="C:\\Users\\phili\\Downloads\\spark-3.5.3-bin-hadoop3-scala2.13\\spark-3.5.3-bin-hadoop3-scala2.13\\README.md"
spark = SparkSession.builder.appName("SimpleApp").getOrCreate()
logData = spark.read.text(logFile).cache()
df2 = spark.read.format("bigquery").load("main-prod-391012.task.music-play-progress")

numAs = logData.filter(logData.value.contains('a')).count()
numBs = logData.filter(logData.value.contains('b')).count()

print("Lines with a: %i, lines with b: %i" % (numAs, numBs))
df2.show()
print("count is ==============>%s" , df2.count())
spark.stop()