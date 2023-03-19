repositories {
    mavenLocal()
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

dependencies {
    compileOnly(project(":common"))
    compileOnly("org.spigotmc:spigot:1.19.4-R0.1-SNAPSHOT")
}
