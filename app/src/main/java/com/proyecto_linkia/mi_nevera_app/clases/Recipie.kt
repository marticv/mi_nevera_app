package com.proyecto_linkia.mi_nevera_app.clases

data class Recipie(
    val id_recipie: Int?,
    val recipieName: String,
    val ingredients: ArrayList<String>,
    val isVegan: Boolean
):java.io.Serializable