package com.example.demo.service

import com.example.demo.Dbmodel.User
import com.example.demo.model.UserDTO
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class UserServic {

    fun convertUserEntityListToUserDtoList(userList: List<User>) : List<UserDTO>{
        return userList.map {
            convertUserEntityToUserDto(it)
        }
    }

    fun convertUserEntityToUserDto(user: User) : UserDTO{
        return UserDTO(id = user.id, userName = user.userName, password = user.password,
            createDate = user.createDate, role = user.role, email = user.email)
    }

    fun convertUserDtoListToUserList(userDTOList: List<UserDTO>) : List<User>{
        return userDTOList.map {
            convertUserDtoToUserEntity(it)
        }
    }

    fun convertUserDtoToUserEntity(userDTO: UserDTO) : User{
        return User(id = userDTO.id, userName = userDTO.userName, password = userDTO.password,
            createDate = userDTO.createDate, role = userDTO.role, email = userDTO.email, token = userDTO.token)
    }

    fun getJWTToken(userDTO: UserDTO): String {
        val secretKey = "mySecretKeyForJWTUserTaskApplicationthisisfortestinjwtddddddddddddd"
        val grantedAuthorities = AuthorityUtils
            .commaSeparatedStringToAuthorityList("ROLE_USER")
        val token = Jwts
            .builder()
            .setId("userTaskJWT")
            .setSubject(userDTO.email)
            .claim("authorities",
                grantedAuthorities.stream()
                    .map { obj: GrantedAuthority -> obj.authority }
                    .collect(Collectors.toList()))
            .setIssuedAt(Date(System.currentTimeMillis()))
            .signWith(
                SignatureAlgorithm.HS512,
                secretKey.toByteArray()
            ).compact()
        return "Bearer $token"
    }
}