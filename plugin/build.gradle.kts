dependencies {
    api(project(":common"))
    api(project(":v1_15_R1"))
}

tasks {
    processResources {
        // replace version
        filesMatching("plugin.yml") {
            filter(org.apache.tools.ant.filters.ReplaceTokens::class, mapOf("tokens" to mapOf("version" to project.version)))
        }
    }
}
