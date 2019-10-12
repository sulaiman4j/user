package com.yaztap.user.pogo

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.Email

/**
 * @author sulaiman kadhodaei
 */
@Entity
@Table(name = "users")
@EqualsAndHashCode
@ToString(includePackage = false, includeFields = true)
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long identId

    @Email
    String email
    String firstName
    String lastName
    String accountNo
    String username
    String cell
    String city
    String address
    String birthday
    byte[] image

    boolean active
    boolean blocked
    boolean marginCalled
    boolean removed
    boolean callVerified
}
