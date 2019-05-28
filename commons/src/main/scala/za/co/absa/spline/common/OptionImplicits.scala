/*
 * Copyright 2017 ABSA Group Limited
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

import org.apache.commons.lang.StringUtils
import za.co.absa.spline.common.TypeConstraints.not

import scala.language.implicitConversions


object OptionImplicits {

  @deprecated
  implicit def anyToOption[T <: Any : not[Option[_]]#λ : Manifest](o: T): Option[T] = Option(o)

  implicit class StringWrapper(s: String) {
    def nonBlankOption: Option[String] = Option(StringUtils.trimToNull(s))
  }

  implicit class AnyWrapper[A](a: A) {
    def optionally[B](applyFn: (A, B) => A, maybeArg: Option[B]): A =
      maybeArg.map(applyFn(a, _)).getOrElse(a)
  }

}
