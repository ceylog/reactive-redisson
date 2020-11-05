package com.wg.redisson.redisson.dao;

import com.wg.redisson.redisson.entity.po.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}
