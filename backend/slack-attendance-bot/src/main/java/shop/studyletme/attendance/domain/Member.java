package shop.studyletme.attendance.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements GrantedAuthority {
    @Id
    @GeneratedValue
    private Long id;

    private String password;
    private String email;
    private String name;
    private String providerId;
    private String provider;
    private String role;

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Builder
    public Member(Long id, String password, String email, String name, String providerId, String provider, String role) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.name = name;
        this.providerId = providerId;
        this.provider = provider;
        this.role = role;
    }
}
