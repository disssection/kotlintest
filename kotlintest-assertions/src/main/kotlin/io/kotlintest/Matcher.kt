package io.kotlintest

/**
 * A [Matcher] is the main abstraction in the assertions library.
 *
 * Implementations contain a single function, called 'test', which
 * accepts a value of type T and returns an instance of [Result].
 * This [Result] return value contains the state of the assertion
 * after it has been evaluted.
 *
 * A matcher will typically be invoked when used with the `should`
 * functions in the assertions DSL. For example, `2 should beLessThan(4)`
 *
 */
interface Matcher<T> {

  fun test(value: T): Result

  fun invert(): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val result = this@Matcher.test(value)
      return Result(!result.passed, result.negatedFailureMessage, result.failureMessage)
    }
  }

  infix fun <U> compose(fn: (U) -> T): Matcher<U> = object : Matcher<U> {
    override fun test(value: U): Result = this@Matcher.test(fn(value))
  }

  infix fun and(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val r = this@Matcher.test(value)
      return if (!r.passed)
        r
      else
        other.test(value)
    }
  }

  infix fun or(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val r = this@Matcher.test(value)
      return if (r.passed)
        r
      else
        other.test(value)
    }
  }
}

/**
 * The [Result] class contains the result of an evaluation of a matcher.
 *
 * @param passed set to true if the matcher indicated this was a valid
 * value and false if the matcher indicated an invalid value
 *
 * @param failureMessage a message indicating why the evaluation failed
 * for when this matcher is used in the positive sense. For example,
 * if a size matcher was used like `mylist should haveSize(5)` then
 * an appropriate error message would be "list should be size 5".
 *
 * @param negatedFailureMessage a message indicating why the evaluation
 * failed for when this matcher is used in the negative sense. For example,
 * if a size matcher was used like `mylist shouldNot haveSize(5)` then
 * an appropriate negated failure would be "List should not have size 5".
 */
data class Result(val passed: Boolean, val failureMessage: String, val negatedFailureMessage: String) {
  @Deprecated("Add a specific negatedFailureMessage")
  constructor(passed: Boolean, failureMessage: String) : this(passed, failureMessage, "Test passed which should have failed: $failureMessage")
}
