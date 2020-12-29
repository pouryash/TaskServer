package com.example.demo.UserApi

import com.example.demo.model.ResponseModel
import com.example.demo.Dbmodel.User
import com.example.demo.model.UserDTO
import com.example.demo.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/")
class UserController(private val userRepository: UserRepository) {

    @Autowired
    lateinit var userService: UserService


    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<ResponseModel> {
        return ResponseEntity(
            ResponseModel(
                HttpStatus.OK.value(),
                HttpStatus.OK.reasonPhrase,
                userService.convertUserEntityListToUserDtoList(userRepository.findAll())
            ), HttpStatus.OK
        )
    }

    @PostMapping("/createUsers")
    fun createNewUser(@RequestBody userDTO: UserDTO?): ResponseEntity<ResponseModel> {
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

                userDTO.token = userService.getJWTToken(userDTO)
                userDTO.createDate = DateUtils.getCurrentDate()
                var user = userService.convertUserDtoToUserEntity(userDTO)

                userRepository.save(user).let {
                    userDTO.id = user.id
                }
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        userDTO.token
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

//    @PostMapping(
//        "/userLogin",
//        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    fun userLogin(@ModelAttribute userDTO: UserDTO): ResponseEntity<ResponseModel> {


    @PostMapping(
        "/userLogin",
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun userLogin(@ModelAttribute userDTO: UserDTO): ResponseEntity<ResponseModel> {
         userRepository.findByUserNameAndPassword(userDTO.userName, userDTO.password)?.let { user ->
           return ResponseEntity(
                ResponseModel(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.reasonPhrase,
                    user.token
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

    @PutMapping("/updateUser")
    fun updateUser(
        @RequestHeader Authorization: String,
        @RequestBody userDTO: UserDTO
    ): ResponseEntity<ResponseModel> {
        if (userDTO.userName.isNotEmpty() && userDTO.password.isNotEmpty() && userDTO.email.isNotEmpty()) {

            userRepository.findByToken(Authorization)?.let { existingUser ->
                val updatedUserEntity: User =
                    existingUser.copy(
                        userName = userDTO.userName, email = userDTO.email,
                        password = userDTO.password, createDate = DateUtils.getCurrentDate()
                    )
                userRepository.save(updatedUserEntity)
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase
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


//    @DeleteMapping("/users/{id}")
//    fun deleteUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Void> {
//        return userRepository.findById(userId).map { user ->
//            userRepository.delete(user)
//            ResponseEntity<Void>(HttpStatus.OK)
//        }.orElse(ResponseEntity.notFound().build())
//    }
}
