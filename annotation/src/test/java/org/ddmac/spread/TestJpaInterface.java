package org.ddmac.spread;

import org.ddmac.spread.enums.Serializer;
import org.springframework.data.jpa.repository.JpaRepository;

@SpREAD(path = "/test", serializer = Serializer.GSON)
public interface TestJpaInterface extends JpaRepository<TestEntity,Long> {}
