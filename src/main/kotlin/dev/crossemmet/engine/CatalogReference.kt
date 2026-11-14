package dev.crossemmet.engine

import java.io.File

fun main(args: Array<String>) {
    val output = File(args.single())
    output.parentFile.mkdirs()
    output.writeText(buildString {
        appendLine("# CrossEmmet abbreviation catalog")
        appendLine("\nGenerated from catalog version ${PropertyCatalog.VERSION}. Repeated abbreviations produce a chooser.\n")
        appendLine("## Property/value shortcuts\n")
        appendLine("Exact keyword shortcuts take precedence over property-only initials. Numeric suffixes still use the property catalog.\n")
        appendLine("| Shortcut | Declaration |")
        appendLine("| --- | --- |")
        KeywordCatalog.shortcuts.forEach { appendLine("| `${it.abbreviation}` | `${it.property}: ${it.value};` |") }
        appendLine("\n## Property initials\n")
        appendLine("| Abbreviation | Property | Numeric category | Default unit |")
        appendLine("| --- | --- | --- | --- |")
        PropertyCatalog.properties.forEach {
            appendLine("| `${it.abbreviation}` | `${it.name}` | ${it.kind.name.lowercase()} | ${it.defaultUnit.ifEmpty { "—" }} |")
        }
    })
}
