package com.themanufacturers.domain.model

class ImageProject {

    var id: Int = 0
    var width: Int = 0
    var height: Int = 0
    var preview: ByteArray? = null
    var imageUri: String = ""
    var models: MutableList<Sticker> = mutableListOf()
    var rectCut: MutableList<Float>? = null
    var bitmap: ByteArray? = null
    var texts: MutableList<Text> = mutableListOf()

}