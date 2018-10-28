package controllers
import javax.inject._
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.linalg.Vectors
//import org.json4s.jackson.Json
import play.libs.Json._
//import vegas._
//import vegas.sparkExt._
//import vegas.render.WindowRenderer._
//import vegas.DSL.SpecBuilder
//import vegas.DSL.OptArg
import org.apache.spark.sql.{Encoder,Encoders}
import scala.reflect.ClassTag
import org.apache.spark._
import org.apache.spark.sql.functions._
import vegas.render.WindowRenderer._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd
import org.apache.spark.rdd.RDDOperationScope
import com.google.common.cache.{CacheBuilder, CacheLoader}
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import play.api._
import play.api.mvc._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import java.io.PrintWriter
import java.io.InputStream
import java.net.URI
import java.net.URLDecoder
import org.apache.spark.sql.types._
import com.knockdata.spark.highcharts._
import com.knockdata.spark.highcharts.model._
//import org.junit.Assert._
//import org.junit.Test
import java.io.PrintWriter
import scala.io.Source
import  java.lang.Double
import org.apache.spark.sql.functions.{unix_timestamp, to_date}
import  play.api.mvc.Action
import play.api.libs.json._
import java.io.File

//import  com.crealytics.spark.excel


object Select extends Controller {
  val conf = new SparkConf()
  conf.set("spark.driver.allowMultipleContexts", "true")
  conf.setMaster("local")
  conf.setAppName("test")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  var  sparkSession = SparkSession.builder.master("local").appName("ApplicationController").getOrCreate()

  def Select (requete : String,df:DataFrame): Unit ={

    df.createOrReplaceTempView("ons7")

    val k1 = sqlContext.sql(requete)
    //  k1.coalesce(1).write.csv("public/p810")
    //import org.apache.hadoop.fs._;
    //val fs = FileSystem.get(sc.hadoopConfiguration());

    //fs.rename(new Path("public/p80"), new Path("public/newData.csv"));
    df.show(10)
    k1.show(4)


    println("jaw save")
  }

  def getListOfFiles(dir:String):List[String]= {
    val file = new File(dir)
    file.listFiles()
      .map(_.getPath).filter(p => p.contains(".csv") && !p.contains(".crc")).toList


  }


  def ToDataframe (file:String, ext:String): DataFrame = {
    val path = "/home/onsbouneb/Desktop/" +file +"." +ext
    if (ext == "xlsx"){
      val dfons1 = sqlContext.read
        .format("com.crealytics.spark.excel").option("location", path)
        .option("useHeader", "true") // Required
        .load(path = path)
      return(dfons1)
    }
    else {
      //val sparkSession = SparkSession.builder.master("local").appName("spark session example").getOrCreate()

      val df = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load(path)

      df.toDF()
      return (df)
    }

  }
  def cols(df: DataFrame): Array[String] = {

    val array = df.columns
    return(array)
    /*
       val colms = array.deep.mkString("\n")

        print(colms)
        val colmsarray = array.deep
    */
    //print(array)
  }

  def GroupBy( col : String, agg :String): Unit = {

    val df = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons.csv")

    if (agg == "count") {
      val dr = df.groupBy(col).count()
      dr.show()
    }
    else if(agg == "max") {
      val dr = df.groupBy(col).max()
      dr.show()
    }
    else if(agg == "min") {
      val dr = df.groupBy(col).min()
      dr.show()
    }
    else if(agg == "avg") {
      val dr = df.groupBy(col).avg()
      dr.show()
    }

    // df.show(10)

  }

  def Join (col1 :String,col2:String,df:DataFrame,dfjoin:DataFrame): Unit ={



    // val df = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons.csv")
    // val dfjoin = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons2.csv")
    val okj = df.join(dfjoin,df(col1) === dfjoin(col2), "inner")
    okj.show()

  }


  def segmentation1() = Action {

    val df = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons.csv")
    val dfjoin = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons2.csv")
    //Select("SELECT * FROM ons7 WHERE Main_Risk_Type = 'Life'",df)

    Join("Age_at_Commencement","Age",df,dfjoin)
    Ok(views.html.customersegmentation("join"))

  }

  def segmentation2() = Action {

    val df = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons.csv")
    val dfjoin = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons2.csv")
    GroupBy("Gender","count")
    Ok(views.html.customersegmentation("groupby"))

  }

  def segmentationcol(d:String, f:String) = Action {

    val df = ToDataframe(d,f)
    //val df = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Downloads/"+d)
    val j = cols(df)
    //val jsonString = Json.toJson(k)
    val k = Map("cols"-> j)
    Ok(Json.toJson(k))

  }

  def rename() = Action {
    val j= getListOfFiles("/home/onsbouneb/Pictures/playapp/public/p74")
    val b = Map("path"-> j)

    Ok(Json.toJson(b))

  }




  def filetodataframe() = Action {
    val f :DataFrame= ToDataframe("testexcel","xlsx")
    f.show()
    Ok(views.html.customersegmentation("filetodataframe"))

  }










  import play.api.libs.json._
  //******************************************************************************************



  /*

def saveStock = Action { request =>
  val json = request.body.asJson.get
  val stock = json.as
  println(stock)
  Ok
}
*/

  def transform = Action { request =>
    val requestBody = request.body.asJson
    val inst = requestBody.get("instructions")
    println(inst)

    Ok(Json.toJson("Hello"))
  }

  def segmentation = Action {

    val df = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons.csv")
    //  val dfjoin = sparkSession.read.format("csv").option("header", "true").option("charset", "UTF8").option("delimiter", ";").load("/home/onsbouneb/Desktop/ons2.csv")
    Select("SELECT * FROM ons7 WHERE Date_of_Commencement = '13/02/2004'",df)
    val k = "test"
    //Join("Age_at_Commencement","Age",df,dfjoin)
    Ok(Json.toJson(k))
  }

}
