package com.yaztap.user.json

import com.yaztap.user.pogo.Token
import com.yaztap.user.pogo.Tokens
import com.yaztap.user.pogo.TokenStatus
import groovy.json.JsonSlurper

import java.time.Instant
import java.time.temporal.ChronoUnit

import static com.yaztap.user.pogo.TokenStatus.*
import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

/**
 * @author sulaiman kadhodaei
 */
class JsonAPI {

    static def jsonSlurper = new JsonSlurper()
    private final static String pathnameJson = "forgotResetPassword.json"
    private static File jsonFile = new File(pathnameJson)
    private static final long expirationTimeInSeconds = 10/*min*/*60/*second*/

    static {
        if (!jsonFile.exists()) {
            jsonFile.createNewFile()
            jsonFile << "{}"
        }
    }

    static TokenStatus write(Token token) {
        try {
            jsonFile.write(prettyPrint(toJson(putToken(token))))
            return WRITED
        }catch(Exception ignored){
            return CANT_WRITE
        }
    }

    static Tokens putToken(Token newToken) {
        Tokens tokens = jsonSlurper.parseText(jsonFile.text)

        if (tokens == null){
            tokens = new Tokens()
        }

        if (tokens.list == null) {
            tokens.list = new LinkedList<Token>()
        }

        tokens.list.removeAll{ it.id == newToken.id }
        tokens.list.add(newToken)

        return tokens
    }


    static TokenStatus tokenStatus(Long id, String code) {
        Tokens tokens = jsonSlurper.parseText(jsonFile.text)
        if (tokens == null){
            return NO_JSON_FILE
        }

        if (tokens.getList() == null) {
            return EMPTY_JSON_FILE
        }

        Token token = tokens.list.find{ it.id == id }
        if (token == null) {
            return USER_HAVE_BEEN_NOT_REQUESTED
        }

        if (getDifferenceTimeInSeconds(token.startExpirationTime) > expirationTimeInSeconds){
            return TOKEN_HAS_BEEN_EXPIRED
        }

        return token.code == code ? MATCHED : NOT_MATCHED
    }

    static def getDifferenceTimeInSeconds(startExpirationTime) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTimeZone(TimeZone.getDefault())

        calendar.setTimeInMillis(startExpirationTime)
        Instant previous = calendar.toInstant()

        calendar.setTimeInMillis(System.currentTimeMillis())
        Instant now = calendar.toInstant()

        ChronoUnit.SECONDS.between(previous, now)
    }
//
//    static String getCode(Long id){
//        Tokens tokenList = readFile(jsonFile)
//
//        if (tokenList == null || tokenList.getList() == null) {
//            return null
//        }
//
//        for (Token fp : tokenList.getList()) {
//            if (fp.getId() == id){
//                return fp.getCode()
//            }
//        }
//
//        return null
//    }
}
