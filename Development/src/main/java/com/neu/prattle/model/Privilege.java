package com.neu.prattle.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PRIVILEGES")
public class Privilege {
  @Id
  private String username;

  public Privilege() {
  }

  public Privilege(String username) {
    if (username == null)
      throw new IllegalArgumentException("Privilege Constructor: Reference username is null.");
    else
      this.username = username;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    if (username == null)
      throw new IllegalArgumentException("setUsername: Reference username is null.");
    else
      this.username = username;
  }
}
