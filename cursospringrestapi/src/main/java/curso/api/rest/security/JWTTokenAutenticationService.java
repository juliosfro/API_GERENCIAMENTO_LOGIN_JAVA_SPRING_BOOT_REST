package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticationService {

	/* Vamos declarar algumas constantes que serão importantes */
	/* O tempo de expiração é em milisegundos, nesse caso serão 2 dias */
	private static final Long EXPIRATION_TIME = 172800000l;

	/*
	 * Uma senha única para compor a autenticação, poderia ser uma assinatura de
	 * certificado digital.
	 */
	private static final String SECRET = "SenhaExtremamenteSecreta";

	/* Esse prefixo é o padrão para token JWT. */
	private static final String TOKEN_PREFIX = "Bearer";

	/* O conteúdo que virá na resposta */
	private static final String HEADER_STRING = "Authorization";

	/*
	 * Gerando token de autenticação e adicionando ao cabeçalho e resposta HTTP que
	 * vai voltar para o navegador.
	 */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		/*
		 * Montagem do Token: primeiro chama o gerador de token e adiciona o usuario
		 */
		final String JWT = Jwts.builder().setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		/* Segundo passo juntar o token com o prefixo. */
		String token = TOKEN_PREFIX + " " + JWT;

		/* Adiciona no cabeçalho HTTP */
		response.addHeader(HEADER_STRING, token);

		/* Para atualizar o token do usuario no banco de dados */
		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
				.upDateTokenUser(JWT, username);

		/* Liberando respostas para portas diferentes que usam a API ou caso clientes web */
		liberacaoCors(response);

		/* Escreve token como resposta no corpo HTTP */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}

	/*
	 * Método para retornar o usuario validado com token, caso não seja válido
	 * retorna null
	 */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		/* Pega o token enviado no cabeçalho HTTP. */
		String token = request.getHeader(HEADER_STRING);

		/* Vamos fazer uma verificação se existe token. */
		try {
			if (token != null) {

				String token_limpo = token.replace(TOKEN_PREFIX, "").trim();

				/* Faz a validação do token do usuario na requisicao */
				String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token_limpo).getBody()
						.getSubject();

				if (user != null) {
					Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
							.findUserByLogin(user);
					/* Precisamos retornar o usuario logado */
					if (usuario != null) {
						/* Verifica se o token que o usuario esta passando é igual ao que esta no banco de dados. */
						if (token_limpo.equalsIgnoreCase(usuario.getToken())) {
							return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
									usuario.getAuthorities());
						}
					}
				}
			} /* Fim da condicao token */
		} catch (ExpiredJwtException e){
			try {
				response.getOutputStream().println("TOKEN Expirado, faça login ou informe novo TOKEN");
			} catch (IOException ioException) {

			}
		}
		liberacaoCors(response);
		return null;
	}

	private void liberacaoCors(HttpServletResponse response) {
		if(response.getHeader("Access-Control-Allow-Origin") == null ){
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		if(response.getHeader("Access-Control-Allow-Headers") == null ){
			response.addHeader("Access-Control-Allow-Headers ", "*");
		}

		if(response.getHeader("Access-Control-Request-Headers ") == null){
			response.addHeader("Access-Control-Request-Headers ", "*");
		}

		if(response.getHeader("Access-Control-Allow-Methods") == null){
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
