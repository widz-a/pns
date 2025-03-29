package wida.pns

object IsToggled {
    var punch = false
    var nod = false
    var shift = false

    fun enables(): Array<Boolean> { return arrayOf(punch, nod, shift) }
}