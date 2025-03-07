package hassony105.dartbarrelfile.misc

data class BarrelFile (
    val dirName: String,
    val barrelFileName: String,
    val selectedFiles: List<String>
) {

    fun generateFileContents() : String {
        var contents = "//GENERATED BARREL FILE \n"
        for(file in selectedFiles) {
            if(file != barrelFileName) {
                contents += "export \'$file\'; \n"
            }
        }
        return contents
    }

    companion object {
        const val BARREL_FILE_HEADER = "//GENERATED BARREL FILE"
    }
}