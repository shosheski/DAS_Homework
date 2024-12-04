package DAS.Project.spring.services.impl;

import DAS.Project.spring.model.Object;
import DAS.Project.spring.repository.ObjectRepository;
import DAS.Project.spring.services.ObjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjectServiceImpl implements ObjectService {

    private final ObjectRepository objectRepository;

    public ObjectServiceImpl(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    public List<Object> findAll() {
        return objectRepository.findAll();
    }

    @Override
    public Object findById(Long id) {
        return objectRepository.findById(id).orElse(null);
    }

    @Override
    public Object save(Object object) {
        return objectRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        objectRepository.deleteById(id);
    }
}
