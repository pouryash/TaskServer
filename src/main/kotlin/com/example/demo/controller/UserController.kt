package com.example.demo.controller

import com.example.demo.model.ResponseModel
import com.example.demo.model.UserDTO
import com.example.demo.service.UserServic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/")
class UserController() {

    @Autowired
    lateinit var userService: UserServic


    @GetMapping("/users")
    fun getAllUsers(@RequestHeader Authorization: String): ResponseEntity<ResponseModel> {
        return userService.getAllUsers(Authorization)
    }

    @PostMapping("/createUsers")
    fun createNewUser(@RequestBody userDTO: UserDTO?): ResponseEntity<ResponseModel> {
        return userService.createNewUser(userDTO)

    }

//    @PostMapping(
//        "/userLogin",
//        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    fun userLogin(@ModelAttribute userDTO: UserDTO): ResponseEntity<ResponseModel> {


    @PostMapping(
        "/userLogin"
    )
    fun userLogin(@RequestBody userDTO: UserDTO): ResponseEntity<ResponseModel> {
         return userService.userLogin(userDTO)

    }

    @PutMapping("/updateUser")
    fun updateUser(
        @RequestHeader Authorization: String,
        @RequestBody userDTO: UserDTO
    ): ResponseEntity<ResponseModel> {
      return userService.updateUser(Authorization, userDTO)
    }


//    @DeleteMapping("/users/{id}")
//    fun deleteUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Void> {
//        return userRepository.findById(userId).map { user ->
//            userRepository.delete(user)
//            ResponseEntity<Void>(HttpStatus.OK)
//        }.orElse(ResponseEntity.notFound().build())
//    }
}
