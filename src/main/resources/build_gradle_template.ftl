plugins {
    id 'java-library'
    id 'kotlin'
}

jar {
    manifest {
        attributes("Lint-Registry-v2": "${lintRegistry}")
    }
}

dependencies {
    ${kotlinDependency}

    compileOnly lintDeps.values()
    testImplementation lintTestDeps.values()
}