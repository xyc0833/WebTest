package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Users;

public interface UserMapper {
    @Select("select * from users where username=#{username} and password = #{password}")
    Users getUser(@Param("username")String username, @Param("password") String password);
    //方法有 2 个及以上参数 要加@Param
}
