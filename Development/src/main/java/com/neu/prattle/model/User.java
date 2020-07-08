package com.neu.prattle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/***
 * A User object represents a basic account information for a user.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Entity
@Table(name = "USERS")
public class User {

  @Id
  @Column(name ="username",nullable = false)
	private String name;

	@Column(name = "password")
	private String password;

  @Column(name = "email")
  private String email;

  @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST
  )
  private List<GroupUser> groups = new ArrayList<>();

	protected User() {

	}

	// Initialize password to empty string instead of null, because SQL error when inserting null into DB.
  public User(String name) {
    this.name = name;
    this.password = "";
  }

  public User(String name, String password) {
    this.name = name;
    this.password = password;
  }

  public User(String name, String password, String email) {
    this.name = name;
    this.password = password;
    this.email = email;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<GroupUser> getGroups() {
    return groups;
  }

  public void setGroups(List<GroupUser> groups) {
	  if(groups==null)
	    throw new IllegalArgumentException("Reference groups is null.");
    this.groups = groups;
  }

  /***
     * Returns the hashCode of this object.
     *
     * As name can be treated as a sort of identifier for
     * this instance, we can use the hashCode of "name"
     * for the complete object.
     *
     *
     * @return hashCode of "this"
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /***
     * Makes comparison between two user accounts.
     *
     * Two user objects are equal if their name are equal ( names are case-sensitive )
     *
     * @param obj Object to compare
     * @return a predicate value for the comparison.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;

        User user = (User) obj;
        return user.name.equals(this.name);
    }
}
