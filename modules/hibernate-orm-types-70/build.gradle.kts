plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    compileOnly(libs.hibernate.orm.v70.compile)

    testImplementation(libs.hibernate.orm.v70.test)
    testImplementation(libs.hibernate.orm.testing.v70)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Scaffolding for custom Hibernate ORM types, for Hibernate ORM 7.0 and 7.1"
