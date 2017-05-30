/*
 * Copyright 2017 Barclays Africa Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.spline.common

import za.co.absa.spline.common.TypeFreaks.`not a subtype of`

import scala.language.implicitConversions

/**
  * The object contains implicit conversions of types to options.
  */
object OptionImplicits {

  /**
    * The method coverts on type to an [[scala.Option Option]]
    *
    * @param o An instance that will be converted
    * @tparam T A source type
    * @return An option
    */
  implicit def anyToOption[T <: Any : `not a subtype of`[Option[_]]#λ : Manifest](o: T): Option[T] = Option(o)
}
