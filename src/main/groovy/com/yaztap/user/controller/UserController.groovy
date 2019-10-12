package com.yaztap.user.controller

import com.yaztap.user.jpa.UserRepository
import com.yaztap.user.pogo.Token
import com.yaztap.user.pogo.TokenStatus
import com.yaztap.user.pogo.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import static com.yaztap.user.json.JsonAPI.tokenStatus
import static com.yaztap.user.json.JsonAPI.write
import static com.yaztap.user.pogo.TokenStatus.CANT_WRITE

/**
 * @author sulaiman kadhodaei
 */

@RestController
@RequestMapping('api/users')
@Slf4j
class UserController {

    @Autowired
    UserRepository userRepository

    @GetMapping
    List getAllUsers() {
        userRepository.findAll()
    }

    @GetMapping('{id}')
    User getUserById(@PathVariable long id) {
        userRepository.findById(id).orElse(null)
    }

    @PostMapping()
    void createUser(@RequestBody User user) {
        userRepository.save(user)
        log.info("Successfully saved user $user")
    }

    @PutMapping('{id}')
    void updateUser(@PathVariable Long id, @RequestBody User body) {
        def user = userRepository.findById(id)

        if (user == Optional.empty()) {
            throw new Exception("Error user does not exist with id $id")
        }

        userRepository.save(body)
        log.info("Successfully updated user $user")
    }

    @DeleteMapping('{id}')
    void deleteUser(@PathVariable Long id){
        userRepository.deleteById(id)
        log.info("Successfully deleted user $id")
    }

    @PostMapping('/forget-password')
    void forgotPassword(Long userId) {
        Token token = new Token()
        def code = ""

        while (code.size() < 5) {
            code = UUID.randomUUID().toString()
                    .replaceAll('\\D+', '').substring(0, 5)
        }

        token.id = userId
        token.code = code
        token.startExpirationTime = System.currentTimeMillis()

        if(write(token) == CANT_WRITE){
            throw new Exception("Error could not create token for user with id $userId")
        }

        // TODO : here should send token to user with this id
    }

    @PostMapping('/match-token')
    TokenStatus matchToken(Long userId, String code) {
        return tokenStatus(userId, code)
    }

    @PostMapping('/reset-json')
    void resetPassword(Long userId, String json) {
        //TODO : last step reset json, if not save pass just remove it
    }
}
