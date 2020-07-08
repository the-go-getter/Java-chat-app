package com.neu.prattle.database;


import org.codehaus.jackson.map.deser.impl.CreatorProperty;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DataRepositoryPrivilegeTest {
  private static IDataRepository repo;
  private static String foo_user;
  private String test_user;
  private String nullString;

  @BeforeClass
  public static void setUpClass(){
    repo = DataRepository.getDataRepository();
    foo_user = "foo";
  }

  @Before
  public void setup() {

    test_user = "test_priv";
    nullString = null;
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullUser() {
    repo.addPrivilegeUser(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHasNullUser() {
    repo.hasPrivilege(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteNullUser() {
    repo.deletePrivilegeUser(nullString);
  }

  @Test
  public void testCrud(){
    assertFalse(repo.hasPrivilege(test_user));
    repo.addPrivilegeUser(test_user);
    assertTrue(repo.hasPrivilege(test_user));
    repo.deletePrivilegeUser(test_user);
    assertFalse(repo.hasPrivilege(test_user));
  }

  @Test(expected = IllegalStateException.class)
  public void testExistingUser(){
    repo.addPrivilegeUser(foo_user);
    repo.addPrivilegeUser(foo_user);
  }

  @Test
  public void testDeleteNonExisitingUser(){
    try{
      repo.deletePrivilegeUser("non_exist");
    }catch (Exception e){
      fail();
    }
  }

}
