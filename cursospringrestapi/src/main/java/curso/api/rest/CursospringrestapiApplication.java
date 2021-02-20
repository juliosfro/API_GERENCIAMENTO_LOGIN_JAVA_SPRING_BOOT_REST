package curso.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = {
		"curso.api.rest.model" }) /* Nossas classes de modelo para criar as tabelas no banco de dados. */
@ComponentScan(basePackages = { "curso.*" }) /* Toda a parte de injeção de dependencia */
@EnableJpaRepositories(basePackages = {
		"curso.api.rest.repository" }) /* Para habilitar toda a parte de repositorios de consulta do banco de dados */
@EnableTransactionManagement /* Para gerenciar todas as transacoes no banco de dados. */
@EnableWebMvc /* Para habilitar o modulo de MVC do Spring */
@RestController /* Para saber que é um projeto REST que vai retornar Json */
@EnableCaching
public class CursospringrestapiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(CursospringrestapiApplication.class, args);
		// System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

	/* Configuração Global de controle de acesso que reflete em todo o sistema. */
	@Override
	public void addCorsMappings(CorsRegistry registry) {

		/* Para liberar acesso a todos os controllers e end-points. */
		/* registry.addMapping("/**"); */

		/* Para todos os end-points que estao dentro do controller /usuario */
		/* Podemos restringir quais tipos de requisicoes que podem ser enviadas */
		/* É possivel restringir por origem, ou seja, quem esta requisitando. */
		registry.addMapping("/usuario/**").allowedMethods("*").allowedOrigins("*");
	}
}
