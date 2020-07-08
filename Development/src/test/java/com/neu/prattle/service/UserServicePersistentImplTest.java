package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotPresentException;
import com.neu.prattle.model.User;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class UserServicePersistentImplTest {
  private static UserService userService;
  private static User nullUser;
  private static User testUser;
  private static String nullUsername;

  @BeforeClass
  public static void setUp() throws Exception {
    userService = UserServicePersistentImpl.getInstance();
    nullUser = null;
    testUser = new User("us_tester","password");
    userService.addUser(testUser);
    nullUsername = null;
  }

  @Test
  public void testGetInstance() {
    UserService us = UserServicePersistentImpl.getInstance();
    assertNotNull(us);
    assertEquals(us,userService);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindUserByNameNull(){
    userService.findUserByName(nullUsername);
  }

  @Test
  public void testFindUserByName() {
    Optional<User> user = userService.findUserByName("us_tester");
    assertTrue(user.isPresent());
  }

  @Test
  public void testFindUserByNameNonexist() {
    Optional<User> user = userService.findUserByName("ustester");
    assertFalse(user.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddUserNull(){
    userService.addUser(nullUser);
  }

  @Test
  public void addUser() {
    User u = new User("add","passwd");
    userService.addUser(u);
    assertTrue(userService.findUserByName("add").isPresent());
  }

  @Test(expected = UserAlreadyPresentException.class)
  public void testAddExistingUser(){
    User u = new User("us_tester","password");
    userService.addUser(u);
  }
}