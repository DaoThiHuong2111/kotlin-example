package org.example.app

import kotlin.system.measureTimeMillis
import kotlinx.coroutines.*

/**
 * Main application - chạy ví dụ về Inline Keywords
 */
fun main() {
    // Import và gọi các functions từ InlineKeywordsExample
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
            if (attemptCount <= 1) { // Sửa condition để tránh warning
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

// Inline functions
inline fun executeWithLog(action: () -> Unit) {
    println("🔧 Bắt đầu thực hiện...")
    action()
    println("✅ Hoàn thành!")
}

fun normalFunction(block: () -> Unit) {
    block()
}

inline fun inlineFunction(block: () -> Unit) {
    block()
}

inline fun processData(
    data: String,
    inlineProcessor: (String) -> String,
    noinline callback: (String) -> Unit
) {
    println("📝 Xử lý dữ liệu: $data")
    val processed = inlineProcessor(data)
    println("⚡ Kết quả xử lý: $processed")
    val storedCallback = callback
    storedCallback(processed)
    callback("Xử lý hoàn tất!")
}

inline fun executeAsync(crossinline action: () -> Unit) {
    Thread {
        println("🧵 Chạy trong thread khác...")
        action()
        println("🏁 Thread hoàn thành")
    }.start()
}

inline fun executeWithDelay(
    delayMs: Long,
    crossinline action: () -> Unit
) = runBlocking {
    println("⏳ Đợi ${delayMs}ms...")
    delay(delayMs)
    action()
}

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

// Bỏ inline cho function không có lambda parameters để tránh warning
fun <T> validate(
    value: T,
    vararg validators: (T) -> Boolean,
    onError: (String) -> Unit = { println("❌ Lỗi: $it") }
): Boolean {
    validators.forEachIndexed { index, validator ->
        if (!validator(value)) {
            onError("Validation failed at step ${index + 1}")
            return false
        }
    }
    return true
}

fun performanceDemo() {
    println("\n📊 === PERFORMANCE COMPARISON ===")

    val iterations = 1_000_000

    val normalTime = measureTimeMillis {
        repeat(iterations) {
            normalFunction { /* empty */ }
        }
    }

    val inlineTime = measureTimeMillis {
        repeat(iterations) {
            inlineFunction { /* empty */ }
        }
    }

    println("🔄 Normal function: ${normalTime}ms")
    println("⚡ Inline function: ${inlineTime}ms")
    println("🚀 Cải thiện: ${if (normalTime > 0) ((normalTime - inlineTime) * 100 / normalTime) else 0}%")
}

fun returnBehaviorDemo() {
    println("\n🔄 === RETURN BEHAVIOR DEMO ===")

    fun testInlineReturn() {
        println("🎯 Trước khi gọi inline function")
        executeWithLog {
            println("📝 Trong inline function")
            return
        }
        println("❌ Dòng này sẽ KHÔNG được thực hiện")
    }

    fun testCrossinlineReturn() {
        println("🎯 Test crossinline function")
        executeAsync {
            println("📝 Trong crossinline function")
        }
        println("✅ Dòng này VẪN được thực hiện")
        Thread.sleep(100)
    }

    testInlineReturn()
    testCrossinlineReturn()
}
