package com.example.todolist.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.todolist.exceptions.DataIntegrationViolationException;
import com.example.todolist.exceptions.NotFoundException;
import com.example.todolist.model.ToDoEntry;
import com.example.todolist.model.ToDoList;
import com.example.todolist.repository.EntryRepository;
import com.example.todolist.repository.ListRepository;

@RunWith(MockitoJUnitRunner.class)
public class ToDoListApiControllerMethodsTest {
	@Mock
	private ListRepository listRepository;

	@Mock
	private EntryRepository entryRepository;

	@InjectMocks
	private ToDoListApiController toDoListApiController;

	@Test
	public void getListTest() {
		when(listRepository.findAll()).thenReturn(new ArrayList<ToDoList>());

		assertNotNull(toDoListApiController.getLists());
	}

	@Test
	public void getListEntryOkTest() {
		when(listRepository.exists(1L)).thenReturn(true);

		Collection<ToDoEntry> entries = new HashSet<>();
		when(entryRepository.findAllByListId(1L)).thenReturn(entries);

		assertNotNull(toDoListApiController.getListEntries(1L));
	}

	@Test(expected = NotFoundException.class)
	public void getListEntryFailTest() {
		when(listRepository.exists(1L)).thenReturn(false);

		toDoListApiController.getListEntries(1L);
	}

	@Test
	public void createListOkTest() {
		ToDoList list = new ToDoList();
		list.setName("list test");
		ToDoEntry element = new ToDoEntry();
		element.setDescription("element 1");
		list.addEntry(element);

		when(listRepository.save(list)).thenReturn(list);

		ToDoList result = toDoListApiController.createList(list);
		assertNotNull(result);
		assertEquals("list test", result.getName());
		assertEquals(1, result.getEntries().size());

	}

	@SuppressWarnings("unchecked")
	@Test(expected = DataIntegrationViolationException.class)
	public void createListFailTest() {
		ToDoList list = new ToDoList();
		list.setName("list test");
		ToDoEntry element = new ToDoEntry();
		element.setDescription("element 1");
		list.addEntry(element);

		when(listRepository.save(list)).thenThrow(DataIntegrationViolationException.class);

		ToDoList result = toDoListApiController.createList(list);
		assertNotNull(result);
		assertEquals("list test", result.getName());
		assertEquals(1, result.getEntries().size());

	}

	@Test
	public void createEntryOkTest() {
		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("element");

		ToDoList list = new ToDoList();
		list.setName("list 1");

		when(listRepository.findOne(1L)).thenReturn(list);
		when(entryRepository.save(entry)).thenReturn(entry);

		ToDoEntry result = toDoListApiController.createEntry(1L, entry);

		assertNotNull(result);
		assertEquals(list, result.getList());
		assertEquals("element", result.getDescription());

	}

	@Test(expected = NotFoundException.class)
	public void createEntryFailNotFoundTest() {
		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("element");

		ToDoList list = new ToDoList();
		list.setName("list 1");

		when(listRepository.findOne(1L)).thenReturn(null);
		when(entryRepository.save(entry)).thenReturn(entry);

		toDoListApiController.createEntry(1L, entry);
	}

	@Test
	public void deleteListOkTest() {
		ToDoList list = new ToDoList();
		list.setName("list 1");

		when(listRepository.findOne(1L)).thenReturn(list);

		ToDoList result = toDoListApiController.deleteList(1L);

		assertNotNull(result);
		assertEquals("list 1", result.getName());
		assertEquals(0, result.getEntries().size());
	}

	@Test(expected = NotFoundException.class)
	public void deleteListFailNotFoundTest() {
		when(listRepository.findOne(2L)).thenReturn(null);

		toDoListApiController.deleteList(2L);
	}

	@Test
	public void deleteEntryOkTest() {
		ToDoList list = new ToDoList();
		list.setName("list 1");
		list.setId(1L);

		when(listRepository.findOne(1L)).thenReturn(list);

		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("element");
		entry.setList(list);
		entry.setId(3L);

		when(entryRepository.findOne(1L)).thenReturn(entry);

		ToDoEntry result = toDoListApiController.deleteEntry(1L, 1L);

		assertNotNull(result);
		assertEquals("element", result.getDescription());
	}

	@Test(expected = NotFoundException.class)
	public void deleteEntryFailNotFoundTest() {
		when(listRepository.findOne(2L)).thenReturn(null);
		when(entryRepository.findOne(2L)).thenReturn(null);

		toDoListApiController.deleteEntry(2L, 2L);
	}

	@Test(expected = NotFoundException.class)
	public void deleteEntryFailNotFound1Test() {
		when(listRepository.findOne(2L)).thenReturn(null);

		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("element");

		when(entryRepository.findOne(2L)).thenReturn(entry);

		toDoListApiController.deleteEntry(2L, 2L);
	}

	@Test(expected = NotFoundException.class)
	public void deleteEntryFailNotFound2Test() {
		ToDoList list = new ToDoList();
		list.setName("list 1");

		when(listRepository.findOne(2L)).thenReturn(list);
		when(entryRepository.findOne(2L)).thenReturn(null);

		toDoListApiController.deleteEntry(2L, 2L);
	}

	@Test(expected = DataIntegrationViolationException.class)
	public void deleteEntryFailDataIntegrationViolationTest() {
		ToDoList list = new ToDoList();
		list.setName("list 1");
		list.setId(1L);

		ToDoList list2 = new ToDoList();
		list2.setName("list 12");
		list2.setId(2L);

		when(listRepository.findOne(2L)).thenReturn(list2);

		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("element");
		entry.setList(list);
		entry.setId(3L);

		when(entryRepository.findOne(1L)).thenReturn(entry);

		ToDoEntry result = toDoListApiController.deleteEntry(2L, 1L);

		assertNotNull(result);
		assertEquals("element", result.getDescription());
	}
}
