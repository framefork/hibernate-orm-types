plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    compileOnly(libs.hibernate.orm.v61.compile)

    testImplementation(libs.hibernate.orm.v61.test)
    testImplementation(libs.hibernate.orm.testing.v61)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Scaffolding for custom Hibernate ORM types, for Hibernate ORM 6.1"
