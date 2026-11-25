package com.induztek.app.utils

object UbicacionesUtils {

    val regionesYComunas: Map<String, List<String>> = linkedMapOf(
        "Arica y Parinacota" to listOf(
            "Arica", "Camarones", "General Lagos", "Putre"
        ),
        "Tarapacá" to listOf(
            "Alto Hospicio", "Huara", "Iquique", "Pica", "Pozo Almonte"
        ),
        "Antofagasta" to listOf(
            "Antofagasta", "Calama", "Mejillones", "Taltal", "Tocopilla"
        ),
        "Atacama" to listOf(
            "Caldera", "Chañaral", "Copiapó", "Diego de Almagro", "Vallenar"
        ),
        "Coquimbo" to listOf(
            "Coquimbo", "Illapel", "La Serena", "Los Vilos", "Ovalle"
        ),
        "Valparaíso" to listOf(
            "Quillota", "Quilpué", "San Antonio", "Valparaíso", "Viña del Mar"
        ),
        "Metropolitana de Santiago" to listOf(
            "Cerro Navia", "Maipú", "Pudahuel", "Quilicura", "Santiago"
        ),
        "Libertador General Bernardo O'Higgins" to listOf(
            "Machalí", "Rancagua", "Rengo", "San Fernando", "Santa Cruz"
        ),
        "Maule" to listOf(
            "Cauquenes", "Constitución", "Curicó", "Linares", "Talca"
        ),
        "Ñuble" to listOf(
            "Bulnes", "Chillán", "Chillán Viejo", "Coihueco", "San Carlos"
        ),
        "Biobío" to listOf(
            "Concepción", "Coronel", "Los Ángeles", "San Pedro de la Paz", "Talcahuano"
        ),
        "La Araucanía" to listOf(
            "Angol", "Padre Las Casas", "Pucón", "Temuco", "Villarrica"
        ),
        "Los Ríos" to listOf(
            "La Unión", "Paillaco", "Panguipulli", "Río Bueno", "Valdivia"
        ),
        "Los Lagos" to listOf(
            "Ancud", "Castro", "Osorno", "Puerto Montt", "Puerto Varas"
        ),
        "Aysén del General Carlos Ibáñez del Campo" to listOf(
            "Aysén", "Chile Chico", "Cisnes", "Cochrane", "Coyhaique"
        ),
        "Magallanes y de la Antártica Chilena" to listOf(
            "Cabo de Hornos", "Natales", "Porvenir", "Primavera", "Punta Arenas"
        )
    )

    val listaRegiones: List<String> = regionesYComunas.keys.toList()

    fun obtenerComunas(region: String): List<String> =
        regionesYComunas[region] ?: emptyList()
}