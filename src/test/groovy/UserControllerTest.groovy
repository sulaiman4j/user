

import com.fasterxml.jackson.databind.ObjectMapper
import com.yaztap.user.controller.UserController
import com.yaztap.user.jpa.UserRepository
import com.yaztap.user.pogo.User
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class UserControllerTest extends Specification {

    //declare variables of setup
    UserRepository repository
    UserController controller
    MockMvc mockMvc
    ObjectMapper mapper = new ObjectMapper()
    def requestUri = '/api/users'

    //declare our data
    User sulaiman
    User mpsk
    String sulaimanJsonString
    String mpskJsonString

    //initialize data and variables before run tests
    void setup() {

        repository = Mock(UserRepository)
        controller = new UserController([userRepository : repository])
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .alwaysDo(MockMvcResultHandlers.print())
                .build()

        sulaiman = new User([identId : 1, firstName : 'sulaiman', email : 'sulaiman@email.com'])
        mpsk = new User([identId : 2, firstName : 'mpsk', email : 'mpsk@email.com'])

        sulaimanJsonString = mapper.writeValueAsString(sulaiman)
        mpskJsonString = mapper.writeValueAsString(mpsk)
    }

    void 'get users return a list of users'() {
        given:
        repository.findAll() >> [sulaiman, mpsk]

        and:
        def response = [sulaimanJsonString, mpskJsonString].toString()

        expect:
        mockMvc.perform(MockMvcRequestBuilders
                .get(requestUri))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
    }

    void 'get user returns a single user'() {
        given:
        1 * repository.findById(1) >> Optional.of(sulaiman)

        expect:
        mockMvc.perform(MockMvcRequestBuilders
                .get("$requestUri/1", ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(sulaimanJsonString))
    }

    void 'save user'() {
        given:
        1 * repository.save(sulaiman) >> null

        expect:
        mockMvc.perform(MockMvcRequestBuilders
                .post(requestUri).contentType(MediaType.APPLICATION_JSON).content(sulaimanJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void 'delete user'() {
        given:
        1 * repository.deleteById(2) >> null

        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("$requestUri/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void 'update an existing user succeeds'() {
        given:
        1 * repository.findById(1) >> Optional.of(sulaiman)
        1 * repository.save(sulaiman) >> null

        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("$requestUri/1")
                .contentType(MediaType.APPLICATION_JSON).content(sulaimanJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void 'update a non-existing user fails'() {
        when:
        mockMvc.perform(MockMvcRequestBuilders.put("$requestUri/3")
                .contentType(MediaType.APPLICATION_JSON).content(mpskJsonString))

        then:
        thrown(Exception)
    }
}
