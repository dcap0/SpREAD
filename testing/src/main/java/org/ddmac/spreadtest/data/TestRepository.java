package org.ddmac.spread.test.data;

import org.ddmac.spread.SpREAD;
import org.springframework.data.jpa.repository.JpaRepository;

@SpREAD
public interface TestRepository extends JpaRepository<TestEntity,Long> {
}
