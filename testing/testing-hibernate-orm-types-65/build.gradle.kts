plugins {
    id("org.framefork.build.library-internal")
}

dependencies {
    testImplementation(project(":hibernate-orm-types-63"))
    testImplementation(libs.hibernate.orm.v65.test)
    testImplementation(libs.hibernate.orm.testing.v63)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}
