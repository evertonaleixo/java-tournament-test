package com.example.todolist.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ToDoListTest {
	@Mock
	private ToDoEntry element1Mock;
	@Mock
	private ToDoEntry element2Mock;

	@Test
	public void createTest() {
		ToDoList entry = new ToDoList();
		
		assertNotNull(entry);
	}
	
	@Test
	public void validAttributesTest() {
		ToDoList entry = new ToDoList();
		
		entry.setName("List name test");
		assertEquals("List name test", entry.getName());
		
		entry.addEntry(element1Mock);
		entry.addEntry(element2Mock);
		
		assertEquals(2, entry.getEntries().size());
		assertTrue(entry.getEntries().contains(element2Mock));
		assertTrue(entry.getEntries().contains(element1Mock));
	}
}
