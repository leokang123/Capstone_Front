package com.example.myapplication.data.user
import com.example.myapplication.data.enums.Roles

/** 수정 완료 **/

data class User(
    val uuid: String = "",
    val userName: String,
    val password: String,
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val hospitalId: Int? = null,
    val roles: List<Roles>? = null,
    val primaryRoles: Roles? = null,
    val token: String = "",
    val fcmToken: String = ""
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