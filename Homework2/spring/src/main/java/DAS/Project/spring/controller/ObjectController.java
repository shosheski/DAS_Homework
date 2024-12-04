package DAS.Project.spring.controller;

import DAS.Project.spring.model.Object;
import DAS.Project.spring.services.ObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Validated
@CrossOrigin(origins = "*")
public class ObjectController {

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Object>> getAllObjects() {
        List<Object> objects = objectService.findAll();
        return new ResponseEntity<>(objects, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getObjectById(@PathVariable(value = "id") Long id) {
        Object object = objectService.findById(id);
        return object != null
                ? new ResponseEntity<>(object, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> createObject(@RequestBody Object object) {
        Object newObject = objectService.save(object);
        return new ResponseEntity<>(newObject, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Object> updateObject(@PathVariable(value = "id") Long id, @RequestBody Object updatedObject) {
        Object existingObject = objectService.findById(id);
        if (existingObject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedObject.setId(id);
        Object savedObject = objectService.save(updatedObject);
        return new ResponseEntity<>(savedObject, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteObject(@PathVariable(value = "id") Long id) {
        Object existingObject = objectService.findById(id);
        if (existingObject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        objectService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
