package curso.api.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController /* Para a classe aceitar métodos REST */
@RequestMapping(value = "/usuario") /* Estou mapeando para /usuario */
public class IndexController {

	@GetMapping(value = "/", produces = "application/json") /* Estou mapeando para a raíz de /usuario/. */
	/* Vou receber um parametro chamado nome do tipo String. */
	public ResponseEntity init(
			@RequestParam(value = "nome", required = true, defaultValue = "Nome não informado.") String nome,
			@RequestParam(value = "salario") String salario) {
		return new ResponseEntity(" Meu nome é: " + nome + " salario: " + salario, HttpStatus.OK);
	}
}
