package DAS.Project.spring.services;

import DAS.Project.spring.model.Object;

import java.util.List;

public interface ObjectService {
    List<Object> findAll();
    Object findById(Long id);
    Object save(Object object);
    void deleteById(Long id);
}
