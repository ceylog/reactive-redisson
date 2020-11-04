package com.wg.redisson.redisson.dao;

import com.wg.redisson.redisson.entity.LoginEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginEventRepository extends ReactiveCrudRepository<LoginEvent, Long> {

}
