package DAS.Project.spring.repository;

import DAS.Project.spring.model.Object;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectRepository extends JpaRepository<Object, Long> {
}
