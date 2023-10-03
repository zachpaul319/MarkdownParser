import java.io.FileNotFoundException

fun main(args: Array<String>) {
    // Ask for file name to be inputted
    print("Please enter the name of the markdown file you'd like to parse: ")
    val filename = readLine()

    filename?.let {
        try {
            val markdownParser = MarkdownParser()

            val lines = markdownParser.parseCode(it)

            val htmlFile = markdownParser.writeFile(it, lines)

            println("HTML file created. Please see '$htmlFile'")

        } catch (e: FileNotFoundException) {
            println("That file does not exist. Rerun and try again")
        }
    }
}