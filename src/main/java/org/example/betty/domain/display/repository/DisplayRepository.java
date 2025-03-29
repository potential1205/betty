package org.example.betty.domain.display.repository;

import org.example.betty.domain.display.entity.Display;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayRepository extends JpaRepository<Display, Long> {
    List<Display> findByIdIn(List<Long> displayIdList);
}
