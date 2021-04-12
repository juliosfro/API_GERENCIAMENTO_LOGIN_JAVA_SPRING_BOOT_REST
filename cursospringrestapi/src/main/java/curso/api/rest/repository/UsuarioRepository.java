package curso.api.rest.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Usuario;

import javax.transaction.Transactional;
import java.util.List;

/* A chave primária é do tipo Long por isso passamos um Long como parametro. */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);

	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByName(String nome);

	@Query(value = "select constraint_name from information_schema.constraint_column_usage where table_name = 'usuarios_role' and column_name = 'role_id' and constraint_name <> 'unique_role_user';", nativeQuery = true)
	String getConstraintRoleUser();

	@Transactional
	@Modifying
	@Query(value = "insert into usuarios_role (usuario_id, role_id) values (?1, (select id from role where nome_role = 'ROLE_USER'));", nativeQuery = true)
	void insertDefaultRoleUser(Long idUser);

	/* Metodo para atualizar o token do usuario no banco de dados toda vez que logar. */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token =?1 where login = ?2")
	void upDateTokenUser(String token, String login);

	@Transactional
	@Modifying
	@Query(value = "update usuario set senha = ?1 where id = ?2", nativeQuery = true)
	void updatePassword(String newPassword, Long codUser);

	default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest) {

		Usuario usuario = new Usuario();
		usuario.setNome(nome);

		/*Configurando para pesquisar por nome e paginação*/
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers
						.contains());

		Example<Usuario> example = Example.of(usuario, exampleMatcher);

		return  findAll(example, pageRequest);
	}

	default Page<Usuario> findPage(Integer pageNumber, Integer itemPerPage, String direction, String orderBy) {

		Usuario usuario = new Usuario();
		usuario.setNome("julio");
		PageRequest pageRequest = PageRequest.of(pageNumber, itemPerPage, Sort.Direction.valueOf(direction), orderBy);
		return findAll(pageRequest);
	}

	@Query("FROM Usuario u " +
			"WHERE LOWER(u.nome) like %:searchTerm% " +
			"OR LOWER(u.login) like %:searchTerm%")
	Page<Usuario> search(
			@Param("searchTerm") String searchTerm,
			Pageable pageable);
}
