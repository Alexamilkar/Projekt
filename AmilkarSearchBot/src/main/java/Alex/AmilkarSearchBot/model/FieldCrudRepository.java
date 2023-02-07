package Alex.AmilkarSearchBot.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldCrudRepository extends CrudRepository<Field, Integer> {
}
