plugins {
    id("org.framefork.build.library-internal")
}

dependencies {
    testImplementation(project(":hibernate-orm-types-70"))
    testImplementation(libs.hibernate.orm.v71.test)
    testImplementation(libs.hibernate.orm.testing.v70)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}
