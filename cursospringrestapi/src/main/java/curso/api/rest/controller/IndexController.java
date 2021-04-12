package curso.api.rest.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import curso.api.rest.model.UserChart;
import curso.api.rest.model.UserReport;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.service.ImplementationUserDetailsService;
import curso.api.rest.service.ServiceRelatorio;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

import javax.servlet.http.HttpServletRequest;
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
	private ServiceRelatorio serviceRelatorio;

	@Autowired
	private ImplementationUserDetailsService implementationUserDetailsService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

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


	@GetMapping(value = "/page/{pagina}/sort/{sort}/criterion/{criterion}/size/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(value = "cache_usuarios", allEntries = true)
	@CachePut("cache_usuarios")
	public ResponseEntity<Page<Usuario>> readUserPage(@PathVariable("pagina") int pagina,
													  @PathVariable("sort") String sort,
													  @PathVariable("criterion") String criterion,
													  @PathVariable("size") String size) throws InterruptedException {

		size = size.equals("undefined")
				|| size.trim().equals("")
				|| size.equals("null") ? "5" : size;

		PageRequest page = PageRequest.of(pagina, Integer.parseInt(size), Sort.Direction.fromString(sort.toUpperCase()), criterion);
		return new ResponseEntity<Page<Usuario>>(usuarioRepository.findAll(page), HttpStatus.OK);
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

			/* Formatacao da data de nascimento para o padrao brasileiro */
			Date currentDate = new Date(usuario.getDataNascimento().getTime());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			String date = dateFormat.format(currentDate);
			usuario.setDataNascimento(date);

			Usuario usuarioSalvo = usuarioRepository.save(usuario);

			implementationUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());

			return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
		}
	}

	/* Método para atualizar um usuario no banco de dados. */
	@PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario) throws ParseException {

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

		/* Formatacao da data de nascimento para o padrao brasileiro */
		Date currentDate = new Date(usuario.getDataNascimento().getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = dateFormat.format(currentDate);
		usuario.setDataNascimento(date);

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
	public ResponseEntity<String> deleteTelephone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		return new ResponseEntity<String>(id.toString(), HttpStatus.OK);
	}

	/* END-POINT consulta de usuário por nome */
	@GetMapping(value = "/search/nome/{nome}/pagina/{pagina}/sort/{sort}/criterion/{criterion}/size/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> search(
			@PathVariable("nome") String nome,
			@PathVariable("pagina") String pagina,
			@PathVariable("sort") String sort,
			@PathVariable("criterion") String criterion,
			@PathVariable("size") String size) throws InterruptedException {

		pagina = pagina.equals("undefined")
				|| pagina.trim().equals("")
				|| pagina.equals("null") ? "0" : pagina;

		size = size.equals("undefined")
				|| size.trim().equals("")
				|| size.equals("null") ? "5" : size;

		return nome == null
				|| (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")
				|| nome.equalsIgnoreCase("null")
				|| nome.trim().equals("") ? readUserPage(Integer.parseInt(pagina), sort, criterion, size) :
				searchByNamePage(nome, Integer.parseInt(pagina), Integer.parseInt(size), sort, criterion);
	}

	public ResponseEntity<Page<Usuario>> searchByNamePage(String nome, int page, int size, String sort, String criterion) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sort.toUpperCase()), criterion);
		return new ResponseEntity<Page<Usuario>>(usuarioRepository.search(nome.toLowerCase(), pageRequest), HttpStatus.OK);
	}

	@GetMapping(value = "/relatorio", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest httpServletRequest) throws Exception {

		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario",
				httpServletRequest.getServletContext());

		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
	}

	@PostMapping(value = "/relatorio/", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> downloadRelatorioParam(HttpServletRequest httpServletRequest, @RequestBody UserReport userReport) throws Exception {

		SimpleDateFormat dateFormatParam = new SimpleDateFormat("yyyy-MM-dd");
		String dataInicio = dateFormatParam.format(dateFormatParam.parse(userReport.getDataInicio()));
		String dataFim = dateFormatParam.format(dateFormatParam.parse(userReport.getDataFim()));

		//System.out.println(dataInicio);
		//System.out.println(dataFim);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("DATA_INICIO", dataInicio);
		params.put("DATA_FIM", dataFim);

		byte[] pdf = serviceRelatorio.gerarRelatorioParam("relatorio-usuario-param", params,
				httpServletRequest.getServletContext());

		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
	}

	@GetMapping(value = "/grafico", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserChart> graficoSalario() {

		UserChart userChart = new UserChart();
		String consultaSql = "select array_agg(nome) from usuario where salario > 0 and nome <> '' union all select cast(array_agg(salario) as character varying[]) from usuario where salario > 0 and nome <> ''";
		List<String> resultado = jdbcTemplate.queryForList(consultaSql, String.class);

		if (!resultado.isEmpty()) {
			String nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salarios = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
			userChart.setNome(nomes);
			userChart.setSalario(salarios);
		}
		return new ResponseEntity<UserChart>(userChart, HttpStatus.OK);
	}
}
