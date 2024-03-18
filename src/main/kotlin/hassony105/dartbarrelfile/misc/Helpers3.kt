package hassony105.dartbarrelfile.misc

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import hassony105.dartbarrelfile.dialog.AlreadyInBarrelFileDialog
import hassony105.dartbarrelfile.dialog.NoBarrelFileFoundDialog
import hassony105.dartbarrelfile.dialog.SelectBarrelFileDialog

fun autoAddToHigherBarrelFile(project: Project, bFile: PsiFile): Boolean {

    val dir = bFile.containingDirectory
    val existingBarrelFiles = mutableListOf<PsiFile>()

    findExistingBarrelFiles(project, dir, existingBarrelFiles, bFile)

    if(existingBarrelFiles.isEmpty()) return false

    val addToHigherBarrelFile = Messages.showYesNoDialog(
        "Add this new barrel file to a higher barrel file?",
        arrayOf("Add","to","higher","barrel","file?").joinToString(" "),
        Messages.getQuestionIcon()
    )

    //The NO response returns value of 1!!!
    return addToHigherBarrelFile == 0

}

fun addToExistingBarrelFileFlow(project: Project, bFile: PsiFile) {

    val dir = bFile.containingDirectory
    val existingBarrelFiles = mutableListOf<PsiFile>()

    findExistingBarrelFiles(project, dir, existingBarrelFiles, bFile)

    if(existingBarrelFiles.isEmpty()) {
        val emptyDialog = NoBarrelFileFoundDialog()
        emptyDialog.show()
        return
    }

    val inExistingBarrelFiles = isInBarrelFile(project, bFile, existingBarrelFiles)

    //If this file is already in other barrel files, show a dialog
    var doContinue = true
    if(inExistingBarrelFiles.isNotEmpty()) {
        val existingDialog = AlreadyInBarrelFileDialog(bFile, inExistingBarrelFiles)
        existingDialog.show()
        doContinue = existingDialog.isOK
    }

    //exit if user chose to cancel.
    if(!doContinue) return

    val dialog = SelectBarrelFileDialog(project, existingBarrelFiles)
    dialog.show()

    //did the user hit cancel on the file picker dialog?
    if(!dialog.isOK) return

    val selectedBarrelFiles = dialog.getSelectedFiles()
    if(selectedBarrelFiles.isEmpty()) return

    val selectedBarrelFile = selectedBarrelFiles[0]

    //if this dart file is already in the selected barrel file, do nothing
    if(inExistingBarrelFiles.contains(selectedBarrelFile)) {
        return
    }

    PsiDocumentManager.getInstance(project).getDocument(selectedBarrelFile)

    addDartFileToBarrelFile(project, bFile, selectedBarrelFile)
}