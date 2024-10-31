package org.waterfogsw.httpinterface.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.waterfogsw.httpinterface.client.UserClient
import org.waterfogsw.httpinterface.domain.User

@Service
class UserService(
    private val userClient: UserClient
) {

    fun getUser(id: Long): User {
        return userClient.getUser(id)
    }

    fun createUser(user: User): User {
        return userClient.createUser(user)
    }

    fun getUserWithRetry(
        id: Long,
        failCount: Int?
    ): User {
        return userClient.getUserWithRetry(id, failCount)
    }

    fun getUserWithError(
        id: Long,
        errorType: String
    ): User {
        return userClient.getUserWithError(id, errorType)
    }

    fun deleteUser(id: Long) {
        userClient.deleteUser(id)
    }

    fun updateUser(
        id: Long,
        user: User
    ): User {
        return userClient.updateUser(id, user)
    }

    fun getUserWithTimeout(
        id: Long,
        delayMs: Long
    ): User {
        return userClient.getUserWithTimeout(id, delayMs)
    }

    companion object {

        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
