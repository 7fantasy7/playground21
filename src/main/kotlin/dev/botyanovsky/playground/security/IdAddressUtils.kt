package dev.botyanovsky.playground.security

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object IdAddressUtils {
    private val IP_HEADER_CANDIDATES = arrayOf(
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    )

    fun getClientIpAddress(): String {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return "0.0.0.0"
        }
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request
        for (header in IP_HEADER_CANDIDATES) {
            val ipList = request.getHeader(header)
            if (ipList != null && ipList.isNotEmpty() && !"unknown".equals(ipList, ignoreCase = true)) {
                return ipList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray<String>()[0]
            }
        }
        return request.remoteAddr
    }
}