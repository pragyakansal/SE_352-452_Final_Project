package edu.kansal_wells_xu_pina.realestate_api.entities;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/* @Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "ROLE_USER", "ROLE_ADMIN"

    /* @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>(); */

    // --- Constructors ---
  /*  public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) { this.users = users; }
}
*/