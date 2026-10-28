package transformdefault

import org.jetbrains.exposed.v1.core.ColumnTransformer
import org.jetbrains.exposed.v1.core.Table
import java.time.ZoneId
import kotlin.test.Test

object ZoneIdTransformer : ColumnTransformer<String, ZoneId> {
    override fun unwrap(value: ZoneId): String = value.id

    override fun wrap(value: String): ZoneId = ZoneId.of(value)
}

object DevicesWithTransformedDefault : Table("devices_transformed_default") {
    val timezone =
        varchar("timezone", 64)
            .transform(ZoneIdTransformer)
            .default(ZoneId.of("Europe/Berlin"))
}

object DevicesWithPlainDefault : Table("devices_plain_default") {
    val timezone = varchar("timezone", 64).default("Europe/Berlin")
}

class TransformDefaultTest {
    // Fails: Exposed expects 'Europe/Berlin', PostgreSQL reports 'Europe/Berlin'::character varying
    @Test
    fun `transformed varchar default requires no migration`() {
        assertNoMigrationRequiredAfterCreate(DevicesWithTransformedDefault)
    }

    // Passes: the same default without transform is compared correctly
    @Test
    fun `plain varchar default requires no migration`() {
        assertNoMigrationRequiredAfterCreate(DevicesWithPlainDefault)
    }
}
