package com.example.coronaview.utils

object ConstantesRegiao{
    val SIGLAS_ESTADO = mapOf<String,String>(
        "AC" to "Acre",
        "AL" to "Alagoas" ,
        "AP" to "Amapá" ,
        "AM" to "Amazonas",
        "BA" to "Bahia",
        "CE" to "Ceará",
        "DF" to "Distrito Federal"  ,
        "ES" to "Espírito Santo" ,
        "GO" to "Goiás",
        "MA" to "Maranhão" ,
        "MT" to "Mato Grosso" ,
        "MS" to "Mato Grosso do Sul",
        "MG" to "Minas Gerais" ,
        "PA" to "Pará" ,
        "PB" to "Paraíba",
        "PR" to "Paraná",
        "PE" to "Pernambuco",
        "PI" to "Piauí",
        "RJ" to "Rio de Janeiro",
        "RN" to "Rio Grande do Norte" ,
        "RS" to "Rio Grande Sul" ,
        "RO" to "Rondônia",
        "RR" to "Roraima" ,
        "SC" to "Santa Catarina",
        "SP" to "São Paulo" ,
        "SE" to "Sergipe" ,
        "TO" to "Tocantins"
    )

    val regiao_sudeste = arrayOf("Rio de Janeiro","São Paulo","Espírito Santo","Minas Gerais")
    val regiao_nordeste = arrayOf("Maranhão","Piauí","Rio Grande do Norte","Ceará","Paraíba","Bahia","Pernambuco","Alagoas","Sergipe")
    val regiao_norte = arrayOf("Amazonas","Acre","Rondônia","Roraima","Amapá","Pará","Tocantins")
    val regiao_sul = arrayOf("Rio Grande Sul","Santa Catarina","Paraná")
    val regiao_centroOeste = arrayOf("Goiás","Mato Grosso","Mato Grosso do Sul","Distrito Federal")

    val REGIAO = mapOf<String,Array<String>>(
        "Sudeste"       to regiao_sudeste,
        "Nordeste"      to regiao_nordeste,
        "Norte"         to regiao_norte,
        "Sul"           to regiao_sul,
        "Centro-Oeste"  to regiao_centroOeste
    )
}