package com.example.demo.service

import com.example.demo.Dbmodel.User
import com.example.demo.model.ResponseModel
import com.example.demo.model.UserDTO
import com.example.demo.repository.UserRepo
import com.example.demo.utils.DateUtils
import io.jsonwebtoken.Jwts
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import io.jsonwebtoken.security.Keys

import io.jsonwebtoken.io.Decoders


@Service
class UserServic(private val userRepository: UserRepo) {

    fun getAllUsers(Authorization: String): ResponseEntity<ResponseModel> {
        userRepository.findByToken(Authorization)?.let {
            if (it.role == "admin")
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        convertUserEntityListToUserDtoList(userRepository.findAll())
                    ), HttpStatus.OK
                )
        }
        return ResponseEntity(
            ResponseModel(
                HttpStatus.NOT_FOUND.value(),
                "User not found"
            ), HttpStatus.NOT_FOUND
        )

    }


    fun createNewUser(userDTO: UserDTO?): ResponseEntity<ResponseModel> {
        userDTO?.let {
            if (userDTO.userName.isNotEmpty() && userDTO.password.isNotEmpty() && userDTO.email.isNotEmpty()) {

                userRepository.findByEmail(userDTO.email)?.let {
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.CONFLICT.value(),
                            HttpStatus.CONFLICT.reasonPhrase
                        ), HttpStatus.CONFLICT
                    )
                }

                userDTO.token = getJWTToken(userDTO)
                userDTO.role = "user"
                userDTO.createDate = DateUtils.convertDateToString(Date())
                val user = convertUserDtoToUserEntity(userDTO)

                userRepository.save(user).let {
                    userDTO.id = user.id
                }
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        convertUserEntityToUserDto(user)
                    ), HttpStatus.OK
                )
            }
        }

        return ResponseEntity(
            ResponseModel(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.reasonPhrase,
            ), HttpStatus.BAD_REQUEST
        )

    }


    fun userLogin(userDTO: UserDTO): ResponseEntity<ResponseModel> {
        userRepository.findByEmailAndPassword(userDTO.email, userDTO.password)?.let { user ->
            return ResponseEntity(
                ResponseModel(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.reasonPhrase,
                    convertUserEntityToUserDto(user)
                ), HttpStatus.OK
            )
        }

        return ResponseEntity(
            ResponseModel(
                HttpStatus.NOT_FOUND.value(),
                "User not found"
            ), HttpStatus.NOT_FOUND
        )

    }


    fun updateUser(Authorization: String, userDTO: UserDTO): ResponseEntity<ResponseModel> {
        if (userDTO.userName.isNotEmpty() && userDTO.password.isNotEmpty() && userDTO.email.isNotEmpty()) {

            userRepository.findByToken(Authorization)?.let { existingUser ->
                val updatedUserEntity: User =
                    existingUser.copy(
                        userName = userDTO.userName, email = userDTO.email,
                        password = userDTO.password
                    )
                userRepository.save(updatedUserEntity)
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        convertUserEntityToUserDto(existingUser)
                    ), HttpStatus.OK
                )
            }
            return ResponseEntity(
                ResponseModel(
                    HttpStatus.NOT_FOUND.value(),
                    "User not found"
                ), HttpStatus.NOT_FOUND
            )

        }
        return ResponseEntity(
            ResponseModel(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.reasonPhrase,
            ), HttpStatus.BAD_REQUEST
        )
    }


    fun convertUserEntityListToUserDtoList(userList: List<User>): List<UserDTO> {
        return userList.map {
            convertUserEntityToUserDto(it)
        }
    }

    fun convertUserEntityToUserDto(user: User): UserDTO {
        return UserDTO(
            id = user.id, userName = user.userName, password = user.password,
            createDate = user.createDate, role = user.role, email = user.email, token = user.token
        )
    }

    fun convertUserDtoListToUserList(userDTOList: List<UserDTO>): List<User> {
        return userDTOList.map {
            convertUserDtoToUserEntity(it)
        }
    }

    fun convertUserDtoToUserEntity(userDTO: UserDTO): User {
        return User(
            id = userDTO.id, userName = userDTO.userName, password = userDTO.password,
            createDate = userDTO.createDate, role = userDTO.role, email = userDTO.email, token = userDTO.token
        )
    }

    fun getJWTToken(userDTO: UserDTO): String {
        val secretKey = "mySecretKeyForJWTUserTaskApplicationthisisfortestinjwtddddddddddddd"
        val keyBytes = Decoders.BASE64.decode(secretKey)

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
            .signWith(Keys.hmacShaKeyFor(keyBytes)).compact()
        return "Bearer $token"
    }
}