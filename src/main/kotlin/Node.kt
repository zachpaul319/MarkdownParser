open class Node {
    var text = ""

    constructor(text: String) {
        this.text = text
    }

    open fun toHTML(): String {
        return text
    }
}