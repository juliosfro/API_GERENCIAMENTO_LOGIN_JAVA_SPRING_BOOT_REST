package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@CrossOrigin
@RestController /* Para a classe aceitar métodos REST */
@RequestMapping(value = "/usuario") /* Estou mapeando para /usuario */
public class IndexController {

	/* Injeção de dependencia do nosso repositorio de usuario no controller. */
	@Autowired
	private UsuarioRepository usuarioRepository;

	/* Método para consultar usuário e venda por id do banco de dados. */
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id,
			@PathVariable(value = "venda") Long venda) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		/* O retorno seria um relatório. */
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	/* Método para consultar usuário por id do banco de dados. */
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> readById(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	/* Método para consultar todos os usuarios do banco de dados */
	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Usuario>> readAll() {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	/* Método para salvar um usuario no banco de dados. */
	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> create(@RequestBody Usuario usuario) {

		/* Para fazer a associção do usuario com o telefone. */
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		/* Para criptografar a senha antes de salva-la no banco de dados */
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	/* Método para atualizar um usuario no banco de dados. */
	@PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario) {

		/* Para fazer a associção do usuario com o telefone. */
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		/*
		 * Verificar se a senha já existe no banco de dados, se existe não criptografar,
		 * se não existe significa que é uma nova senha então criptografa.
		 */

		Usuario usuarioTemp = usuarioRepository.findUserByLogin(usuario.getLogin());
		/* Se as senhas forem diferentes então criptografa e atualiza. */
		if (!usuarioTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	/* Método para deletar um usuário por id do banco de dados. */
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteById(@PathVariable(value = "id") Long id) {
		usuarioRepository.deleteById(id);
		return new ResponseEntity<String>(id.toString(), HttpStatus.OK);
	}

}
