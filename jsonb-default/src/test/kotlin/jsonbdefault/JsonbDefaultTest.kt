package jsonbdefault

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.jsonb
import kotlin.test.Test

@Serializable
data class Status(
    val open: Boolean,
)

@Serializable
data class Counter(
    val name: String,
    val count: Int,
)

@Serializable
data class Label(
    val name: String,
    val tags: List<String>,
)

object DevicesWithBooleanDefault : Table("devices_boolean_default") {
    val status = jsonb<Status>("status", Json).default(Status(open = false))
}

object DevicesWithNumberDefault : Table("devices_number_default") {
    val counter = jsonb<Counter>("counter", Json).default(Counter(name = "alarms", count = 0))
}

object DevicesWithStringDefault : Table("devices_string_default") {
    val label = jsonb<Label>("label", Json).default(Label(name = "default", tags = listOf("a", "b")))
}

class JsonbDefaultTest {
    // Fails: Exposed expects '{"open":false}'::jsonb, PostgreSQL reports '{"open": false}'::jsonb
    @Test
    fun `jsonb default with boolean value requires no migration`() {
        assertNoMigrationRequiredAfterCreate(DevicesWithBooleanDefault)
    }

    // Fails: no space is inserted after `:` when a number follows
    @Test
    fun `jsonb default with number value requires no migration`() {
        assertNoMigrationRequiredAfterCreate(DevicesWithNumberDefault)
    }

    // Passes: only strings, objects and arrays follow `:` and `,`, which the regex handles
    @Test
    fun `jsonb default with only string and array values requires no migration`() {
        assertNoMigrationRequiredAfterCreate(DevicesWithStringDefault)
    }
}
