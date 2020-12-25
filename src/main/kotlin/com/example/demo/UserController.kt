package com.example.demo

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/")
class UserController(private val userRepository: UserRepository) {
    @GetMapping("/users")
    fun getAllUsers(): List<User> = userRepository.findAll()
    @PostMapping("/users")
    fun createNewUser(@RequestBody user: User): User = userRepository.save(user)
    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<User> {
        return userRepository.findById(userId).map { user ->
            ResponseEntity.ok(user)
        }.orElse(ResponseEntity.notFound().build())
    }
    @PutMapping("/users/{id}")
    fun updateArticleById(@PathVariable(value = "id") userId: Long,
                          @RequestBody newUser: User): ResponseEntity<User>{
        return userRepository.findById(userId).map { existingUser ->
            val updatedUser: User = existingUser.copy(userName = newUser.userName, email = newUser.email)
            ResponseEntity.ok().body(userRepository.save(updatedUser))
        }.orElse(ResponseEntity.notFound().build())
    }
    @DeleteMapping("/users/{id}")
    fun deleteUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Void> {
        return userRepository.findById(userId).map { user ->
            userRepository.delete(user)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}