package com.paligot.conferences.backend.users

import com.paligot.conferences.backend.receiveValidated
import com.paligot.conferences.models.inputs.UserEmailInput
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerUserRoutes(
    userDao: UserDao
) {
    post("users/qrcode") {
        val eventId = call.parameters["eventId"]!!
        val input = call.receiveValidated<UserEmailInput>()
        val upload = userDao.saveQrCode(eventId, input.email)
        call.respond(HttpStatusCode.Created, upload.convertToModel())
    }
}