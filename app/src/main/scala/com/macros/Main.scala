package com.macros

import com.domain.TestResponse

object Main extends App{
  val testResponse: TestResponse = TestResponse("test")
  println(testResponse)
  println(TestResponse.testResponseDecoder)
  println(TestResponse.testResponseEncoder)
}
