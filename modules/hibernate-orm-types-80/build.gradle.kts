plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    compileOnly(libs.hibernate.orm.v80.compile)

    testImplementation(libs.hibernate.orm.v80.test)
    testImplementation(libs.hibernate.orm.testing.v80)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Scaffolding for custom Hibernate ORM types, for Hibernate ORM 8.0"
