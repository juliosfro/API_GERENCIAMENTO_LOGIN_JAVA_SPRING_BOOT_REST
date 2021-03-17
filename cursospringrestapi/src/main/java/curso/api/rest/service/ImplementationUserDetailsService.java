package curso.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@Service
public class ImplementationUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		/* Consultar no banco de dados o usuario. */
		Usuario usuario = usuarioRepository.findUserByLogin(username);

		if (usuario == null) {
			throw new UsernameNotFoundException("Usuário não encontrado.");
		}

		/* Vamos construir um usuario proprio do Spring para autenticação. */
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
	}

	public void insereAcessoPadrao(Long id) {

		/* Descobre o nome da constraint de restricao */
		String constraint = usuarioRepository.getConstraintRoleUser();

		/* Remove a constraint de restricao */
		if (constraint != null) {
			jdbcTemplate.execute("alter table usuarios_role drop constraint " + constraint);
		}

		/* Atribui o role default ao usuario*/
		usuarioRepository.insertDefaultRoleUser(id);
	}
}
