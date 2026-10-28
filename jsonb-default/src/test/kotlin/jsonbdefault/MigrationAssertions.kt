package jsonbdefault

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals

private val postgres = PostgreSQLContainer<Nothing>("postgres:18.6").apply { start() }

private val database =
    Database.connect(
        url = postgres.jdbcUrl,
        driver = "org.postgresql.Driver",
        user = postgres.username,
        password = postgres.password,
    )

fun assertNoMigrationRequiredAfterCreate(table: Table) {
    transaction(database) { SchemaUtils.create(table) }
    try {
        val statements =
            transaction(database) {
                MigrationUtils.statementsRequiredForDatabaseMigration(table, withLogs = false)
            }
        assertEquals(emptyList(), statements)
    } finally {
        transaction(database) { SchemaUtils.drop(table) }
    }
}
