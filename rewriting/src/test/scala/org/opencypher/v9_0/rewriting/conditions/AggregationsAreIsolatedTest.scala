/*
 * Copyright © 2002-2018 Neo4j Sweden AB (http://neo4j.com)
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
package org.opencypher.v9_0.rewriting.conditions

import org.opencypher.v9_0.ast.AstConstructionTestSupport
import org.opencypher.v9_0.expressions.CountStar
import org.opencypher.v9_0.expressions.Equals
import org.opencypher.v9_0.expressions.Expression
import org.opencypher.v9_0.expressions.FunctionInvocation
import org.opencypher.v9_0.expressions.FunctionName
import org.opencypher.v9_0.expressions.UnsignedDecimalIntegerLiteral
import org.opencypher.v9_0.util.test_helpers.CypherFunSuite

class AggregationsAreIsolatedTest extends CypherFunSuite with AstConstructionTestSupport {

  private val condition: Any => Seq[String] = aggregationsAreIsolated

  test("happy when aggregation are top level in expressions") {
    val ast: Expression = CountStar()_

    condition(ast) shouldBe empty
  }

  test("unhappy when aggregation is sub-expression of the expressions") {
    val ast: Expression = Equals(CountStar()_, UnsignedDecimalIntegerLiteral("42")_)_

    condition(ast) should equal(Seq(s"Expression $ast contains child expressions which are aggregations"))
  }

  test("unhappy when aggregations are both top-level and sub-expression of the expression") {
    val equals: Expression = Equals(CountStar()_, UnsignedDecimalIntegerLiteral("42")_)_
    val ast: Expression = FunctionInvocation(FunctionName("count")_, equals)_

    condition(ast) should equal(Seq(s"Expression $equals contains child expressions which are aggregations"))
  }
}
