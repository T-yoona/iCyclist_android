package com.example.icyclist_android2

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

/**
 * 拦截并打印 HTTP 请求的详细信息。这对于调试网络请求非常有用，尤其是在开发和测试阶段。
 */
class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 打印请求信息
        println("Sending request to: ${request.url()}") // 使用 request.url() 访问 URL
        println("Request method: ${request.method()}")

        // 打印请求体（如果有）
        request.body()?.let { requestBody ->
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val contentType = requestBody.contentType()
            val requestBodyString = buffer.readString(contentType?.charset() ?: Charsets.UTF_8)
            println("Request body: $requestBodyString")
        }

        return chain.proceed(request)
    }
}
