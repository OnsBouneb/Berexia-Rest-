package controllers

import java.io.File

import controllers.Select.{ sparkSession, sqlContext}
import javax.inject._
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import play.api.libs.json.Reads._
import play.api.libs.json._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.apache.spark.sql.DataFrame

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))


  }
  def getListOfFiles(dir:String):List[String]= {
    val file = new File(dir)
    file.listFiles()
      .map(_.getPath).filter(p => p.contains(".csv") && !p.contains(".crc")).toList


  }

  case class inst(transformation : String, query: String)
  case class listOfInst(list : List[inst])
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

  def transform = Action { request =>

//    (request.body.asJson.get \ "instructions" ).get(0).validate[Map[String, String]].map{
//      case (name) => Ok(name.getClass())
//    }.recoverTotal{
//      e => BadRequest("Detected error:"+ JsError.toJson(e))
//    }

    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
//    val parsedJson = mapper.readValue[Map[String, Object]]((request.body.asJson.get \ "instructions" ).get(1).toString())
    val parsedJson = mapper.readValue[Map[String, Object]]((request.body.asJson.get \ "instructions" ).get(0).toString())

    println(parsedJson)
    println(parsedJson.get("query").get)
    val query = parsedJson.get("query").get.toString()
val name = parsedJson.get("source").get.toString()
    val type1 =  parsedJson.get("type").get.toString()
val df = ToDataframe(name,type1)
//    val requestBody = request.body.asJson.map { json =>
//      json.validate[(String)].map{
//        case (name) => Ok("Hello " + name)
//      }.recoverTotal{²
//        e => BadRequest("Detected error:")
//      } }
////    val inst :JsValue = requestBody.get("instructions")
//    println(requestBody)
//    println(requestBody.getClass())
    Select(query,df)

    val k = "test"
    Ok(Json.toJson(k))

  }
  def Select (requete : String,df:DataFrame): Unit ={

    df.createOrReplaceTempView("ons7")

    val k1 = sqlContext.sql(requete)
    //  k1.coalesce(1).write.csv("public/p810")
    //import org.apache.hadoop.fs._;
    //val fs = FileSystem.get(sc.hadoopConfiguration());

    //fs.rename(new Path("public/p80"), new Path("public/newData.csv"));
    df.show(10)
    k1.show(4)
    k1.coalesce(1).write.csv("public/test")
    rename("public/test")
    println("jaw save")
  }

  def rename(path:String)  {

    val j= getListOfFiles(path)

    val b = Map("path"-> j)
    println("*************************************")
    println(j)
    println(b.getClass())
    println(b)
    println("*************************************")

  // Ok(Json.toJson(b))

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
  def filetodataframe() = Action {
    val f :DataFrame= ToDataframe("testexcel","xlsx")
    f.show()
    Ok(views.html.customersegmentation("filetodataframe"))

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
