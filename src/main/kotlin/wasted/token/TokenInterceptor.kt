package wasted.token

import org.apache.commons.codec.digest.DigestUtils.sha256Hex
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@Component
class TokenInterceptor : HandlerInterceptor {

  @Value("\${api-token}")
  lateinit var apiToken: String

  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    if (setOf("/user", "/expense", "/total").none { request.requestURI.startsWith(it) }) return true
    val apiTokenHeader = request.getHeader("api-token")
    if (apiTokenHeader == apiToken) return true
    val userIdHeader = request.getHeader("user-id")
    if (sha256Hex("$userIdHeader$apiTokenHeader") == sha256Hex("$userIdHeader$apiToken")) return true
    response.sendError(SC_FORBIDDEN, "api-token is not valid")
    return false
  }
}
