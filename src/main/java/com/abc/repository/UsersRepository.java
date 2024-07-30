package com.abc.repository;




import com.abc.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
     boolean existsByUsername(String username);
     //Optional<Users> findByUsername(String username);
     Optional<Users> findByUsername(String username);
}
