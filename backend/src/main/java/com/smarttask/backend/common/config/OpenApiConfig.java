package com.smarttask.backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartTaskOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("SmartTask API")
                .description("API REST\n" +
                    "  .   ____          _            __ _ _\n" +
                    " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\\n" +
                    "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\\n" +
                    " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )\n" +
                    "  '  |____| .__|_| |_|_| |_\\__, | / / / /\n" +
                    " =========|_|==============|___/=/_/_/_/\n" +
                    "\n" +
                    " :: Spring Boot ::                (v4.1.1)\n" +
                    "\n" +
                    "2026-10-03T02:35:29.029+01:00  INFO 3796 --- [backend] [  restartedMain] c.smarttask.backend.BackendApplication   : Starting BackendApplication using Java 17.0.12 with PID 3796 (C:\\Users\\Hanen GB\\Desktop\\smarttask\\backend\\target\\classes started by Hanen GB in C:\\Users\\Hanen GB\\Desktop\\smarttask\\backend)\n" +
                    "2026-10-03T02:35:29.036+01:00 DEBUG 3796 --- [backend] [  restartedMain] c.smarttask.backend.BackendApplication   : Running with Spring Boot v4.1.1, Spring v7.0.9\n" +
                    "2026-10-03T02:35:29.037+01:00  INFO 3796 --- [backend] [  restartedMain] c.smarttask.backend.BackendApplication   : The following 1 profile is active: \"dev\"\n" +
                    "2026-10-03T02:35:29.182+01:00  INFO 3796 --- [backend] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable\n" +
                    "2026-10-03T02:35:29.183+01:00  INFO 3796 --- [backend] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'\n" +
                    "2026-10-03T02:35:30.946+01:00  INFO 3796 --- [backend] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.\n" +
                    "2026-10-03T02:35:31.023+01:00  INFO 3796 --- [backend] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 60 ms. Found 1 JPA repository interface.\n" +
                    "2026-10-03T02:35:32.531+01:00  INFO 3796 --- [backend] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 8080 (http)\n" +
                    "2026-10-03T02:35:32.563+01:00  INFO 3796 --- [backend] [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]\n" +
                    "2026-10-03T02:35:32.564+01:00  INFO 3796 --- [backend] [  restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.24]\n" +
                    "2026-10-03T02:35:32.663+01:00  INFO 3796 --- [backend] [  restartedMain] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 3477 ms\n" +
                    "2026-10-03T02:35:33.363+01:00  INFO 3796 --- [backend] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...\n" +
                    "2026-10-03T02:35:33.780+01:00  INFO 3796 --- [backend] [  restartedMain] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@43db059f\n" +
                    "2026-10-03T02:35:33.782+01:00  INFO 3796 --- [backend] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.\n" +
                    "2026-10-03T02:35:33.836+01:00  INFO 3796 --- [backend] [  restartedMain] org.flywaydb.core.FlywayExecutor         : Database: jdbc:postgresql://localhost:5433/smarttask (PostgreSQL 16.15)\n" +
                    "2026-10-03T02:35:34.026+01:00  INFO 3796 --- [backend] [  restartedMain] o.f.core.internal.command.DbValidate     : Successfully validated 1 migration (execution time 00:00.060s)\n" +
                    "2026-10-03T02:35:34.071+01:00  INFO 3796 --- [backend] [  restartedMain] o.f.core.internal.command.DbMigrate      : Current version of schema \"public\": 1\n" +
                    "2026-10-03T02:35:34.084+01:00  INFO 3796 --- [backend] [  restartedMain] o.f.core.internal.command.DbMigrate      : Schema \"public\" is up to date. No migration necessary.\n" +
                    "2026-10-03T02:35:34.402+01:00  INFO 3796 --- [backend] [  restartedMain] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]\n" +
                    "2026-10-03T02:35:34.577+01:00  INFO 3796 --- [backend] [  restartedMain] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.4.5.Final\n" +
                    "2026-10-03T02:35:35.450+01:00  INFO 3796 --- [backend] [  restartedMain] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer\n" +
                    "2026-10-03T02:35:35.737+01:00  INFO 3796 --- [backend] [  restartedMain] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:\n" +
                    "        Database JDBC URL [jdbc:postgresql://localhost:5433/smarttask]\n" +
                    "        Database driver: PostgreSQL JDBC Driver\n" +
                    "        Database dialect: PostgreSQLDialect\n" +
                    "        Database version: 16.15\n" +
                    "        Default catalog/schema: smarttask/public\n" +
                    "        Autocommit mode: undefined/unknown\n" +
                    "        Isolation level: READ_COMMITTED [default READ_COMMITTED]\n" +
                    "        JDBC fetch size: none\n" +
                    "        Pool: DataSourceConnectionProvider\n" +
                    "        Minimum pool size: undefined/unknown\n" +
                    "        Maximum pool size: undefined/unknown\n" +
                    "2026-10-03T02:35:37.351+01:00  INFO 3796 --- [backend] [  restartedMain] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)\n" +
                    "2026-10-03T02:35:37.441+01:00  INFO 3796 --- [backend] [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'\n" +
                    "2026-10-03T02:35:37.745+01:00  WARN 3796 --- [backend] [  restartedMain] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning\n" +
                    "2026-10-03T02:35:38.511+01:00  INFO 3796 --- [backend] [  restartedMain] o.s.d.j.r.query.QueryEnhancerFactories   : Hibernate is in classpath; If applicable, HQL parser will be used.\n" +
                    "2026-10-03T02:35:38.823+01:00  WARN 3796 --- [backend] [  restartedMain] .s.a.UserDetailsServiceAutoConfiguration : \n" +
                    "\n" +
                    "Using generated security password: 5e36f522-ad6b-4e49-a75e-5bd251d60426\n" +
                    "\n" +
                    "This generated password is for development use only. Your security configuration must be updated before running your application in production.\n" +
                    "\n" +
                    "2026-10-03T02:35:38.885+01:00  INFO 3796 --- [backend] [  restartedMain] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with UserDetailsService bean with name inMemoryUserDetailsManager\n" +
                    "2026-10-03T02:35:39.190+01:00  INFO 3796 --- [backend] [  restartedMain] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'\n" +
                    "2026-10-03T02:35:39.503+01:00  INFO 3796 --- [backend] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'\n" +
                    "2026-10-03T02:35:39.524+01:00  INFO 3796 --- [backend] [  restartedMain] c.smarttask.backend.BackendApplication   : Started BackendApplication in 11.352 seconds (process running for 12.329)\n de la plateforme de gestion de projets et taches SmartTask")
                .version("v0.1"));
    }
}
