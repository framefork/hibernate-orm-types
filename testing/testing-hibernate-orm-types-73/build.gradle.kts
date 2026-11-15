plugins {
    id("org.framefork.build.library-internal")
}

dependencies {
    testImplementation(project(":hibernate-orm-types-72"))
    testImplementation(libs.hibernate.orm.v73.test)
    testImplementation(libs.hibernate.orm.testing.v72)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}
