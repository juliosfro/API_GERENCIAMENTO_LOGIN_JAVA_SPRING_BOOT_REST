package curso.api.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Usuario;

/* A chave primária é do tipo Long por isso passamos um Long como parametro. */
@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
}
