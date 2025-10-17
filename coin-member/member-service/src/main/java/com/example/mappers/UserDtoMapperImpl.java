package com.example.mappers;

import com.example.domin.User;
import com.example.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


public class UserDtoMapperImpl {

    public static UserDto convert2Dto1(User source) {
        if (source == null) {
            return null;
        } else {
            UserDto userDto = new UserDto();
            userDto.setId(source.getId());
            userDto.setUsername(source.getUsername());
            userDto.setCountryCode(source.getCountryCode());
            userDto.setMobile(source.getMobile());
            userDto.setEmail(source.getEmail());
            userDto.setRealName(source.getRealName());
            userDto.setPaypassword(source.getPaypassword());
            return userDto;
        }
    }

    public User convert2Entity(UserDto source) {
        if (source == null) {
            return null;
        } else {
            User user = new User();
            user.setId(source.getId());
            user.setUsername(source.getUsername());
            user.setCountryCode(source.getCountryCode());
            user.setMobile(source.getMobile());
            user.setPaypassword(source.getPaypassword());
            user.setEmail(source.getEmail());
            user.setRealName(source.getRealName());
            return user;
        }
    }

    public static List<UserDto> convert2Dto(List<User> source) {
        if (source == null) {
            return null;
        } else {
            List<UserDto> list = new ArrayList(source.size());

            for(User user : source) {
                list.add(convert2Dto1(user));
            }

            return list;
        }
    }

    public List<User> convert2Entity(List<UserDto> source) {
        if (source == null) {
            return null;
        } else {
            List<User> list = new ArrayList(source.size());

            for(UserDto userDto : source) {
                list.add(this.convert2Entity(userDto));
            }

            return list;
        }
    }
}
