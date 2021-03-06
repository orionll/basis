//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2015 Chris Sachs
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.form

import basis._
import basis.collections._
import basis.text._

private[form] trait JsonFactory {
  type JsonValue
  type JsonObject    <: JsonValue
  type JsonArray     <: JsonValue
  type JsonString    <: JsonValue
  type JsonNumber    <: JsonValue
  type JsonBoolean   <: JsonValue
  type JsonNull      <: JsonValue
  type JsonUndefined <: JsonValue

  def JsonObjectValue(json: JsonObject): JsonValue
  def JsonArrayValue(json: JsonArray): JsonValue
  def JsonStringValue(json: JsonString): JsonValue

  def JsonObjectBuilder: Builder[(String, JsonValue)] with State[JsonObject]
  def JsonArrayBuilder: Builder[JsonValue] with State[JsonArray]
  def JsonStringBuilder: StringBuilder with State[JsonString]

  def JsonString(value: String): JsonString
  def JsonNumber(value: String): JsonNumber
  def JsonInteger(value: String): JsonNumber
  def JsonTrue: JsonBoolean
  def JsonFalse: JsonBoolean
  def JsonNull: JsonNull
  def JsonUndefined: JsonUndefined

  def JsonNew(identifier: String, arguments: JsonArray): JsonValue
}
