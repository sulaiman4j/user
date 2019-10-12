package com.yaztap.user.jpa

import com.yaztap.user.pogo.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author sulaiman kadhodaei
 */

@Repository
interface UserRepository extends JpaRepository<User, Long> {

}