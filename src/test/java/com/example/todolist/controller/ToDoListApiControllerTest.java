package com.example.todolist.controller;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.example.todolist.exceptions.DataIntegrationViolationException;
import com.example.todolist.exceptions.NotFoundException;
import com.example.todolist.model.ToDoEntry;
import com.example.todolist.model.ToDoList;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(ToDoListApiController.class)
public class ToDoListApiControllerTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private ToDoListApiController toDoListController;

	@Test
	public void getEmptyLitsTest() throws Exception {
		List<ToDoList> lists = new ArrayList<>();
		
		given(toDoListController.getLists()).willReturn(lists);
		
		mvc.perform(get("/api").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@Test
	public void getOneLitTest() throws Exception {
		List<ToDoList> lists = new ArrayList<>();
		ToDoList toDoList = new ToDoList();
		lists.add(toDoList);
		
		given(toDoListController.getLists()).willReturn(lists);
		
		mvc.perform(get("/api").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].entries", hasSize(0)));
	}
	
	@Test
	public void getTwoLitTest() throws Exception {
		List<ToDoList> lists = new ArrayList<>();
		ToDoList toDoList = new ToDoList();
		ToDoEntry element = new ToDoEntry();
		element.setDescription("element");
		toDoList.addEntry(element);
		ToDoList toDoList2 = new ToDoList();
		lists.add(toDoList);
		lists.add(toDoList2);
		
		given(toDoListController.getLists()).willReturn(lists);
		
		mvc.perform(get("/api").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].entries", hasSize(1)))
			.andExpect(jsonPath("$[0].entries[0].description", is("element")))
			.andExpect(jsonPath("$[1].entries", hasSize(0)));
	}
	
	@Test
	public void getListByIdOkTest() throws Exception {
		List<ToDoEntry> lists = new ArrayList<>();
		
		ToDoEntry element = new ToDoEntry();
		element.setDescription("element");
		lists.add(element);
		
		given(toDoListController.getListEntries(1L)).willReturn(lists);
		
		mvc.perform(get("/api/1").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].description", is("element")));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getListByIdNotFoundTest() throws Exception {
		given(toDoListController.getListEntries(2L)).willThrow(NotFoundException.class);
		
		mvc.perform(get("/api/2").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void createListOkTest() throws Exception {
		ToDoList list = new ToDoList();
		list.setName("list 1");
		
		given(toDoListController.createList(list )).willReturn(list);
		
		mvc.perform(post("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"list 1\"}"))
			.andExpect(status().isCreated());
	}
	
	@Test
	public void createListOk2Test() throws Exception {
		ToDoList list = new ToDoList();
		list.setName("list 1");
		
		given(toDoListController.createList(list )).willReturn(list);
		
		mvc.perform(post("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"list 1\", \"entries\":[]}"))
			.andExpect(status().isCreated());
	}
	
	@Test
	public void createListFailMalFormedTest() throws Exception {
		ToDoList list = new ToDoList();
		list.setName("list 1");
		
		given(toDoListController.createList(list )).willReturn(list);
		
		mvc.perform(post("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name1\": \"list 1\", \"entries\":[]}"))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void createToDoOkTest() throws Exception {
		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("todo 1");
		given(toDoListController.createEntry(1L, entry)).willReturn(entry);
		
		mvc.perform(post("/api/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\": \"todo 1\"}"))
			.andExpect(status().isCreated());
	}
	
	public static String asJsonString(final Object obj) {
	    try {
	        final ObjectMapper mapper = new ObjectMapper();
	        final String jsonContent = mapper.writeValueAsString(obj);
	        return jsonContent;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	} 
		
	@Test
	public void createToDoFailMalFormedTest() throws Exception {
		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("todo 1");
		given(toDoListController.createEntry(1L, entry)).willReturn(entry);
		
		mvc.perform(post("/api/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\": \"todo\""))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void createToDoFailMalFormed2Test() throws Exception {
		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("todo 1");
		given(toDoListController.createEntry(1L, entry)).willReturn(entry);
		
		mvc.perform(post("/api/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descriptions\": \"todo\"}"))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void deleteListOkTest() throws Exception {
		ToDoList list = new ToDoList();
		list.setName("list 1");
		
		given(toDoListController.deleteList(1L)).willReturn(list);
		
		mvc.perform(delete("/api/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteListFailNotFoundTest() throws Exception {
		given(toDoListController.deleteList(2L)).willThrow(NotFoundException.class);
		
		mvc.perform(delete("/api/2")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteEntryOkTest() throws Exception {
		ToDoEntry entry = new ToDoEntry();
		entry.setDescription("todo 1");
		
		given(toDoListController.deleteEntry(1L, 1L)).willReturn(entry);
		
		mvc.perform(delete("/api/1/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteEntryFailNotFoundTest() throws Exception {
		given(toDoListController.deleteEntry(2L, 1L)).willThrow(NotFoundException.class);
		
		mvc.perform(delete("/api/1/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		mvc.perform(delete("/api/2/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteEntryFailNotFound2Test() throws Exception {
		given(toDoListController.deleteEntry(1L, 2L)).willThrow(NotFoundException.class);
		
		mvc.perform(delete("/api/1/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		mvc.perform(delete("/api/1/2")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteEntryFailDataViolationTest() throws Exception {
		given(toDoListController.deleteEntry(1L, 3L)).willThrow(DataIntegrationViolationException.class);
		
		mvc.perform(delete("/api/1/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		mvc.perform(delete("/api/1/3")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
	
	
}
