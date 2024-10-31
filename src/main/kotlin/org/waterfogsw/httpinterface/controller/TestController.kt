package org.waterfogsw.httpinterface.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.waterfogsw.httpinterface.domain.User
import org.waterfogsw.httpinterface.service.UserService

@RestController
@RequestMapping("/api/test")
class TestController(
    private val userService: UserService
) {
    // 기본 CRUD 작업 테스트
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): User {
        return userService.getUser(id)
    }

    @PostMapping("/users")
    fun createUser(@RequestBody user: User): User {
        return userService.createUser(user)
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) {
        userService.deleteUser(id)
    }

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody user: User
    ): User {
        return userService.updateUser(id, user)
    }

    // 재시도 로직 테스트
    @GetMapping("/users/retry/{id}")
    fun getUserWithRetry(
        @PathVariable id: Long,
        @RequestParam("failCount", required = false) failCount: Int?
    ): User {
        return userService.getUserWithRetry(id, failCount)
    }

    // 에러 케이스 테스트
    @GetMapping("/users/error/{id}")
    fun getUserWithError(
        @PathVariable id: Long,
        @RequestParam("errorType") errorType: String
    ): User {
        return userService.getUserWithError(id, errorType)
    }

    // 타임아웃 테스트
    @GetMapping("/users/timeout/{id}")
    fun getUserWithTimeout(
        @PathVariable id: Long,
        @RequestParam("delayMs", defaultValue = "5000") delayMs: Long
    ): User {
        return userService.getUserWithTimeout(id, delayMs)
    }
}
