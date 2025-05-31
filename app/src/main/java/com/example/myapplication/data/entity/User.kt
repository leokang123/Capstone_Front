package com.example.myapplication.data.entity
import com.example.myapplication.data.enums.Roles

/** 수정 완료 **/

data class User(
    val uuid: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val hospitalId: Int? = null,
    val roles: List<Roles>? = null,
    val primaryRole: Roles? = null,
    val token: String? = null,
    val fcmToken: String? = null
)

//data class User(
//    val id: Long = 0,
//    val asdsomeName: String,
//    val password: String,
//    val email: String? = null,
//    val name: String = "",
//    val phoneNumber: String? = null,
//    val hospital: Hospital? = null,
//    val role: Role? = null,
//)