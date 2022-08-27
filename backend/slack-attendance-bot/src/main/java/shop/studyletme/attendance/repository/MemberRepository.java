package shop.studyletme.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.studyletme.attendance.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderId(String providerId);
}
