import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import java.io.File

fun main() {
    val file = File("./resc/posty.geojson")
    val file2 = File("./resc/zrusenepobocky.json")

    val objectMapper = ObjectMapper()

    try {
        val geoJsonData: JsonNode = objectMapper.readTree(file)
        val jsonData = objectMapper.readTree(file2)

        val geoJsonFeatures = geoJsonData["features"]
        val jsonItems = jsonData["data"]

        if (geoJsonFeatures.isArray && jsonItems.isArray) {
            for (feature in geoJsonFeatures) {
                val properties = (feature as ObjectNode)["properties"]
                val nazev = properties["NAZEV"]?.textValue()

                for (item in jsonItems) {
                    val pobocka = ((item as ObjectNode)["Pobočka"] as? TextNode)?.textValue()

                    if (nazev != null && pobocka != null) {
                        if (nazev == pobocka) {
                            (properties as ObjectNode).put("ZRUSENA",1)
                            break
                        }
                    }
                }
            }
        } else {
            throw IllegalStateException("Invalid JSON structure.")
        }

        // Uložení upraveného geojson souboru
        objectMapper.writeValue(file, geoJsonData)
        println("Úprava proběhla úspěšně.")
    } catch (e: Exception) {
        println("Chyba při zpracování souborů: ${e.message}")
    }
}
