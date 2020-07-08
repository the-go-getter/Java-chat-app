package com.neu.prattle.database;

import com.neu.prattle.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DataRepositoryTest {
  private DataRepository repo;
  private User user;
  private User user1;
  private User user2;
  private User nullUser = null;
  private String nullUsername = null;

  @Before
  public void setup() {
    repo = DataRepository.getDataRepository();
    repo.deleteUserByUsername("testUser");
    user = new User("testUser", "testpassword");
    user1 = new User("testUser", "testpassword");
    user2 = new User("testUser_", "testpassword");
  }

  @Test
  public void testFindNonExistUser() {
    assertNull(repo.findUserByUsername("nonExist"));
  }

  @Test
  public void testFindExistUser() {
    repo.createUser(user);
    User user = repo.findUserByUsername("testUser");
    assertNotNull(user);
    assertEquals("testUser", user.getName());
    assertEquals("testpassword", user.getPassword());
  }

  @Test
  public void testRemoveUser() {
    repo.createUser(user);
    repo.deleteUserByUsername("testUser");
    assertNull(repo.findUserByUsername("testUser"));
  }

  @Test
  public void testUpdateUser() {
    repo.createUser(user);
    User userNew = new User("testUser", "password");
    repo.updateUser(userNew);
    assertEquals("password", repo.findUserByUsername("testUser").getPassword());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullUsername() {
    repo.findUserByUsername(nullUsername);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullUser() {
    repo.createUser(nullUser);
  }

  @Test
  public void testSetName() {
    user.setName("User");
    assertEquals("User", user.getName());
  }

  @Test
  public void testEquals() {
    assertTrue(user.equals(user1));
  }

  @Test
  public void testNotEquals() {
    assertFalse(user.equals(user2));
  }

  @Test
  public void testNotEqualsDifferentClass(){
    assertFalse(user.equals(repo));
  }

  @Test
  public void testSetEmail() {
    user.setEmail("u@u.info");
    assertEquals("u@u.info", user.getEmail());
  }

  @Test
  public void testGetPassword() {
    assertEquals("testpassword", user.getPassword());
  }


  @After
  public void tearDown() {
    repo.deleteUserByUsername("testUser");
  }
}