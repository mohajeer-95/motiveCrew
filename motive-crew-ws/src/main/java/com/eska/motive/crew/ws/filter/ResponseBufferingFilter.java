package com.eska.motive.crew.ws.filter;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

/**
 * Forces Spring to buffer every response and set an explicit Content-Length
 * header. React Native's fetch implementation struggles to parse chunked
 * responses with no Content-Length (Transfer-Encoding: chunked), which is why
 * the mobile app was seeing empty bodies while Postman worked.
 *
 * By copying the cached body back onto the original response we ensure the
 * client receives a regular response with a Content-Length header instead of a
 * chunked stream.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@Log4j2
public class ResponseBufferingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

		try {
			filterChain.doFilter(request, cachingResponse);
		} finally {
			try {
				byte[] body = cachingResponse.getContentAsByteArray();
				if (body != null && body.length > 0) {
					response.setContentLength(body.length);
				}
				cachingResponse.copyBodyToResponse();
			} catch (Exception e) {
				log.error("Failed to copy buffered response", e);
				cachingResponse.copyBodyToResponse();
			}
		}
	}
}

