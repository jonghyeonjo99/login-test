package test.Springboot_Login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.Springboot_Login.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);
    boolean existsByNickName(String nickName);
    Optional<User> findByLoginId(String loginId);
}
