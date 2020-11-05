package com.wg.redisson.redisson.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("t_user")
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createTime;

}
