package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import curso.api.rest.service.ImplementationUserDetailsService;

/* Mapeia URL, endereços, autoriza ou bloqueia acessos a URLS. */
/* Temos que extender da classe WebSecurityConfigurerAdapter */
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	/* Configurações de solicitação de acesso HTTP */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/* Ativando a proteção contra usuarios que não estão validados por token. */
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

				/* Ativando restrição a URL. */
				/* Abaixo esta dizendo que a parte inicial do sistema será publica. */
				.disable().authorizeRequests().antMatchers("/").permitAll()
				.antMatchers("/index", "/recuperar/**").permitAll()

				/* Para aceitar requisicoes de todos os metodos do HTTP */
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				/* URL de logout que redireciona após o user deslogar do sistema. */
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

				/* Mapeia URL de logout e invalida o usuario */
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

				/* Filtra requisições de login para autenticação */
				.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
						UsernamePasswordAuthenticationFilter.class)

				/*
				 * Filtra demais requisições para verificar a presença do token JWT no HEADER
				 * HTTP
				 */
				.addFilterBefore(new JwtApiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/* Abaixo esta a implementacao que fizemos para consultar no banco de dados. */
	@Autowired
	private ImplementationUserDetailsService implementationUserDetailsService;

	/* Vamos customizar essa autenticação. */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/* Service que irá consultar o usuario no banco de dados. */
		/* Usamos um padrão de codificação de senha. */
		auth.userDetailsService(implementationUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
}
