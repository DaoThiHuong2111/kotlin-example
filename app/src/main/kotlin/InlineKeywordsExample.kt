/**
 * KOTLIN INLINE KEYWORDS EXAMPLE
 *
 * Giải thích chi tiết về inline, noinline, và crossinline trong Kotlin
 * với các ví dụ thực tế và usecase cụ thể.
 */

import kotlin.system.measureTimeMillis
import kotlinx.coroutines.*

/**
 * 1. INLINE FUNCTIONS - Hàm Inline
 *
 * Inline functions được compiler thay thế trực tiếp tại nơi gọi thay vì tạo function call.
 * Điều này giúp:
 * - Tránh overhead của function call
 * - Tăng hiệu suất với higher-order functions
 * - Cho phép non-local returns
 */

// Ví dụ 1: Inline function cơ bản
inline fun executeWithLog(action: () -> Unit) {
    println("🔧 Bắt đầu thực hiện...")
    action()
    println("✅ Hoàn thành!")
}

// Ví dụ 2: So sánh performance
fun normalFunction(block: () -> Unit) {
    block()
}

inline fun inlineFunction(block: () -> Unit) {
    block()
}

/**
 * 2. NOINLINE PARAMETER - Tham số không inline
 *
 * Khi một inline function có nhiều function parameters,
 * bạn có thể dùng noinline để ngăn một số parameters được inline hóa.
 *
 * Lý do sử dụng noinline:
 * - Muốn lưu trữ function parameter để dùng sau
 * - Function parameter quá phức tạp
 * - Cần pass function như một object
 */

// Ví dụ 3: Mixed inline function
inline fun processData(
    data: String,
    inlineProcessor: (String) -> String,      // Sẽ được inline
    noinline callback: (String) -> Unit       // KHÔNG được inline
) {
    println("📝 Xử lý dữ liệu: $data")

    // Xử lý với inline function
    val processed = inlineProcessor(data)
    println("⚡ Kết quả xử lý: $processed")

    // Có thể lưu trữ noinline function
    val storedCallback = callback
    storedCallback(processed)

    // Gọi callback một lần nữa
    callback("Xử lý hoàn tất!")
}

/**
 * 3. CROSSINLINE PARAMETER - Tham số crossinline
 *
 * Crossinline được dùng khi:
 * - Muốn inline function parameter
 * - Nhưng KHÔNG cho phép non-local returns
 * - Function parameter được gọi trong lambda hoặc nested function
 */

// Ví dụ 4: Crossinline với threading
inline fun executeAsync(crossinline action: () -> Unit) {
    Thread {
        println("🧵 Chạy trong thread khác...")
        action()  // Có thể gọi trong lambda
        println("🏁 Thread hoàn thành")
    }.start()
}

// Ví dụ 5: Crossinline với coroutines
inline fun executeWithDelay(
    delayMs: Long,
    crossinline action: () -> Unit
) = runBlocking {
    println("⏳ Đợi ${delayMs}ms...")
    delay(delayMs)
    action()
}

/**
 * 4. USECASE THỰC TẾ
 */

// Usecase 1: HTML DSL Builder
inline fun html(init: HtmlBuilder.() -> Unit): String {
    val builder = HtmlBuilder()
    builder.init()
    return builder.build()
}

class HtmlBuilder {
    private val content = StringBuilder()

    fun head(init: () -> Unit) {
        content.append("<head>")
        init()
        content.append("</head>")
    }

    fun body(init: () -> Unit) {
        content.append("<body>")
        init()
        content.append("</body>")
    }

    fun title(text: String) {
        content.append("<title>$text</title>")
    }

    fun h1(text: String) {
        content.append("<h1>$text</h1>")
    }

    fun p(text: String) {
        content.append("<p>$text</p>")
    }

    fun build() = content.toString()
}

// Usecase 2: Database Transaction
inline fun <T> transaction(crossinline block: () -> T): T {
    println("📊 Bắt đầu transaction...")
    return try {
        val result = block()
        println("💾 Commit transaction")
        result
    } catch (e: Exception) {
        println("🔄 Rollback transaction: ${e.message}")
        throw e
    }
}

// Usecase 3: Retry Mechanism
inline fun <T> retry(
    maxAttempts: Int = 3,
    crossinline onError: (Exception, Int) -> Unit = { _, _ -> },
    block: () -> T
): T {
    var lastException: Exception? = null

    repeat(maxAttempts) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            onError(e, attempt + 1)
            if (attempt < maxAttempts - 1) {
                println("🔄 Thử lại lần ${attempt + 2}...")
                Thread.sleep(100L * (attempt + 1)) // Sửa lỗi type casting
            }
        }
    }

    throw lastException ?: RuntimeException("Retry failed")
}

// Usecase 4: Performance Measurement
inline fun <T> measureTime(
    operation: String,
    block: () -> T
): T {
    val startTime = System.currentTimeMillis()
    val result = block()
    val endTime = System.currentTimeMillis()
    println("⏱️ $operation: ${endTime - startTime}ms")
    return result
}

// Usecase 5: Validation Chain
inline fun <T> validate(
    value: T,
    vararg validators: (T) -> Boolean,
    noinline onError: (String) -> Unit = { println("❌ Lỗi: $it") }
): Boolean {
    validators.forEachIndexed { index, validator ->
        if (!validator(value)) {
            onError("Validation failed at step ${index + 1}")
            return false
        }
    }
    return true
}

/**
 * 5. PERFORMANCE COMPARISON
 */
fun performanceDemo() {
    println("\n📊 === PERFORMANCE COMPARISON ===")

    val iterations = 1_000_000

    // Test normal function
    val normalTime = measureTimeMillis {
        repeat(iterations) {
            normalFunction { /* empty */ }
        }
    }

    // Test inline function
    val inlineTime = measureTimeMillis {
        repeat(iterations) {
            inlineFunction { /* empty */ }
        }
    }

    println("🔄 Normal function: ${normalTime}ms")
    println("⚡ Inline function: ${inlineTime}ms")
    println("🚀 Cải thiện: ${if (normalTime > 0) ((normalTime - inlineTime) * 100 / normalTime) else 0}%")
}

/**
 * 6. RETURN BEHAVIOR DEMO
 */
fun returnBehaviorDemo() {
    println("\n🔄 === RETURN BEHAVIOR DEMO ===")

    // Với inline function - non-local return
    fun testInlineReturn() {
        println("🎯 Trước khi gọi inline function")
        executeWithLog {
            println("📝 Trong inline function")
            return // Return từ testInlineReturn(), KHÔNG phải từ lambda
        }
        println("❌ Dòng này sẽ KHÔNG được thực hiện")
    }

    // Với crossinline - không cho phép return
    fun testCrossinlineReturn() {
        println("🎯 Test crossinline function")
        executeAsync {
            println("📝 Trong crossinline function")
            // return // ❌ Sẽ gây lỗi compilation nếu uncomment
        }
        println("✅ Dòng này VẪN được thực hiện")
        Thread.sleep(100) // Đợi thread hoàn thành
    }

    testInlineReturn()
    testCrossinlineReturn()
}

/**
 * 7. MAIN DEMO FUNCTION
 */
fun main() {
    println("🎉 === KOTLIN INLINE KEYWORDS DEMO ===\n")

    // 1. Inline function cơ bản
    println("1️⃣ Inline Function:")
    executeWithLog {
        println("   📝 Thực hiện task quan trọng")
    }

    // 2. Mixed inline/noinline
    println("\n2️⃣ Mixed Inline/Noinline:")
    processData(
        data = "Hello Kotlin",
        inlineProcessor = { it.uppercase() },
        callback = { result -> println("   📢 Callback: $result") }
    )

    // 3. Crossinline
    println("\n3️⃣ Crossinline:")
    executeWithDelay(500) {
        println("   ⏰ Delayed execution hoàn thành!")
    }

    // 4. HTML DSL
    println("\n4️⃣ HTML DSL:")
    val htmlContent = html {
        head {
            title("My Kotlin App")
        }
        body {
            h1("Welcome!")
            p("Đây là ví dụ về inline functions trong Kotlin")
        }
    }
    println("   🌐 HTML: $htmlContent")

    // 5. Transaction
    println("\n5️⃣ Transaction:")
    try {
        val result = transaction {
            // Giả lập database work
            println("   💾 Thực hiện database operations...")
            "Data saved successfully"
        }
        println("   ✅ Kết quả: $result")
    } catch (e: Exception) {
        println("   ❌ Transaction failed: ${e.message}")
    }

    // 6. Retry mechanism
    println("\n6️⃣ Retry Mechanism:")
    try {
        var attemptCount = 0
        val result = retry(
            maxAttempts = 3,
            onError = { error, attempt ->
                println("   ⚠️ Lỗi lần $attempt: ${error.message}")
            }
        ) {
            attemptCount++
            if (attemptCount < 2) {
                throw RuntimeException("Simulated failure")
            }
            "Success after retry!"
        }
        println("   🎉 Kết quả: $result")
    } catch (e: Exception) {
        println("   💥 Tất cả attempts đều thất bại: ${e.message}")
    }

    // 7. Performance measurement
    println("\n7️⃣ Performance Measurement:")
    val result = measureTime("Complex calculation") {
        Thread.sleep(200)
        (1..1000).sum()
    }
    println("   🔢 Kết quả tính toán: $result")

    // 8. Validation
    println("\n8️⃣ Validation Chain:")
    val email = "user@example.com"
    val isValid = validate(
        email,
        { it.contains("@") },
        { it.length > 5 },
        { it.endsWith(".com") }
    ) { error -> println("   ❌ Validation lỗi: $error") }
    println("   ${if (isValid) "✅" else "❌"} Email valid: $isValid")

    // 9. Performance comparison
    performanceDemo()

    // 10. Return behavior
    returnBehaviorDemo()
}

/**
 * 📚 === TÓM TẮT VÀ LƯU Ý QUAN TRỌNG ===
 *
 * 🔹 INLINE:
 *   ✅ Sử dụng khi: Có function parameters (lambdas)
 *   ✅ Lợi ích: Tránh overhead, cho phép non-local returns
 *   ❌ Tránh: Function quá lớn, không có lambda parameters
 *
 * 🔹 NOINLINE:
 *   ✅ Sử dụng khi: Cần lưu trữ function parameter
 *   ✅ Lợi ích: Linh hoạt trong việc sử dụng function
 *   ❌ Tránh: Overuse - mất lợi ích của inline
 *
 * 🔹 CROSSINLINE:
 *   ✅ Sử dụng khi: Gọi lambda trong nested context
 *   ✅ Lợi ích: Bảo vệ khỏi unexpected returns
 *   ❌ Tránh: Khi cần non-local returns
 *
 * 🎯 BEST PRACTICES:
 * 1. Chỉ inline functions có lambda parameters
 * 2. Giữ inline functions ngắn gọn (< 3 dòng)
 * 3. Sử dụng noinline khi cần store function
 * 4. Sử dụng crossinline trong async contexts
 * 5. Đo performance trước khi inline
 * 6. Tránh inline functions quá phức tạp
 *
 * ⚠️ CẢNH BÁO:
 * - Inline functions làm tăng kích thước bytecode
 * - Không inline functions được gọi nhiều nơi
 * - Cẩn thận với non-local returns
 * - Test thoroughly khi sử dụng crossinline
 */
