package example

import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.{DecisionTree, RandomForest}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD

object Train {
  val numClasses = 4
  val numTrees = 3
  val featureSubsetStrategy = "auto" // Let the algorithm choose.
  val impurity = "entropy"
  val maxDepth = 4
  val maxBins = 32

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext("local", "Train")
    try {
      val data = MLUtils.loadLibSVMFile(sc, "iris.scale")
      val Array(trainData, testData) = data.randomSplit(Array(0.7, 0.3))

      randomForest(trainData, testData)
      decisionTree(trainData, testData)
    } finally {
      sc.stop()
    }
  }

  def randomForest(trainData: RDD[LabeledPoint], testData: RDD[LabeledPoint]): Unit = {
    val startTime = System.currentTimeMillis()

    val model = RandomForest.trainClassifier(
      trainData, numClasses, Map[Int, Int](), numTrees,
      featureSubsetStrategy, impurity, maxDepth, maxBins
    )

    val labelAndPreds = testData.zipWithIndex().map {
      case (current, index) =>
        val predictionResult = model.predict(current.features)
        (index, current.label, predictionResult, current.label == predictionResult)
    }

    val endTime = System.currentTimeMillis() - startTime

    val testDataCount = testData.count()
    val testErrCount = labelAndPreds.filter(r => !r._4).count
    val testSuccessRate = 100 - (testErrCount.toDouble / testDataCount * 100)

    println("RfClassifier Results: execTime " + endTime + "msec")
    println("Test Data Count = " + testDataCount)
    println("Test Error Count = " + testErrCount)
    println("Test Success Rate (%) = " + testSuccessRate)
  }

  def decisionTree(trainData: RDD[LabeledPoint], testData: RDD[LabeledPoint]): Unit = {
    val startTime = System.currentTimeMillis()

    val model = DecisionTree.trainClassifier(
      trainData, numClasses, Map[Int, Int](),
      impurity, maxDepth, maxBins
    )

    val labelAndPreds = testData.zipWithIndex().map {
      case (current, index) =>
        val predictionResult = model.predict(current.features)
        (index, current.label, predictionResult, current.label == predictionResult)
    }

    val endTime = System.currentTimeMillis() - startTime

    val testDataCount = testData.count()
    val testErrCount = labelAndPreds.filter(r => !r._4).count // r._4 = 4th element of tuple (current.label == predictionResult)
    val testSuccessRate = 100 - (testErrCount.toDouble / testDataCount * 100)

    println("DTClassifier Results: execTime " + endTime + "msec")
    println("Test Data Count = " + testDataCount)
    println("Test Error Count = " + testErrCount)
    println("Test Success Rate (%) = " + testSuccessRate)
  }
}

