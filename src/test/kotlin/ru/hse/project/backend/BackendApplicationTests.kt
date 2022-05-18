package ru.hse.project.backend

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.model.User
import ru.hse.project.backend.repository.TrashRepository
import ru.hse.project.backend.repository.UserRepository
import ru.hse.project.backend.service.JwtService
import ru.hse.project.backend.service.TrashService
import ru.hse.project.backend.service.UserService

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class BackendApplicationTests {
    val token = "JWT"
    val headers = HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, "Bearer $token") }
    @LocalServerPort
    var port: Int = 0

    @AfterEach
    fun `Cleaning database`() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0")
        jdbcTemplate.execute("TRUNCATE TABLE trash_user_favorite_trash_cans")
        jdbcTemplate.execute("TRUNCATE TABLE trash_user_visited_trash_cans")
        jdbcTemplate.execute("TRUNCATE TABLE trash_user")
        jdbcTemplate.execute("TRUNCATE TABLE trash_can")
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1")
    }

    companion object {
        @Container
        val container = MySQLContainer<Nothing>(
            DockerImageName.parse("mysql:5.7")
        ).apply {
            withDatabaseName("homestead")
            withPassword("secret")
            withUsername("root")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.username", container::getUsername)
        }

        init {
            container.start()
        }
    }

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var trashService: TrashService

    @Autowired
    lateinit var trashRepository: TrashRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @MockkBean
    lateinit var jwtService: JwtService

    protected fun mockAuthentication(user: User) {
        every { jwtService.getIdFromToken(token) } returns user.id
        every { jwtService.validateToken(token) } returns Unit
    }

    fun createTrashCan(title: String, latitude: Double, longitude: Double) =
        trashRepository.save(TrashCan(title = title, latitude = latitude, longitude = longitude))

    fun createUser(nickName: String, googleId: String) =
        userRepository.save(User(nickName = nickName, googleId = googleId))

    @Transactional
    fun addTrashContainerToFavorite(userId: Long, trashCan: TrashCan) = transactionTemplate.execute {
        userRepository.findById(userId).get().let {
            it.favoriteTrashCans.add(trashCan)
            userRepository.save(it)
        }
    }!!

    @Transactional
    fun addTrashContainerToVisited(userId: Long, trashCan: TrashCan) = transactionTemplate.execute {
        userRepository.findById(userId).get().let {
            it.visitedTrashCans.add(trashCan)
            userRepository.save(it)
        }
    }!!
}
