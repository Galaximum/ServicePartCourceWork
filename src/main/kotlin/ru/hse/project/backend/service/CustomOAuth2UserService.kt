package ru.hse.project.backend.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import ru.hse.project.backend.constant.GoogleAttributes
import ru.hse.project.backend.domain.CustomOAuth2User
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.model.User

@Service
class CustomOAuth2UserService(
    private val userService: UserService
) : DefaultOAuth2UserService() {

    companion object {
        private val PARAMETERIZED_RESPONSE_TYPE = object : ParameterizedTypeReference<Map<String, Any>>() {}
    }

    private val restOperations = RestTemplate().also { it.errorHandler = OAuth2ErrorResponseErrorHandler() }
    private val requestEntityConverter = OAuth2UserRequestEntityConverter()

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = SecurityContextHolder.getContext().authentication?.principal as? User
        if (userRequest.clientRegistration?.registrationId == "google") {
            return processGoogleAuth(userRequest, user)
        }
        throw UserException("Неподдерживаемый способ авторизации: ${userRequest.clientRegistration?.registrationId}")
    }

    /**
     * Обработка авторизации через Google
     */
    private fun processGoogleAuth(userRequest: OAuth2UserRequest, authorizedUser: User?): OAuth2User {
        // 1. Парсим запрос
        val (userNameAttributeName, response) = validateRequestAndGetResponse(userRequest)
        val (authorities, userAttributes) = getUserAuthoritiesAndAttributes(response, userRequest.accessToken)
        // 2. Получаем базовые данные и проверяем, есть ли такой пользователь
        val oAuthUser = CustomOAuth2User(authorities, userAttributes, userNameAttributeName, authorizedUser == null)
        val googleId = userAttributes[GoogleAttributes.ID.att] as? String ?: throw getExceptionIfAttributeIsMissed(
            GoogleAttributes.EMAIL.att,
            "google"
        )
        val oAuthAccount = userService.findByGoogleId(googleId)
        // 3. Если да - возвращаем его, иначе - создаем
        if (oAuthAccount.isPresent) {
            authorizedUser?.throwIfAuthorized(oAuthAccount.get())
            return oAuthUser.apply { user = oAuthAccount.get() }
        }

        val gmail =
            userAttributes.getObjectAsString(GoogleAttributes.EMAIL.att, googleId)
        val name =
            userAttributes.getObjectAsString(GoogleAttributes.FIRST_NAME.att, googleId)
        val surname =
            userAttributes.getObjectAsString(GoogleAttributes.SECOND_NAME.att, googleId)
        val image =
            userAttributes.getObjectAsString(GoogleAttributes.PICTURE.att, googleId)

        val user = User(nickName = "$name $surname", firstName = name, secondName = surname, email = gmail, urlImage = image, googleId = googleId)

        oAuthUser.user = userService.save(user)
        return oAuthUser
    }

    /**
     * Вспомогательный метод для получения значений из словаря
     */
    private fun Map<String, Any>.getObjectAsString(
        attribute: String,
        accountId: String? = null
    ) =
        this[attribute] as? String ?: throw getExceptionIfAttributeIsMissed(attribute, accountId)

    /**
     * Вспомогательный метод для генерации исключений
     */
    private fun getExceptionIfAttributeIsMissed(
        attributeName: String,
        id: String? = null
    ): UserException {
        val additionalDescription = if (id != null) " через google по id: $id" else ""
        val errorMessage =
            "Отсутсвует обязательный аттрибут \"$attributeName\" в запросе на OAuth авторизацию$additionalDescription"
        return UserException(errorMessage)
    }

    /**
     * Метод дублирует логику DefaultOAuth2UserService.loadUser(), относящуюся к валидации запроса.
     * Возвращает пару из атрибута имени пользователя и [ResponseEntity]
     */
    private fun validateRequestAndGetResponse(userRequest: OAuth2UserRequest): Pair<String, ResponseEntity<Map<String, Any>>> {
        if (userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri.isNullOrEmpty()) {
            throw getExceptionIfAttributeIsMissed("UserInfo Uri")
        }
        val userNameAttributeName =
            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        if (userNameAttributeName.isNullOrEmpty()) {
            throw getExceptionIfAttributeIsMissed("user name")
        }

        val entity = requestEntityConverter.convert(userRequest)
            ?: throw UserException("Невозможно распарсить запрос на регистрацию")
        val message = "Ошибка возникла при попытке получить UserInfo: "
        return userNameAttributeName to try {
            restOperations.exchange(entity, PARAMETERIZED_RESPONSE_TYPE)
        } catch (e: OAuth2AuthorizationException) {
            val uri = userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri
            val additionDescription = when (e.error.description) {
                null -> ", описание ошибки: ${e.error.description}"
                else -> ""
            }
            val errorDetails =
                "Сведения об ошибке: [UserInfo Uri: $uri, Error Code: ${e.error.errorCode}$additionDescription]"
            throw UserException("$message$errorDetails")
        } catch (e: RestClientException) {
            throw UserException("$message${e.message}")
        }
    }

    /**
     * Парсит [ResponseEntity] и возвращает пару из authorities и атрибутов пользователя
     */
    private fun getUserAuthoritiesAndAttributes(
        response: ResponseEntity<Map<String, Any>>,
        token: OAuth2AccessToken
    ): Pair<MutableSet<GrantedAuthority>, Map<String, Any>> {
        val userAttributes =
            response.body ?: throw UserException("Тело ответа не должно иметь значение null")
        val authorities: MutableSet<GrantedAuthority> = mutableSetOf(OAuth2UserAuthority(userAttributes))
        for (authority in token.scopes) {
            authorities.add(SimpleGrantedAuthority("SCOPE_$authority"))
        }

        return authorities to userAttributes
    }

    private fun User.throwIfAuthorized(foundAccount: User) {
        if (id != foundAccount.id) {
            throw UserException("Профиль в сервисе google уже связан  с другим аккаунтом")
        }
    }
}