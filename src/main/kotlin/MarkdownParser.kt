import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

class MarkdownParser: CodeParser {
    val HEADING = Regex("^\\s*(#{1,3})\\s(.*)")
    val BOLD = Regex("^\\s*\\*\\*(.*?)\\*\\*")
    val ITALIC = Regex("^\\s*\\*(.*?)\\*")
    val BLOCKQUOTE = Regex("^\\s*>\\s*(.*)")
    val HORIZONTAL = Regex("^\\s*(-{3,})")

    val REGEX_MAP = LinkedHashMap<String, Regex>() // Using LinkedHashMap, as order of insertion will matter for iteration

    // Make REGEX_MAP iterable
    val REGEX_MAP_ITERATOR = REGEX_MAP.iterator()

    constructor() {
        // Bold must come before italic for iteration and regex matching purposes
        REGEX_MAP.put("Heading", HEADING)
        REGEX_MAP.put("Bold", BOLD)
        REGEX_MAP.put("Italic", ITALIC)
        REGEX_MAP.put("Blockquote", BLOCKQUOTE)
        REGEX_MAP.put("Horizontal", HORIZONTAL)
    }

    override fun parseCode(filename: String): List<Node> {
        val lines = readFile(filename)

        val markdownNodes = mutableListOf<Node>() // Empty list of markdown nodes

        // Iterate through each line of the file
        for (line in lines) {
            var result: MatchResult? = null
            // Iterate through REGEX_MAP until a match is found or there are no more map entries left to check
            while (result == null && REGEX_MAP_ITERATOR.hasNext()) {
                val regex = REGEX_MAP_ITERATOR.next()

                result = regex.value.matchEntire(line)
                result?.let {
                    // Convert line to markdown node and add it to list of markdown nodes (IntelliJ suggested this format)
                    toMarkdownNode(result.groups, regex.key)?.let { mdNode -> markdownNodes.add(mdNode) }
                }
            }
            // If no matches were found, then convert to Plain Text markdown node
            if (result == null) {
                markdownNodes.add(MarkdownNode(line, 0)) // 0 has no associated type in MarkdownNode, and will resolve as Plain Text
            }
        }

        return markdownNodes
    }

    private fun toMarkdownNode(groups: MatchGroupCollection, type: String): MarkdownNode? {
        var text = groups[1]?.value // for all cases except Headings, our only capture will be the text

        when (type) {
            "Heading" -> {
                val hashes = groups[1]?.value // hashes captured, so we can determine which type of heading it is
                text = groups[2]?.value

                // Switch-case to determine type of heading
                return when (hashes) {
                    "#" -> text?.let { MarkdownNode(it, 1) }
                    "##" -> text?.let { MarkdownNode(it, 2) }
                    "###" -> text?.let { MarkdownNode(it, 3) }
                    else -> null
                }
            }
            "Bold" -> {
                return text?.let { MarkdownNode(it, 4) }
            }
            "Italic" -> {
                return text?.let { MarkdownNode(it, 5) }
            }
            "Blockquote" -> {
                return text?.let { MarkdownNode(it, 6) }
            }
            "Horizontal" -> {
                return text?.let { MarkdownNode(it, 7)}
            }
            else -> return null
        }
    }

    fun readFile(filename: String): List<String> {
        val lines = mutableListOf<String>()
        val reader = BufferedReader(FileReader(filename))

        var line:String?
        while (reader.readLine().also {line = it} != null) {
            line?.let {
                lines.add(it)
            }
        }

        return lines
    }

    fun writeFile(filename: String, lines: List<Node>): String {
        val htmlFilename = filename.replace(".md", ".html")
        val writer = BufferedWriter(FileWriter(htmlFilename))

        for (line in lines) {
            writer.write(line.toHTML())
            writer.newLine()
        }

        writer.flush()
        return htmlFilename
    }
}