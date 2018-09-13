defineVersion("jooq", "3.9.1").lock { withGroup("org.jooq") }
defineVersion("kotlin", "1.2.50").lock { withGroup("org.jetbrains.kotlin") }
defineVersion("spring", "4.3.8.RELEASE").lock { withGroup("org.springframework") }
defineVersion("springBoot", "1.5.3.RELEASE").lock { withGroup("org.springframework.boot") }
defineVersion("springSecurity", "4.2.2.RELEASE").lock { withGroup("org.springframework.security") }
defineVersion("springSocial", "1.1.4.RELEASE")

defineVersion("slf4j", "1.7.25").lock { withGroup("org.slf4j") }
defineVersion("ehcache", "2.10.4").lock { withGroup("net.sf.ehcache") }
defineVersion("hibernate", "5.3.4.Final")
defineVersion("cglib", "3.2.5").lock{ withGroup("cglib") }
defineVersion("jackson", "2.8.8")
    .lock { withGroup("com.fasterxml.jackson.core") }
    .lock { withGroup("com.fasterxml.jackson.datatype") }
defineVersion("flyway", "4.1.2").lock { withGroup("org.flywaydb") }
defineVersion("dropwizard", "3.1.2").lock { withGroup("io.dropwizard.metrics") }
defineVersion("swagger", "1.5.13").lock { withGroup("io.swagger")}

excludeLibrary("org.apache.ant", "ant")
excludeLibrary("org.springframework.boot", "spring-boot-starter-tomcat")

defineLibrary("swaggerAnnotations", listOf("io.swagger:swagger-annotations"))
defineLibrary("jooq", listOf("org.jooq:jooq", "org.jooq:jooq-meta"))
defineLibrary("jooqCodeGen", listOf("org.jooq:jooq", "org.jooq:jooq-meta", "org.jooq:jooq-codegen"))
defineLibrary("kotlin", listOf("org.jetbrains.kotlin:kotlin-stdlib-jre8:1.2.50",
    "org.jetbrains.kotlin:kotlin-stdlib:1.2.50",
    "org.jetbrains.kotlin:kotlin-reflect:1.2.50"))
defineLibrary("aspectJ", listOf("org.aspectj:aspectjweaver:1.8.8"))

defineLibrary("springSecurity", listOf("org.springframework.security:spring-security-web",
    "org.springframework.security:spring-security-config",
    "org.springframework.security:spring-security-core",
    "org.springframework.security:spring-security-acl",
    "javax.annotation:jsr250-api:1.0"))

defineLibrary("springSecurityTest", listOf("org.springframework.security:spring-security-test"))
defineLibrary("springTest", listOf("org.springframework:spring-test", "org.spockframework:spock-spring"))
defineLibrary("springBootTest", listOf("org.springframework.boot:spring-boot-starter-test"))
defineLibrary("springCore", listOf("org.springframework:spring-context",
    "org.springframework:spring-context-support",
    "org.springframework:spring-aop",
    "javax.transaction:javax.transaction-api:1.2",
    "org.springframework:spring-tx",
    "org.springframework:spring-jdbc",
    "cglib:cglib"))

defineLibrary("springWeb", listOf("org.springframework:spring-web", "org.springframework:spring-webmvc"))
defineLibrary("springBootBase", listOf("org.springframework.boot:spring-boot",
    "org.springframework.boot:spring-boot-autoconfigure"))

defineLibrary("springBoot", listOf("org.springframework.boot:spring-boot",
    "org.springframework.boot:spring-boot-autoconfigure",
    "org.springframework.boot:spring-boot-starter-web",
    "org.springframework.boot:spring-boot-starter-jetty"))

defineLibrary("springSocial", listOf("org.springframework.social:spring-social-security:${usingVersion("springSocial")}",
    "org.springframework.social:spring-social-core:${usingVersion("springSocial")}",
    "org.springframework.social:spring-social-config:${usingVersion("springSocial")}",
    "org.springframework.social:spring-social-google:1.0.0.RELEASE"))

defineLibrary("database", listOf("mysql:mysql-connector-java:6.0.5",
    "com.zaxxer:HikariCP:2.4.1",
    "org.jooq:jooq",
    "org.jooq:jooq-meta",
    "org.flywaydb:flyway-core"))

defineLibrary("testingLibraries", listOf("org.spockframework:spock-core",
    "org.mockito:mockito-core:1.10.19",
    "com.jayway.jsonpath:json-path:2.2.0",
    "cglib:cglib",
    "org.objenesis:objenesis:2.2",
    "org.ow2.asm:asm:5.0.4",
    "de.sven-jacobs:loremipsum:1.0",
    "org.codehaus.groovy:groovy-all"))

defineLibrary("groovy", listOf("org.codehaus.groovy:groovy-all"))
defineLibrary("apacheCommons", listOf("org.apache.commons:commons-lang3:3.4",
    "commons-io:commons-io:2.4",
    "org.apache.httpcomponents:httpclient:4.2.2"))

defineLibrary("logging", listOf("org.slf4j:slf4j-api", "ch.qos.logback:logback-classic"))
defineLibrary("servletCore", listOf("javax.servlet:javax.servlet-api:3.1.0",
    "org.glassfish.web:javax.el:2.2.6"))

defineLibrary("jwt", listOf("io.jsonwebtoken:jjwt:0.6.0"))
defineLibrary("ehcache", listOf("net.sf.ehcache:ehcache"))
defineLibrary("jackson", listOf("com.fasterxml.jackson.core:jackson-annotations",
    "com.fasterxml.jackson.core:jackson-databind",
    "com.fasterxml.jackson.datatype:jackson-datatype-jsr310",
    "com.fasterxml.jackson.module:jackson-module-kotlin:2.8.7"))

defineLibrary("dropwizard", listOf("io.dropwizard.metrics:metrics-core",
    "io.dropwizard.metrics:metrics-ehcache"))

defineLibrary("googleCloud", listOf("com.google.api-client:google-api-client:1.22.0",
    "com.google.apis:google-api-services-storage:v1-rev83-1.22.0"))
