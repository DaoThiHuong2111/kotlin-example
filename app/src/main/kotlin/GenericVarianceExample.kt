package org.example.app

/**
 * KOTLIN GENERICS VARIANCE - IN VÀ OUT
 * 
 * File này giải thích chi tiết về variance trong Kotlin generics:
 * - Covariance (out): Cho phép sử dụng kiểu con thay cho kiểu cha
 * - Contravariance (in): Cho phép sử dụng kiểu cha thay cho kiểu con
 * - Invariance: Không cho phép thay thế kiểu
 * 
 * Tác giả: Kotlin Learning Guide
 * Ngày tạo: 2024
 */

import kotlin.random.Random

/**
 * =============================================================================
 * 1. GIỚI THIỆU VỀ VARIANCE
 * =============================================================================
 * 
 * Variance là khái niệm mô tả mối quan hệ giữa các kiểu generic khi có
 * mối quan hệ kế thừa giữa các type parameter.
 * 
 * Có 3 loại variance:
 * - Covariance (out): Producer - chỉ đọc dữ liệu ra
 * - Contravariance (in): Consumer - chỉ nhận dữ liệu vào  
 * - Invariance: Không có variance - vừa đọc vừa ghi
 */

// Định nghĩa hierarchy class để demo
open class Animal(val name: String) {
    open fun makeSound() = "Some sound"
    override fun toString() = "Animal($name)"
}

open class Mammal(name: String) : Animal(name) {
    override fun makeSound() = "Mammal sound"
    override fun toString() = "Mammal($name)"
}

class Dog(name: String) : Mammal(name) {
    override fun makeSound() = "Woof!"
    fun bark() = "Bark bark!"
    override fun toString() = "Dog($name)"
}

class Cat(name: String) : Mammal(name) {
    override fun makeSound() = "Meow!"
    fun purr() = "Purr purr!"
    override fun toString() = "Cat($name)"
}

/**
 * =============================================================================
 * 2. COVARIANCE (OUT) - PRODUCER
 * =============================================================================
 * 
 * Covariance cho phép sử dụng kiểu con thay cho kiểu cha.
 * Sử dụng từ khóa 'out' trước type parameter.
 * 
 * Quy tắc: Chỉ có thể SẢN XUẤT (produce/return) dữ liệu, không thể TIÊU THỤ (consume/accept)
 * 
 * Ví dụ: Producer<Dog> có thể được sử dụng như Producer<Animal>
 * vì Dog là con của Animal, và ta chỉ lấy dữ liệu ra từ Producer.
 */

// Interface covariant - chỉ produce dữ liệu ra
interface Producer<out T> {
    fun produce(): T
    // fun consume(item: T) // ❌ KHÔNG được phép - vi phạm covariance
}

// Implementation cụ thể
class DogProducer : Producer<Dog> {
    private val dogs = listOf(
        Dog("Buddy"), 
        Dog("Max"), 
        Dog("Charlie")
    )

    override fun produce(): Dog {
        return dogs[Random.nextInt(dogs.size)]
    }
}

class AnimalProducer : Producer<Animal> {
    private val animals = listOf(
        Dog("Rex"),
        Cat("Whiskers"), 
        Animal("Generic Animal")
    )

    override fun produce(): Animal {
        return animals[Random.nextInt(animals.size)]
    }
}

/**
 * =============================================================================
 * 3. CONTRAVARIANCE (IN) - CONSUMER  
 * =============================================================================
 * 
 * Contravariance cho phép sử dụng kiểu cha thay cho kiểu con.
 * Sử dụng từ khóa 'in' trước type parameter.
 * 
 * Quy tắc: Chỉ có thể TIÊU THỤ (consume/accept) dữ liệu, không thể SẢN XUẤT (produce/return)
 * 
 * Ví dụ: Consumer<Animal> có thể được sử dụng như Consumer<Dog>
 * vì Animal có thể xử lý bất kỳ Dog nào (Dog là con của Animal).
 */

// Interface contravariant - chỉ consume dữ liệu vào
interface Consumer<in T> {
    fun consume(item: T)
    // fun produce(): T // ❌ KHÔNG được phép - vi phạm contravariance
}

// Implementation cụ thể
class AnimalConsumer : Consumer<Animal> {
    private val consumedAnimals = mutableListOf<Animal>()

    override fun consume(item: Animal) {
        consumedAnimals.add(item)
        println("🍽️ Consumed: $item - Sound: ${item.makeSound()}")
    }

    fun getConsumedCount() = consumedAnimals.size
    fun getConsumedAnimals() = consumedAnimals.toList()
}

class DogConsumer : Consumer<Dog> {
    private val consumedDogs = mutableListOf<Dog>()

    override fun consume(item: Dog) {
        consumedDogs.add(item)
        println("🐕 Dog consumed: $item - ${item.bark()}")
    }

    fun getConsumedDogs() = consumedDogs.toList()
}

/**
 * =============================================================================
 * 4. INVARIANCE - KHÔNG CÓ VARIANCE
 * =============================================================================
 * 
 * Invariance không cho phép thay thế kiểu.
 * Không sử dụng 'in' hay 'out' trước type parameter.
 * 
 * Có thể vừa produce vừa consume dữ liệu.
 */

// Interface invariant - có thể vừa produce vừa consume
interface Storage<T> {
    fun store(item: T)      // consume
    fun retrieve(): T?      // produce
    fun isEmpty(): Boolean
}

class AnimalStorage<T : Animal> : Storage<T> {
    private val items = mutableListOf<T>()

    override fun store(item: T) {
        items.add(item)
        println("📦 Stored: $item")
    }

    override fun retrieve(): T? {
        return if (items.isNotEmpty()) {
            val item = items.removeAt(0)
            println("📤 Retrieved: $item")
            item
        } else {
            println("📭 Storage is empty")
            null
        }
    }

    override fun isEmpty(): Boolean = items.isEmpty()

    fun size(): Int = items.size
}

/**
 * =============================================================================
 * 5. USE-SITE VARIANCE (PROJECTION)
 * =============================================================================
 * 
 * Khi sử dụng generic type, ta có thể chỉ định variance tại điểm sử dụng
 * thay vì tại điểm khai báo.
 */

// Generic class không có variance tại declaration-site
class Box<T>(private var item: T) {
    fun get(): T = item
    fun set(item: T) { this.item = item }
}

/**
 * =============================================================================
 * 6. REAL-WORLD USE CASES
 * =============================================================================
 */

/**
 * USE CASE 1: Repository Pattern với Covariance
 * 
 * Trong repository pattern, ta thường chỉ đọc dữ liệu ra,
 * nên covariance rất hữu ích.
 */
interface VarianceRepository<out T> {
    suspend fun findById(id: String): T?
    suspend fun findAll(): List<T>
    // suspend fun save(entity: T) // ❌ Không được phép với covariance
}

class VarianceUserRepository : VarianceRepository<VarianceUser> {
    private val users = mapOf(
        "1" to VarianceUser("1", "John Doe", "john@example.com"),
        "2" to VarianceUser("2", "Jane Smith", "jane@example.com")
    )

    override suspend fun findById(id: String): VarianceUser? = users[id]
    override suspend fun findAll(): List<VarianceUser> = users.values.toList()
}

data class VarianceUser(val id: String, val name: String, val email: String)
data class VarianceAdminUser(val id: String, val name: String, val email: String, val permissions: List<String>)

/**
 * USE CASE 2: Event Handler với Contravariance
 * 
 * Event handler thường chỉ nhận event vào để xử lý,
 * nên contravariance rất phù hợp.
 */
interface VarianceEventHandler<in T> {
    fun handle(event: T)
}

// Base event classes
open class VarianceEvent(val timestamp: Long = System.currentTimeMillis())
class VarianceUserEvent(val userId: String, val action: String) : VarianceEvent()
class VarianceSystemEvent(val component: String, val message: String) : VarianceEvent()

class GeneralVarianceEventHandler : VarianceEventHandler<VarianceEvent> {
    override fun handle(event: VarianceEvent) {
        println("🎯 Handling general event at ${event.timestamp}")
    }
}

class UserVarianceEventHandler : VarianceEventHandler<VarianceUserEvent> {
    override fun handle(event: VarianceUserEvent) {
        println("👤 Handling user event: User ${event.userId} performed ${event.action}")
    }
}

/**
 * USE CASE 3: Collection Operations
 * 
 * Kotlin collections sử dụng variance rất hiệu quả:
 * - List<out T>: Covariant vì chỉ đọc
 * - MutableList<T>: Invariant vì vừa đọc vừa ghi
 */

/**
 * USE CASE 4: Function Types với Variance
 * 
 * Function types trong Kotlin có variance tự nhiên:
 * - Parameter types: Contravariant (in)
 * - Return type: Covariant (out)
 * 
 * (A) -> B tương đương với Function1<in A, out B>
 */

/**
 * =============================================================================
 * 7. LƯU Ý KHI SỬ DỤNG
 * =============================================================================
 */

/**
 * LƯU Ý 1: PECS Rule (Producer Extends, Consumer Super)
 * 
 * - Nếu bạn chỉ ĐỌC dữ liệu từ generic type → sử dụng COVARIANCE (out)
 * - Nếu bạn chỉ GHI dữ liệu vào generic type → sử dụng CONTRAVARIANCE (in)  
 * - Nếu bạn vừa đọc vừa ghi → sử dụng INVARIANCE (không có in/out)
 */

/**
 * LƯU Ý 2: Star Projection (*)
 * 
 * Khi không biết chính xác kiểu generic, có thể sử dụng star projection
 */
fun processUnknownList(list: List<*>) {
    // Chỉ có thể đọc như Any?
    for (item in list) {
        println("Item: $item")
    }
}

/**
 * LƯU Ý 3: Type Erasure
 * 
 * Thông tin generic bị xóa tại runtime, cần cẩn thận khi làm việc với reflection
 */

/**
 * LƯU Ý 4: Bounded Type Parameters
 * 
 * Có thể kết hợp variance với bounded types
 */
interface BoundedProducer<out T : Animal> {
    fun produce(): T
}

interface BoundedConsumer<in T : Animal> {
    fun consume(item: T)
}

/**
 * =============================================================================
 * 8. DEMO FUNCTIONS
 * =============================================================================
 */

fun demonstrateCovariance() {
    println("\n🔄 === COVARIANCE DEMO ===")

    val dogProducer: Producer<Dog> = DogProducer()
    // ✅ Covariance cho phép gán Producer<Dog> cho Producer<Animal>
    val animalProducer: Producer<Animal> = dogProducer

    println("From dog producer (as animal producer):")
    repeat(3) {
        val animal = animalProducer.produce()
        println("  Produced: $animal - Sound: ${animal.makeSound()}")
    }

    // Giải thích: Vì Dog là con của Animal, và Producer chỉ produce ra dữ liệu,
    // nên ta có thể an toàn sử dụng Producer<Dog> như Producer<Animal>
}

fun demonstrateContravariance() {
    println("\n🔄 === CONTRAVARIANCE DEMO ===")

    val animalConsumer: Consumer<Animal> = AnimalConsumer()
    // ✅ Contravariance cho phép gán Consumer<Animal> cho Consumer<Dog>
    val dogConsumer: Consumer<Dog> = animalConsumer

    println("Using animal consumer as dog consumer:")
    dogConsumer.consume(Dog("Buddy"))
    dogConsumer.consume(Dog("Max"))

    // Giải thích: Vì Animal có thể xử lý bất kỳ Dog nào (Dog là con của Animal),
    // nên ta có thể an toàn sử dụng Consumer<Animal> như Consumer<Dog>
}

fun demonstrateInvariance() {
    println("\n🔄 === INVARIANCE DEMO ===")

    val dogStorage = AnimalStorage<Dog>()
    // ❌ Không thể gán cho Storage<Animal> vì invariant
    // val animalStorage: Storage<Animal> = dogStorage // Compile error

    dogStorage.store(Dog("Rex"))
    dogStorage.store(Dog("Buddy"))

    println("Storage size: ${dogStorage.size()}")

    val retrieved = dogStorage.retrieve()
    retrieved?.let { println("Retrieved and can call dog-specific method: ${it.bark()}") }
}

fun demonstrateUseSiteVariance() {
    println("\n🔄 === USE-SITE VARIANCE DEMO ===")

    val dogBox = Box(Dog("Buddy"))
    val catBox = Box(Cat("Whiskers"))

    // Use-site variance: chỉ định variance tại điểm sử dụng
    val boxes: List<Box<out Animal>> = listOf(dogBox, catBox)

    println("Reading from boxes with out projection:")
    boxes.forEach { box ->
        val animal = box.get() // Chỉ có thể đọc như Animal
        println("  Animal: $animal - Sound: ${animal.makeSound()}")
        // box.set(Dog("New Dog")) // ❌ Không thể ghi với out projection
    }

    // In projection example
    fun feedAnimals(boxes: List<Box<in Animal>>) {
        boxes.forEach { box ->
            box.set(Dog("Fed Dog")) // Có thể ghi Animal hoặc subtype
            // val animal = box.get() // ❌ Không thể đọc với in projection (trả về Any?)
        }
    }
}

fun demonstrateRealWorldUseCases() {
    println("\n🔄 === REAL-WORLD USE CASES DEMO ===")

    // Repository pattern
    println("\n📚 Repository Pattern:")
    val userRepo: VarianceRepository<VarianceUser> = VarianceUserRepository()
    // Có thể sử dụng như VarianceRepository<Any> nếu cần

    // Event handling
    println("\n🎯 Event Handling:")
    val generalHandler: VarianceEventHandler<VarianceEvent> = GeneralVarianceEventHandler()
    val userHandler: VarianceEventHandler<VarianceUserEvent> = UserVarianceEventHandler()

    // Contravariance: có thể sử dụng general handler cho specific events
    val specificUserHandler: VarianceEventHandler<VarianceUserEvent> = generalHandler

    specificUserHandler.handle(VarianceUserEvent("123", "login"))
    userHandler.handle(VarianceUserEvent("456", "logout"))

    // Collection variance
    println("\n📋 Collection Variance:")
    val dogs: List<Dog> = listOf(Dog("Rex"), Dog("Buddy"))
    val animals: List<Animal> = dogs // ✅ Covariance

    animals.forEach { animal ->
        println("  Animal in list: $animal")
    }
}

fun demonstrateCommonPitfalls() {
    println("\n🔄 === COMMON PITFALLS ===")

    println("\n⚠️ Pitfall 1: Trying to modify covariant collections")
    val dogs = mutableListOf(Dog("Rex"))
    // val animals: MutableList<Animal> = dogs // ❌ Compile error - MutableList is invariant
    // animals.add(Cat("Whiskers")) // Nếu được phép, sẽ thêm Cat vào list Dog!
    println("  MutableList is invariant - cannot assign MutableList<Dog> to MutableList<Animal>")

    println("\n⚠️ Pitfall 2: Star projection limitations")
    val unknownList: List<*> = listOf("Hello", "World")
    processUnknownList(unknownList)
    // val item: String = unknownList[0] // ❌ Compile error - chỉ có thể đọc như Any?

    println("\n⚠️ Pitfall 3: Variance và null safety")
    val nullableProducer: Producer<String?> = object : Producer<String?> {
        override fun produce(): String? = null
    }
    // val nonNullProducer: Producer<String> = nullableProducer // ❌ Compile error
    println("  Cannot assign Producer<String?> to Producer<String> due to null safety")
}

/**
 * =============================================================================
 * 9. MAIN FUNCTION - CHẠY TẤT CẢ DEMO
 * =============================================================================
 */
fun runGenericVarianceExamples() {
    println("🚀 KOTLIN GENERICS VARIANCE - IN VÀ OUT EXAMPLES")
    println("=" * 60)

    demonstrateCovariance()
    demonstrateContravariance() 
    demonstrateInvariance()
    demonstrateUseSiteVariance()
    demonstrateRealWorldUseCases()
    demonstrateCommonPitfalls()

    println("\n" + "=" * 60)
    println("✅ Hoàn thành tất cả ví dụ về Kotlin Generics Variance!")
    println("\n📝 TÓM TẮT:")
    println("• OUT (Covariance): Chỉ đọc dữ liệu - Producer")
    println("• IN (Contravariance): Chỉ ghi dữ liệu - Consumer") 
    println("• Không có IN/OUT (Invariance): Vừa đọc vừa ghi")
    println("• PECS Rule: Producer Extends, Consumer Super")
    println("• Use-site variance: Chỉ định variance tại điểm sử dụng")
}

// Extension function để lặp string
private operator fun String.times(n: Int): String = this.repeat(n)
