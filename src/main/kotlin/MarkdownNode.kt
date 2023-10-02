class MarkdownNode: Node {
    var type = 0

    constructor(text: String, type: Int) : super(text) {
        this.type = type
    }

    override fun toHTML(): String {
        return when (type) {
            1 -> "<h1>$text</h1>" //Heading 1
            2 -> "<h2>$text</h2>" //Heading 2
            3 -> "<h3>$text</h3>" //Heading 3
            4 -> "<b>$text</b>" //Bold text
            5 -> "<i>$text</i>" //Italic text
            6 -> "<blockquote>$text</blockquote>" //Blockquote text
            7 -> "<hr />" //Horizontal line
            else -> text //Plain text. This is the default
        }
    }
}