package curso.api.rest.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Usuario;

import javax.transaction.Transactional;
import java.util.List;

/* A chave primária é do tipo Long por isso passamos um Long como parametro. */
@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);

	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByName(String nome);

	/* Metodo para atualizar o token do usuario no banco de dados toda vez que logar. */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token =?1 where login = ?2")
	void upDateTokenUser(String token, String login);
	
}
