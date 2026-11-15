plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    compileOnly(libs.hibernate.orm.v62.compile)

    testImplementation(libs.hibernate.orm.v62.test)
    testImplementation(libs.hibernate.orm.testing.v62)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Scaffolding for custom Hibernate ORM types, for Hibernate ORM 6.2"
