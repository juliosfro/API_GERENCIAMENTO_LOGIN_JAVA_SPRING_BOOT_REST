package curso.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EntityScan(basePackages = {
		"curso.api.rest.model" }) /* Nossas classes de modelo para criar as tabelas no banco de dados. */
@ComponentScan(basePackages = { "curso.*" }) /* Toda a parte de injeção de dependencia */
@EnableJpaRepositories(basePackages = {
		"curso.api.rest.repository" }) /* Para habilitar toda a parte de repositorios de consulta do banco de dados */
@EnableTransactionManagement /* Para gerenciar todas as transacoes no banco de dados. */
@EnableWebMvc /* Para habilitar o modulo de MVC do Spring */
@RestController /* Para saber que é um projeto REST que vai retornar Json */
@EnableAutoConfiguration /* Para o Srping configurar todo o projeto. */
public class CursospringrestapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CursospringrestapiApplication.class, args);
	}

}
