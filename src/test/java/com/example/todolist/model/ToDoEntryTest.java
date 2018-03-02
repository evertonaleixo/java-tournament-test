package com.example.todolist.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ToDoEntryTest {
	
	@Mock
	private ToDoList list;

	@Test
	public void createTest() {
		ToDoEntry entry = new ToDoEntry();
		
		assertNotNull(entry);
	}
	
	@Test
	public void validAttributesTest() {
		ToDoEntry entry = new ToDoEntry();
		
		entry.setDescription("Entry test");
		assertEquals("Entry test", entry.getDescription());
		
		entry.setList(list);
		assertEquals(list, entry.getList());
	}
}
