package org.example

import scalaj.http.{Http, HttpResponse}
import play.api.libs.json.{Json, Writes, JsValue}

object SummarizeLibrary {

  case class HtmlRequest(html: String)

  implicit val htmlRequestWrites: Writes[HtmlRequest] = Json.writes[HtmlRequest]

  def summarize(url: String): String = {
    try {
      val response = Http(url)
        .timeout(connTimeoutMs = 5000, readTimeoutMs = 10000)
        .asString

      if (response.is2xx) {
        val summary = callOpenAIService(response.body)
        summary
      } else {
        throw new Exception(s"Failed to fetch URL: ${response.code} ${response.body}")
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        "Error occurred during summarization"
    }
  }

  private def callOpenAIService(url: String): String = {
    
    //include
    // val apikey = ""
    val endpoint = "https://api.openai.com/v1/chat/completions"

    val jsonBody: String = Json.obj(
      "model" -> "gpt-3.5-turbo",
      "messages" -> Json.arr(
        Json.obj(
          "role" -> "system",
          "content" -> "You are a helpful assistant."
        ),
        Json.obj(
          "role" -> "user",
          "content" -> s"Summarize the following URL content:\n$url"
        )
      ),
      "max_tokens" -> 150
    ).toString()

    try {
      val response: HttpResponse[String] = Http(endpoint)
        .postData(jsonBody)
        .header("Content-Type", "application/json")
        .header("Authorization", s"Bearer $apiKey")
        .timeout(connTimeoutMs = 5000, readTimeoutMs = 10000)
        .asString

      if (response.is2xx) {
        val jsonResponse: JsValue = Json.parse(response.body)
        (jsonResponse \ "choices" \ 0 \ "message" \ "content").as[String].trim      
      } else {
        throw new Exception(s"Failed to call OpenAI API: ${response.code} ${response.body}")
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        "Error occurred during summarization"
    }
  }
}
