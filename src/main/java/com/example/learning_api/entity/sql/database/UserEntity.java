package com.example.learning_api.entity.sql.database;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@Document(collection = "users")
public class UserEntity implements UserDetails  {
    @Id
    private String id;
    private String email;
    private String password;
    private String fullname;
    private RoleEnum role;
    private List<RoleEnum> roleList;
    private UserStatus status;
    private String avatar;
    private String authType;
    private StudentEntity student;
    private TeacherEntity teacher;
    private Date createdAt;
    private Date updatedAt;

    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
