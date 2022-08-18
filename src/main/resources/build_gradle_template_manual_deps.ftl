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

    compileOnly "com.android.tools.lint:lint-api:${lintVersion}"
    compileOnly "com.android.tools.lint:lint-checks:${lintVersion}"
    testImplementation "com.android.tools.lint:lint-tests:${lintVersion}"
}