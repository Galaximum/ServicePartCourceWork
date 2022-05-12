package ru.hse.project.backend.constant

enum class GoogleAttributes(val att: String) {
    ID("sub"), // Id in google
    EMAIL("email"),
    FIRST_NAME("given_name"), // Name
    SECOND_NAME("family_name"), // Surname
    PICTURE("picture") // Profile picture
}