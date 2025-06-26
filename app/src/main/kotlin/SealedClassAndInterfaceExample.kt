package org.example.app

/**
 * SEALED CLASS VÀ SEALED INTERFACE TRONG KOTLIN
 * 
 * File này giải thích chi tiết về sealed class và sealed interface,
 * tại sao cần sử dụng chúng, và so sánh giữa hai khái niệm này.
 */

// ================================
// 1. SEALED CLASS
// ================================

/**
 * SEALED CLASS LÀ GÌ?
 * 
 * Sealed class là một class đặc biệt trong Kotlin cho phép bạn định nghĩa
 * một tập hợp hạn chế các subclass. Tất cả subclass phải được khai báo
 * trong cùng một file với sealed class.
 * 
 * TẠI SAO CẦN SEALED CLASS?
 * 1. Type Safety: Đảm bảo chỉ có những subclass được định nghĩa mới có thể tồn tại
 * 2. Exhaustive When: Compiler đảm bảo tất cả trường hợp được xử lý trong when expression
 * 3. Restricted Hierarchy: Kiểm soát được hierarchy của class
 */

// Ví dụ 1: Sealed class cho trạng thái UI
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: String) : UiState()
    data class Error(val exception: Throwable) : UiState()
    object Empty : UiState()
}

// Ví dụ 2: Sealed class cho các loại thanh toán
sealed class PaymentMethod(val name: String) {
    data class CreditCard(val cardNumber: String, val expiryDate: String) : PaymentMethod("Credit Card")
    data class PayPal(val email: String) : PaymentMethod("PayPal")
    data class BankTransfer(val accountNumber: String, val bankCode: String) : PaymentMethod("Bank Transfer")
    object Cash : PaymentMethod("Cash")
}


// Ví dụ 3: Sealed class với generic type
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}



/**
 * LƯU Ý KHI SỬ DỤNG SEALED CLASS:
 * 
 * 1. Tất cả subclass phải trong cùng file (Kotlin 1.5+: cùng package và module)
 * 2. Sealed class là abstract by default
 * 3. Constructor của sealed class là private by default
 * 4. Có thể có abstract members
 * 5. Subclass có thể là data class, object, hoặc regular class
 */

// ================================
// 2. SEALED INTERFACE
// ================================

/**
 * SEALED INTERFACE LÀ GÌ?
 * 
 * Sealed interface (từ Kotlin 1.5) tương tự sealed class nhưng sử dụng interface.
 * Cho phép multiple inheritance và linh hoạt hơn sealed class.
 * 
 * TẠI SAO CẦN SEALED INTERFACE?
 * 1. Multiple Inheritance: Một class có thể implement nhiều sealed interface
 * 2. Flexibility: Linh hoạt hơn sealed class trong việc thiết kế hierarchy
 * 3. Interface Benefits: Có tất cả lợi ích của interface (default implementations, etc.)
 */

// Ví dụ 1: Sealed interface cho kết quả operation
sealed interface OperationResult {
    data class Success(val message: String) : OperationResult
    data class Failure(val error: String) : OperationResult
    object InProgress : OperationResult
}

// Ví dụ 2: Sealed interface với multiple inheritance
sealed interface Drawable {
    fun draw()
}

sealed interface Clickable {
    fun onClick()
}

// Class có thể implement cả hai sealed interface
data class Button(val text: String) : Drawable, Clickable {
    override fun draw() {
        println("Drawing button: $text")
    }

    override fun onClick() {
        println("Button clicked: $text")
    }
}

data class Image(val url: String) : Drawable {
    override fun draw() {
        println("Drawing image: $url")
    }
}

fun handleDrawable(drawable: Drawable) {
    when (drawable) {
        is Button -> println("Button")
        is Image -> println("Image")
        is CompositeHandler -> print("composite handler")
    }
}

// Ví dụ 3: Sealed interface với generic và default implementation
sealed interface Repository<T> {
    suspend fun save(item: T): OperationResult
    suspend fun findById(id: String): T?

    // Default implementation
    suspend fun saveAll(items: List<T>): List<OperationResult> {
        return items.map { save(it) }
    }
}

class UserRepository : Repository<User> {
    override suspend fun save(item: User): OperationResult {
        // Simulate save operation
        return OperationResult.Success("User ${item.name} saved successfully")
    }

    override suspend fun findById(id: String): User? {
        // Simulate find operation
        return User(id, "User $id")
    }
}

data class User(val id: String, val name: String)

/**
 * LƯU Ý KHI SỬ DỤNG SEALED INTERFACE:
 * 
 * 1. Tương tự sealed class về quy tắc file/package
 * 2. Có thể có default implementations
 * 3. Cho phép multiple inheritance
 * 4. Không thể có state (properties with backing fields)
 * 5. Tất cả members là public by default
 */

// ================================
// 3. SO SÁNH SEALED CLASS VÀ SEALED INTERFACE
// ================================

/**
 * BẢNG SO SÁNH:
 * 
 * | Đặc điểm                    | Sealed Class        | Sealed Interface    |
 * |-----------------------------|---------------------|---------------------|
 * | Multiple Inheritance        | Không               | Có                  |
 * | State (Properties)          | Có                  | Không               |
 * | Constructor                 | Có                  | Không               |
 * | Abstract Members            | Có                  | Có                  |
 * | Default Implementation      | Có                  | Có                  |
 * | Inheritance từ other class  | Có (single)         | Không               |
 * | Performance                 | Tốt hơn             | Hơi chậm hơn        |
 * | Use Case                    | Data modeling       | Behavior modeling   |
 */

// Ví dụ minh họa sự khác biệt:

// Sealed class - tốt cho data modeling
sealed class NetworkState {
    object Idle : NetworkState()
    object Connecting : NetworkState()
    data class Connected(val connectionInfo: String) : NetworkState()
    data class Error(val exception: Exception) : NetworkState()
}

// Sealed interface - tốt cho behavior modeling
sealed interface EventHandler {
    fun handle(event: String)

    object ClickHandler : EventHandler {
        override fun handle(event: String) {
            println("Handling click: $event")
        }
    }

    object KeyboardHandler : EventHandler {
        override fun handle(event: String) {
            println("Handling keyboard: $event")
        }
    }
}

// Class có thể implement multiple sealed interfaces
class CompositeHandler : EventHandler, Drawable, Clickable {
    override fun handle(event: String) {
        println("Composite handling: $event")
    }

    override fun draw() {
        println("Drawing composite handler")
    }

    override fun onClick() {
        println("Composite handler clicked")
    }
}

// ================================
// 4. EXAMPLES VÀ BEST PRACTICES
// ================================

fun demonstrateSealedClass() {
    println("=== SEALED CLASS EXAMPLES ===")

    // Ví dụ với UiState
    val states = listOf(
        UiState.Loading,
        UiState.Success("Data loaded successfully"),
        UiState.Error(RuntimeException("Network error")),
        UiState.Empty
    )

    states.forEach { state ->
        // When expression với sealed class - exhaustive
        val message = when (state) {
            is UiState.Loading -> "Đang tải..."
            is UiState.Success -> "Thành công: ${state.data}"
            is UiState.Error -> "Lỗi: ${state.exception.message}"
            is UiState.Empty -> "Không có dữ liệu"
            // Không cần else clause - compiler đảm bảo exhaustive
        }
        println(message)
    }

    // Ví dụ với PaymentMethod
    val payments = listOf(
        PaymentMethod.CreditCard("1234-5678-9012-3456", "12/25"),
        PaymentMethod.PayPal("user@example.com"),
        PaymentMethod.BankTransfer("123456789", "VCB"),
        PaymentMethod.Cash
    )

    payments.forEach { payment ->
        val info = when (payment) {
            is PaymentMethod.CreditCard -> "Thẻ tín dụng: ${payment.cardNumber}"
            is PaymentMethod.PayPal -> "PayPal: ${payment.email}"
            is PaymentMethod.BankTransfer -> "Chuyển khoản: ${payment.accountNumber}"
            is PaymentMethod.Cash -> "Tiền mặt"
        }
        println("Phương thức thanh toán: $info")
    }
}

fun demonstrateSealedInterface() {
    println("\n=== SEALED INTERFACE EXAMPLES ===")

    // Ví dụ với OperationResult
    val results = listOf(
        OperationResult.Success("Operation completed"),
        OperationResult.Failure("Invalid input"),
        OperationResult.InProgress
    )

    results.forEach { result ->
        val status = when (result) {
            is OperationResult.Success -> "✅ ${result.message}"
            is OperationResult.Failure -> "❌ ${result.error}"
            is OperationResult.InProgress -> "⏳ Đang xử lý..."
        }
        println(status)
    }

    // Ví dụ với multiple inheritance
    val button = Button("Click Me")
    val image = Image("https://example.com/image.jpg")
    val compositeHandler = CompositeHandler()

    val drawables: List<Drawable> = listOf(button, image, compositeHandler)
    drawables.forEach { result -> result.draw() }

    val clickables: List<Clickable> = listOf(button, compositeHandler)
    clickables.forEach { it.onClick() }
}

suspend fun demonstrateGenericSealedInterface() {
    println("\n=== GENERIC SEALED INTERFACE EXAMPLE ===")

    val userRepo = UserRepository()
    val user = User("1", "John Doe")

    val saveResult = userRepo.save(user)
    println("Save result: $saveResult")

    val foundUser = userRepo.findById("1")
    println("Found user: $foundUser")

    val users = listOf(
        User("2", "Jane Smith"),
        User("3", "Bob Johnson")
    )
    val saveAllResults = userRepo.saveAll(users)
    println("Save all results: $saveAllResults")
}

// SEALED CLASS - Shared state, constructor logic
sealed interface NetworkResult<T> {
    // Shared state và constructor logic
    val age: Long get() = 10

    fun isStale(maxAgeMs: Long = 30_000): Boolean = age > maxAgeMs

    // Shared method với access to constructor parameters

    data class Success<T>(val data: T) : NetworkResult<T>
    data class Error(val message: String, val code: Int) : NetworkResult<Nothing>
    data class Loading(val progress: Int = 0) : NetworkResult<Nothing>
}

fun main() {
    println("\n=== SEALED CLASS EXAMPLE ===")
    val loadingResult = NetworkResult.Loading(10)
    val successResult = NetworkResult.Success("Hello from Kotlin!")
    print("loadingResult.age: ${loadingResult.age}")
    print("successResult.age: ${successResult.age}")

}

// ================================
// 5. KHI NÀO SỬ DỤNG CÁI GÌ?
// ================================

/**
 * SỬ DỤNG SEALED CLASS KHI:
 * 1. Bạn cần model data với các trạng thái cố định
 * 2. Cần lưu trữ state/properties
 * 3. Muốn single inheritance
 * 4. Performance là ưu tiên
 * 5. Ví dụ: UI states, API responses, error types
 *
 * SỬ DỤNG SEALED INTERFACE KHI:
 * 1. Bạn cần model behavior/actions
 * 2. Cần multiple inheritance
 * 3. Muốn tận dụng default implementations
 * 4. Thiết kế API linh hoạt
 * 5. Ví dụ: Event handlers, repositories, strategies
 */

// Main function để chạy tất cả examples
//fun main() {
//    demonstrateSealedClass()
//    demonstrateSealedInterface()
//
//    // Chạy async example
//    kotlinx.coroutines.runBlocking {
//        demonstrateGenericSealedInterface()
//    }
//
//    println("\n=== KẾT LUẬN ===")
//    println("✅ Sealed class: Tốt cho data modeling với single inheritance")
//    println("✅ Sealed interface: Tốt cho behavior modeling với multiple inheritance")
//    println("✅ Cả hai đều đảm bảo type safety và exhaustive when expressions")
//    println("✅ Lựa chọn dựa trên use case cụ thể của bạn")
//}
