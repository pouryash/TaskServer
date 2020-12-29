package com.example.demo

import com.example.demo.model.ResponseModel
import org.springframework.http.HttpStatus

import javax.servlet.RequestDispatcher

import javax.servlet.http.HttpServletRequest

import org.springframework.web.bind.annotation.RequestMapping

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller


@Controller
class MyErrorController : ErrorController {
    override fun getErrorPath(): String {
        return "/error"
    }

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): ResponseEntity<ResponseModel> {
        // get error status
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)

        // TODO: log error details here
        if (status != null) {
            val statusCode = status.toString().toInt()

            // display specific error page
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.reasonPhrase), HttpStatus.NOT_FOUND)
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase), HttpStatus.INTERNAL_SERVER_ERROR)
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.reasonPhrase), HttpStatus.FORBIDDEN)
            }else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.reasonPhrase), HttpStatus.BAD_REQUEST)
            }else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.reasonPhrase), HttpStatus.BAD_REQUEST)
            }else if (statusCode == HttpStatus.REQUEST_TIMEOUT.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.REQUEST_TIMEOUT.value(), HttpStatus.REQUEST_TIMEOUT.reasonPhrase), HttpStatus.BAD_REQUEST)
            }else if (statusCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.reasonPhrase), HttpStatus.BAD_REQUEST)
            }else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return ResponseEntity(ResponseModel(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase), HttpStatus.BAD_REQUEST)
            }
        }

        // display generic error
        return ResponseEntity(ResponseModel(5005 , "Unknown Error Occurred"), HttpStatus.OK)
    }
}