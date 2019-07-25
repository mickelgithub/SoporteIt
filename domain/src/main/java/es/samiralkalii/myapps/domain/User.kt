package es.samiralkalii.myapps.domain

data class User(val id: String, val name: String,
                val email: String, val password: String,
                val profileImage: String)