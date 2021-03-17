package curso.api.rest.controller;

import java.util.Optional;

import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.service.ImplementationUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ImplementationUserDetailsService implementationUserDetailsService;

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


	@GetMapping(value = "/page/{pagina}", produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(value = "cache_usuarios", allEntries = true)
	@CachePut("cache_usuarios")
	public ResponseEntity<Page<Usuario>> readUserPage(@PathVariable("pagina") int pagina) throws InterruptedException {

		/* Esta funcionando corretamente... */
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		Page<Usuario> lista = usuarioRepository.findAll(page);

		// System.out.println("Resgitros da página: => " + page);

		return new ResponseEntity<Page<Usuario>>(lista, HttpStatus.OK);
	}
	
	/* END-POINT consulta de usuário por nome */
	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> readUserByNamePage(@PathVariable("nome") String nome, @PathVariable("page") int page) throws InterruptedException{

		PageRequest pageRequest = null;
		Page<Usuario> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) { /* Não informou nome */

			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list =  usuarioRepository.findAll(pageRequest);
		}else {
			// pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			//list = usuarioRepository.findUserByNamePage(nome, pageRequest);
			//list = search(nome, 0, 0);
		}

		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
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

			implementationUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());

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

	@DeleteMapping(value = "/removeTelephone/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteTelephone(@PathVariable("id") Long id)  {
		telefoneRepository.deleteById(id);
		return new ResponseEntity<String>(id.toString(), HttpStatus.OK);
	}


	@GetMapping("/search/nome/{nome}/pagina/{pagina}")
	public Page<Usuario> search(
			@PathVariable("nome") String nome,
			@PathVariable(value = "pagina") String pagina,
			@RequestParam(
					value = "page",
					required = false,
					defaultValue = "0") int page,
			@RequestParam(
					value = "size",
					required = false,
					defaultValue = "5") int size) {

		if (pagina.equals("undefined") || pagina.trim().isEmpty() || pagina == null){
			pagina = "0";
		}

		return searchByNamePage(nome, Integer.parseInt(pagina), size);
	}

	public Page<Usuario> searchByNamePage(String searchTerm, int page, int size) {

		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "nome");
		return usuarioRepository.search(searchTerm.toLowerCase(), pageRequest);
	}

	public Page<Usuario> findAll2() {
		int page = 0;
		int size = 10;

		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "name");
		return new PageImpl<>(usuarioRepository.findAll(), pageRequest, size);
	}
}
