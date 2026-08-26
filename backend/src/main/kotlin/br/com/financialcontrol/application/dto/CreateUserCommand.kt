package br.com.financialcontrol.application.dto

data class CreateUserCommand(
    val name: String,
    val email: String,
)
