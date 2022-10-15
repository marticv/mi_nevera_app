package com.proyecto_linkia.mi_nevera_app.clases

class Ingrediente{
    private var id_ingrediente: Int? = null
    private var nombreIngrediente: String =""

    constructor()
    constructor(id_ingrediente: Int?, nombreIngrediente: String) {
        this.id_ingrediente = id_ingrediente
        this.nombreIngrediente = nombreIngrediente
    }

}