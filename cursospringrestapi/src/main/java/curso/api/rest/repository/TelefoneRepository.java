package curso.api.rest.repository;

import curso.api.rest.model.Telefone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long> {

}
