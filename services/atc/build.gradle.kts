plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":libs:common"))
    runtimeOnly("org.postgresql:postgresql")
}
