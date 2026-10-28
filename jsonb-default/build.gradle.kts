plugins {
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    testImplementation(libs.bundles.exposed.postgres)
    testImplementation(libs.exposed.json)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(kotlin("test"))
}
