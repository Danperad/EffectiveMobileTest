package ru.danperad.effectivemobiletest.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.danperad.effectivemobiletest.dto.NewUserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user", schema = "public")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements UserDetails {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Setter
    @Column(nullable = false, name = "last_name")
    private String lastName;

    @NotNull
    @Setter
    @Column(nullable = false, name = "first_name")
    private String firstName;

    @NotNull
    @Setter
    @Column(nullable = false, name = "email")
    private String email;

    @NotNull
    @Setter
    @Column(nullable = false, name = "password")
    private String password;

    @NotNull
    @Column(nullable = false, name = "role")
    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

    public static User fromNewUserDto(NewUserDto newUserDto) {
        User user = new User();
        user.setLastName(newUserDto.getLastName());
        user.setFirstName(newUserDto.getFirstName());
        user.setEmail(newUserDto.getEmail());
        user.setPassword(newUserDto.getPassword());
        user.setRole(newUserDto.getRole());
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (role.equals(Role.ROLE_ADMIN)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }
}
