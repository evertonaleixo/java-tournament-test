package com.example.todolist.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.todolist.exceptions.DataIntegrationViolationException;
import com.example.todolist.exceptions.NotFoundException;
import com.example.todolist.model.ToDoEntry;
import com.example.todolist.model.ToDoList;
import com.example.todolist.repository.EntryRepository;
import com.example.todolist.repository.ListRepository;

@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ToDoListApiController {

	@Autowired
    private ListRepository listRepository;
	@Autowired
    private EntryRepository entryRepository;


    /**
     * Returns available lists with code 200
     */
    @GetMapping
    public Collection<ToDoList> getLists() {
        return listRepository.findAll();
    }

    /**
     * Lists all entries in the specified list, 404 if list not found
     */
    @GetMapping("/{listId}")
    public Collection<ToDoEntry> getListEntries(@PathVariable Long listId) {
    		if(!listRepository.exists(listId)) {
    			throw new NotFoundException();
    		}
        return entryRepository.findAllByListId(listId);
    }

    /**
     * Returns 201 and new entity if operation successful or 400 if invalid data supplied.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ToDoList createList(@RequestBody @Valid ToDoList list) {
    		for(ToDoEntry entry: list.getEntries()) {
    			entry.setList(list);
    		}
    		ToDoList saved = null;
    		
    		try {
    			saved = listRepository.save(list);
    		} catch (DataIntegrityViolationException e) {
    			throw new DataIntegrationViolationException();
		}
    		
        return saved;
    }

    /**
     * Returns 201 and new entity if operation successful or 400 if invalid data supplied.
     * Note that creating to do entries with description longer than 16k chars is
     * not allowed!
     * @return 
     */
    @PostMapping("/{listId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ToDoEntry createEntry(@PathVariable Long listId, @RequestBody @Valid ToDoEntry entry) {
        ToDoList list = ensureExists(listRepository.findOne(listId));
        entry.setList(list);
        ToDoEntry saved = entryRepository.save(entry);
        
        return saved;
    }

    /**
     * Returns 200 if successful, 404 if no such list id is found
     * @return 
     */
    @DeleteMapping("/{listId}")
    public ToDoList deleteList(@PathVariable Long listId) {
    		ToDoList list = ensureExists(listRepository.findOne(listId));
        listRepository.delete(list);
        return list;
    }

    /**
     * Deletes given entry if list and entry is valid. Return 404 if ether list or entry id is incorrect.
     * Return 400 if specified entry ID does not belong to the list.
     * @return 
     */
    @DeleteMapping("/{entryId}/{listId}")
    public ToDoEntry deleteEntry(@PathVariable Long listId, @PathVariable Long entryId) {
        ToDoList list = ensureExists(listRepository.findOne(listId));
        ToDoEntry entry = ensureExists(entryRepository.findOne(entryId));
        
        if (entry.getList().getId() != list.getId()) {
            throw new DataIntegrationViolationException();
        }
        entryRepository.delete(entry);
        
        return entry;
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFound() {
        // No-op, return empty 404
    }

    private static <T> T ensureExists(T object) {
        if (object == null) {
            throw new NotFoundException();
        }
        return object;
    }
}
