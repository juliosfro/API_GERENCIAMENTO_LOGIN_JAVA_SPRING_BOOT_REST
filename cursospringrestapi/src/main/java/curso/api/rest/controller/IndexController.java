package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import curso.api.rest.model.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
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

import javax.validation.Valid;


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
	@CacheEvict(value = "cache_usuario", allEntries = true)
	@CachePut("cache_usuario")
	public ResponseEntity<UsuarioDTO> readById(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}

	/* Método para consultar todos os usuarios do banco de dados */
	/* Vamos supor que o carregamento de usuarios seja um processo lento
	*  e queremos controlar ele com cache para agilizar o processo. */
	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(value = "cache_usuarios", allEntries = true)
	@CachePut("cache_usuarios")
	public ResponseEntity<List<Usuario>> readAll() throws InterruptedException {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		/* Segura o codigo por 6 segundos simulando um processo lento */
		// Thread.sleep(6000);
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	/* End-Point de consulta de usuario por nome */
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(value = "cache_usuarios", allEntries = true)
	@CachePut("cache_usuarios")
	public ResponseEntity<List<Usuario>> readUserByName(@PathVariable("nome") String nome) throws InterruptedException {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findUserByName(nome);
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	/* Método para salvar um usuario no banco de dados. */
	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> create(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) throws Exception {

		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException(bindingResult.getAllErrors().get(0).getDefaultMessage());
		} else {
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

		Usuario usuarioTemp = usuarioRepository.findById(usuario.getId()).get();

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
