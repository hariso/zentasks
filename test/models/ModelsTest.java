package models;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.avaje.ebean.Ebean;

import play.libs.Yaml;
import play.test.WithApplication;

public class ModelsTest extends WithApplication {
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}

	@Test
	@Ignore
	public void createAndRetrieveUser() {
		new User("bob@gmail.com", "Bob", "secret").save();

		assertNotNull(User.authenticate("bob@gmail.com", "secret"));
		assertNull(User.authenticate("bob@gmail.com", "badpassword"));
		assertNull(User.authenticate("tom@gmail.com", "secret"));
	}

	@Test
	@Ignore
	public void findProjectsInvolving() {
		new User("bob@gmail.com", "Bob", "secret").save();
		new User("jane@gmail.com", "Jane", "secret").save();

		Project.create("Play 2", "play", "bob@gmail.com");
		Project.create("Play 1", "play", "jane@gmail.com");

		List<Project> results = Project.findInvolving("bob@gmail.com");
		assertEquals(1, results.size());
		assertEquals("Play 2", results.get(0).name);
	}

	@Test
	@Ignore
	public void findTodoTasksInvolving() {
		User bob = new User("bob@gmail.com", "Bob", "secret");
		bob.save();

		Project project = Project.create("Play 2", "play", "bob@gmail.com");
		Task t1 = new Task();
		t1.title = "Write tutorial";
		t1.assignedTo = bob;
		t1.done = true;
		t1.save();

		Task t2 = new Task();
		t2.title = "Release next version";
		t2.project = project;
		t2.save();

		List<Task> results = Task.findTodoInvolving("bob@gmail.com");
		assertEquals(1, results.size());
		assertEquals("Release next version", results.get(0).title);
	}

	@Test
	public void fullTest() {
		Ebean.save((Collection) Yaml.load("test-data.yml"));

		// Count things
		assertEquals(6, User.find.findRowCount());
		assertEquals(11, Project.find.findRowCount());
		assertEquals(6, Task.find.findRowCount());

		// Try to authenticate as users
		assertNotNull(User.authenticate("bob@example.com", "secret"));
		assertNotNull(User.authenticate("jane@example.com", "secret"));
		assertNull(User.authenticate("jeff@example.com", "badpassword"));
		assertNull(User.authenticate("tom@example.com", "secret"));

		// Find all Bob's projects
		List<Project> bobsProjects = Project.findInvolving("guillaume@sample.com");
		assertEquals(7, bobsProjects.size());

		// Find all Bob's todo tasks
		List<Task> bobsTasks = Task.findTodoInvolving("guillaume@sample.com");
		assertEquals(4, bobsTasks.size());
	}

}