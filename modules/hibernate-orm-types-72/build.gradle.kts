plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    compileOnly(libs.hibernate.orm.v72.compile)

    testImplementation(libs.hibernate.orm.v72.test)
    testImplementation(libs.hibernate.orm.testing.v72)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Scaffolding for custom Hibernate ORM types, for Hibernate ORM 7.2 through 7.4"
