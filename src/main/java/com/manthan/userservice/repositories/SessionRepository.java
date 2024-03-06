package com.manthan.userservice.repositories;

import com.manthan.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {
    @Override
    Session save(Session entity);
}
