package com.domain

import com.macros.generator.generateCachedImplicits
import io.circe.generic.auto._
import io.circe.Decoder
import io.circe.Encoder

@generateCachedImplicits(Seq("Encoder", "Decoder"))
case class TestResponse(str: String)

object TestResponse {
  def method = "testmethod"
}