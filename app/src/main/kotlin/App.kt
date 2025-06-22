package org.example.app

import org.example.utils.Printer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

/**
 * Ví dụ về các tính năng mới từ Kotlin 1.8 đến 2.1.21
 */
fun main() {
    println("Các tính năng mới từ Kotlin 1.8 đến 2.1.21")

    // Sử dụng Printer từ utils
    val printer = Printer("Hello from Kotlin 2.1.20!")
    printer.printMessage()

    // Gọi các hàm ví dụ
    valueClassExample()
    sealedInterfaceExample()
    contextReceiversExample()
    durationExample()
    optInExample()
    explicitBackingFieldExample()
    rangesExample()
    typeInferenceExample()
    stringTemplateExample()
    coroutinesExample()
}

/**
 * 1. Value Classes (Kotlin 1.8+)
 * 
 * Value classes là một tính năng cho phép tạo wrapper cho một giá trị đơn mà không tạo ra
 * chi phí runtime bổ sung. Chúng được biên dịch thành kiểu cơ bản trong hầu hết các trường hợp.
 * 
 * Trong Kotlin 1.8+, value classes đã được cải thiện và ổn định hơn, thay thế cho inline classes.
 */
@JvmInline
value class UserId(val id: Int)

fun valueClassExample() {
    println("\n--- Value Classes Example ---")

    // Sử dụng value class
    val userId = UserId(123)
    println("User ID: ${userId.id}")

    // Value class được biên dịch thành kiểu cơ bản (Int) trong hầu hết các trường hợp
    // nên không tạo ra chi phí runtime bổ sung
}

/**
 * 2. Sealed Interfaces (Kotlin 1.8+)
 * 
 * Sealed interfaces cho phép tạo ra các hierarchy kín, tương tự như sealed classes.
 * Điều này giúp kiểm soát tốt hơn các kiểu có thể implement interface.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String) : Result<Nothing>
    data object Loading : Result<Nothing>
}

fun sealedInterfaceExample() {
    println("\n--- Sealed Interface Example ---")

    // Sử dụng sealed interface
    val result: Result<String> = Result.Success("Data loaded successfully")

    // Khi sử dụng when với sealed interface, compiler đảm bảo tất cả các trường hợp được xử lý
    val message = when (result) {
        is Result.Success -> "Success: ${result.data}"
        is Result.Error -> "Error: ${result.message}"
        is Result.Loading -> "Loading..."
    }

    println(message)
}

/**
 * 3. Context Receivers (Kotlin 1.8+)
 * 
 * Context receivers là một tính năng mới cho phép một hàm hoặc thuộc tính
 * nhận nhiều receivers. Điều này mở rộng khả năng của extension functions.
 */
// Định nghĩa các context classes
class Logger {
    fun log(message: String) {
        println("LOG: $message")
    }
}

class Database {
    fun query(sql: String): String {
        return "Result of: $sql"
    }
}

// Sử dụng context receivers
context(Logger, Database)
fun executeAndLog(sql: String) {
    log("Executing query: $sql")
    val result = query(sql)
    log("Query result: $result")
}

fun contextReceiversExample() {
    println("\n--- Context Receivers Example ---")

    val logger = Logger()
    val database = Database()

    // Sử dụng with để cung cấp các context receivers
    with(logger) {
        with(database) {
            executeAndLog("SELECT * FROM users")
        }
    }
}

/**
 * 4. Duration API Improvements (Kotlin 1.8+)
 * 
 * Kotlin 1.8+ đã cải thiện API Duration, làm cho việc làm việc với thời gian dễ dàng hơn.
 */
fun durationExample() {
    println("\n--- Duration API Example ---")

    // Tạo Duration bằng cách sử dụng extension properties
    val fiveSeconds = 5.seconds
    println("Five seconds: $fiveSeconds")

    // Thực hiện các phép toán với Duration
    val tenSeconds = fiveSeconds * 2
    println("Ten seconds: $tenSeconds")

    // Chuyển đổi giữa các đơn vị thời gian
    println("Ten seconds in milliseconds: ${tenSeconds.inWholeMilliseconds}")

    // Sử dụng Duration với coroutines
    runBlocking {
        println("Waiting for 1 second...")
        delay(1.seconds)
        println("Done waiting!")
    }
}

/**
 * 5. OptIn Annotations (Kotlin 1.8+)
 * 
 * OptIn annotations thay thế cho @Experimental và @UseExperimental,
 * cung cấp cách tốt hơn để làm việc với các API thử nghiệm.
 */
@RequiresOptIn(message = "This API is experimental. It may be changed in the future without notice.")
annotation class ExperimentalAPI

@ExperimentalAPI
fun experimentalFunction() {
    println("This is an experimental function")
}

@OptIn(ExperimentalAPI::class)
fun optInExample() {
    println("\n--- OptIn Annotations Example ---")

    // Sử dụng hàm thử nghiệm với @OptIn
    experimentalFunction()
}

/**
 * 6. Explicit Backing Fields (Kotlin 2.0)
 * 
 * Kotlin 2.0 giới thiệu khả năng kiểm soát tốt hơn cách lưu trữ dữ liệu
 * thông qua các thuộc tính và backing fields.
 */
class Person {
    // Sử dụng backing field thông qua identifier 'field'
    var name: String = ""
        set(value) {
            println("Setting name to: $value")
            field = value
        }

    // Property không có backing field
    var displayName: String
        get() = name.uppercase()
        set(value) { name = value.lowercase() }
}

fun explicitBackingFieldExample() {
    println("\n--- Explicit Backing Fields Example ---")

    val person = Person()
    person.name = "John"
    println("Name: ${person.name}")
    println("Display name: ${person.displayName}")

    person.displayName = "ALICE"
    println("After setting display name:")
    println("Name: ${person.name}")
    println("Display name: ${person.displayName}")
}

/**
 * 7. Ranges và Progressions Improvements (Kotlin 1.9+)
 * 
 * Kotlin 1.9+ cải thiện API cho ranges và progressions,
 * làm cho chúng linh hoạt và mạnh mẽ hơn.
 */
fun rangesExample() {
    println("\n--- Ranges and Progressions Example ---")

    // Sử dụng ranges với step
    val range = 1..10 step 2
    println("Range with step 2: $range")
    println("Elements: ${range.toList()}")

    // Sử dụng ranges với Char
    val charRange = 'a'..'z'
    println("Char range: $charRange")
    println("Contains 'x': ${'x' in charRange}")

    // Sử dụng downTo
    val reverseRange = 10 downTo 1 step 3
    println("Reverse range with step 3: ${reverseRange.toList()}")
}

/**
 * 8. Improved Type Inference (Kotlin 1.8+)
 * 
 * Kotlin 1.8+ cải thiện type inference, cho phép compiler suy luận
 * kiểu dữ liệu chính xác hơn trong nhiều trường hợp.
 */
fun <T> identity(value: T): T = value

fun typeInferenceExample() {
    println("\n--- Improved Type Inference Example ---")

    // Compiler có thể suy luận kiểu dữ liệu chính xác hơn
    val result = identity("Hello")
    println("Result type: ${result::class.simpleName}, value: $result")

    // Suy luận kiểu với lambda và SAM conversions
    val numbers = listOf(1, 2, 3, 4, 5)
    val evenNumbers = numbers.filter { it % 2 == 0 }
    println("Even numbers: $evenNumbers")

    // Suy luận kiểu với builder patterns
    val map = buildMap {
        put("one", 1)
        put("two", 2)
    }
    println("Map: $map")
}

/**
 * 9. String Templates Improvements (Kotlin 1.9+)
 * 
 * Kotlin 1.9+ cải thiện string templates, cho phép sử dụng
 * các biểu thức phức tạp hơn trong template.
 */
fun stringTemplateExample() {
    println("\n--- String Templates Example ---")

    val name = "Kotlin"
    val version = "2.1.20"

    // String template cơ bản
    println("Hello, $name $version!")

    // String template với biểu thức
    println("Length of name: ${name.length}")

    // String template với biểu thức phức tạp
    println("Is version greater than 2.0? ${version.split(".").first().toInt() >= 2}")

    // Raw string với templates
    val code = """
        fun main() {
            println("Hello, $name!")
        }
    """.trimIndent()

    println("Code snippet:\n$code")
}

/**
 * 10. Coroutines Improvements (Kotlin 1.8+ to 2.1.21)
 * 
 * Kotlin Coroutines đã được cải thiện đáng kể từ phiên bản 1.8 đến 2.1.21,
 * với nhiều tính năng mới và cải tiến hiệu suất.
 */
fun coroutinesExample() = runBlocking {
    println("\n--- Coroutines Improvements Example ---")

    // Flow API improvements
    val flow = flow {
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }

    // Collect với timeout
    withTimeoutOrNull(350) {
        flow.collect { value ->
            println("Received: $value")
        }
    }

    // Structured concurrency
    coroutineScope {
        launch {
            delay(200)
            println("Task 1 completed")
        }

        launch {
            delay(100)
            println("Task 2 completed")
        }

        println("Waiting for all tasks to complete...")
    }

    println("All tasks completed")
}
