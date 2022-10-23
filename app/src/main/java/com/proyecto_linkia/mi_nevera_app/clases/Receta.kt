package com.proyecto_linkia.mi_nevera_app.clases

class Receta{
    var id_receta:Int? = null
    var nombreReceta:String = ""
    var ingredientes:ArrayList<Ingrediente> =ArrayList<Ingrediente>()
    var vegan:Boolean=false

    constructor()
    constructor(id_receta: Int?, nombreReceta: String, ingredientes: ArrayList<Ingrediente>, vegan:Boolean) {
        this.id_receta = id_receta
        this.nombreReceta = nombreReceta
        this.ingredientes = ingredientes
        this.vegan=vegan
    }


    override fun toString(): String{
        return nombreReceta
    }
}