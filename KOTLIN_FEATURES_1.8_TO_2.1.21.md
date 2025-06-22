# Các tính năng mới từ Kotlin 1.8 đến 2.1.21

Dự án này minh họa các tính năng mới được giới thiệu trong Kotlin từ phiên bản 1.8 đến 2.1.21 với các ví dụ thực tế và giải thích chi tiết bằng tiếng Việt.

## Cách chạy ví dụ

```bash
./gradlew :app:run
```

## Danh sách các tính năng được minh họa

### 1. Value Classes (Kotlin 1.8+)

**Mô tả**: Value classes cho phép tạo wrapper cho một giá trị đơn mà không tạo ra chi phí runtime bổ sung. Chúng được biên dịch thành kiểu cơ bản trong hầu hết các trường hợp.

**Ví dụ**:
```kotlin
@JvmInline
value class UserId(val id: Int)

val userId = UserId(123)
println("User ID: ${userId.id}")
```

**Lợi ích**:
- Không có chi phí runtime bổ sung
- Type safety tốt hơn
- Thay thế cho inline classes (deprecated)

### 2. Sealed Interfaces (Kotlin 1.8+)

**Mô tả**: Sealed interfaces cho phép tạo ra các hierarchy kín, tương tự như sealed classes. Điều này giúp kiểm soát tốt hơn các kiểu có thể implement interface.

**Ví dụ**:
```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String) : Result<Nothing>
    data object Loading : Result<Nothing>
}
```

**Lợi ích**:
- Exhaustive when expressions
- Kiểm soát tốt hơn các implementations
- Hỗ trợ multiple inheritance thông qua interfaces

### 3. Context Receivers (Kotlin 1.8+)

**Mô tả**: Context receivers cho phép một hàm hoặc thuộc tính nhận nhiều receivers. Điều này mở rộng khả năng của extension functions.

**Ví dụ**:
```kotlin
context(Logger, Database)
fun executeAndLog(sql: String) {
    log("Executing query: $sql")
    val result = query(sql)
    log("Query result: $result")
}
```

**Lưu ý**: Tính năng này đang được deprecated và sẽ được thay thế bởi context parameters trong Kotlin 2.2.

### 4. Duration API Improvements (Kotlin 1.8+)

**Mô tả**: Kotlin 1.8+ đã cải thiện API Duration, làm cho việc làm việc với thời gian dễ dàng hơn.

**Ví dụ**:
```kotlin
val fiveSeconds = 5.seconds
val tenSeconds = fiveSeconds * 2
println("Ten seconds in milliseconds: ${tenSeconds.inWholeMilliseconds}")

// Sử dụng với coroutines
delay(1.seconds)
```

**Lợi ích**:
- Syntax tự nhiên hơn
- Tích hợp tốt với coroutines
- Type-safe time operations

### 5. OptIn Annotations (Kotlin 1.8+)

**Mô tả**: OptIn annotations thay thế cho @Experimental và @UseExperimental, cung cấp cách tốt hơn để làm việc với các API thử nghiệm.

**Ví dụ**:
```kotlin
@RequiresOptIn(message = "This API is experimental.")
annotation class ExperimentalAPI

@ExperimentalAPI
fun experimentalFunction() {
    println("This is an experimental function")
}

@OptIn(ExperimentalAPI::class)
fun useExperimentalAPI() {
    experimentalFunction()
}
```

**Lợi ích**:
- Kiểm soát tốt hơn việc sử dụng experimental APIs
- Thông báo rõ ràng hơn cho developers
- Thay thế cho các annotation cũ

### 6. Explicit Backing Fields (Kotlin 2.0)

**Mô tả**: Kotlin 2.0 giới thiệu khả năng kiểm soát tốt hơn cách lưu trữ dữ liệu thông qua các thuộc tính và backing fields.

**Ví dụ**:
```kotlin
class Person {
    var name: String = ""
        set(value) {
            println("Setting name to: $value")
            field = value
        }

    var displayName: String
        get() = name.uppercase()
        set(value) { name = value.lowercase() }
}
```

**Lợi ích**:
- Kiểm soát tốt hơn việc lưu trữ dữ liệu
- Custom logic trong getters/setters
- Tối ưu hóa memory usage

### 7. Ranges và Progressions Improvements (Kotlin 1.9+)

**Mô tả**: Kotlin 1.9+ cải thiện API cho ranges và progressions, làm cho chúng linh hoạt và mạnh mẽ hơn.

**Ví dụ**:
```kotlin
val range = 1..10 step 2
val charRange = 'a'..'z'
val reverseRange = 10 downTo 1 step 3
```

**Lợi ích**:
- API linh hoạt hơn
- Hỗ trợ nhiều kiểu dữ liệu
- Performance improvements

### 8. Improved Type Inference (Kotlin 1.8+)

**Mô tả**: Kotlin 1.8+ cải thiện type inference, cho phép compiler suy luận kiểu dữ liệu chính xác hơn trong nhiều trường hợp.

**Ví dụ**:
```kotlin
val result = identity("Hello") // Compiler tự suy luận kiểu String
val evenNumbers = numbers.filter { it % 2 == 0 } // Tự suy luận List<Int>
val map = buildMap {
    put("one", 1)
    put("two", 2)
} // Tự suy luận Map<String, Int>
```

**Lợi ích**:
- Ít cần khai báo kiểu explicit
- Code ngắn gọn hơn
- Compiler thông minh hơn

### 9. String Templates Improvements (Kotlin 1.9+)

**Mô tả**: Kotlin 1.9+ cải thiện string templates, cho phép sử dụng các biểu thức phức tạp hơn trong template.

**Ví dụ**:
```kotlin
val name = "Kotlin"
val version = "2.1.20"

println("Hello, $name $version!")
println("Length of name: ${name.length}")
println("Is version greater than 2.0? ${version.split(".").first().toInt() >= 2}")
```

**Lợi ích**:
- Biểu thức phức tạp hơn trong templates
- Performance improvements
- Better IDE support

### 10. Coroutines Improvements (Kotlin 1.8+ to 2.1.21)

**Mô tả**: Kotlin Coroutines đã được cải thiện đáng kể từ phiên bản 1.8 đến 2.1.21, với nhiều tính năng mới và cải tiến hiệu suất.

**Ví dụ**:
```kotlin
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
    launch { /* task 1 */ }
    launch { /* task 2 */ }
}
```

**Lợi ích**:
- Flow API mạnh mẽ hơn
- Structured concurrency tốt hơn
- Performance improvements
- Better error handling

## Cấu hình dự án

### Dependencies được sử dụng

- Kotlin 2.1.20
- kotlinx-coroutines-core 1.9.0
- kotlinx-serialization-json 1.7.3
- kotlinx-datetime 0.6.1

### Compiler flags

- `-Xcontext-receivers`: Để enable context receivers (deprecated)

### Build configuration

Dự án sử dụng Gradle với Kotlin DSL và version catalog để quản lý dependencies.

## Kết luận

Các tính năng mới từ Kotlin 1.8 đến 2.1.21 mang lại nhiều cải tiến quan trọng:

1. **Type Safety**: Value classes, sealed interfaces
2. **Expressiveness**: Context receivers, improved string templates
3. **Performance**: Duration API, coroutines improvements
4. **Developer Experience**: OptIn annotations, improved type inference
5. **Modern APIs**: Ranges improvements, explicit backing fields

Tất cả các ví dụ trong dự án này đều có thể chạy và được giải thích chi tiết bằng tiếng Việt để dễ hiểu và áp dụng.
