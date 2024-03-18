package hassony105.dartbarrelfile.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import hassony105.dartbarrelfile.dialog.AlreadyInBarrelFileDialog
import hassony105.dartbarrelfile.dialog.NoBarrelFileFoundDialog
import hassony105.dartbarrelfile.dialog.SelectBarrelFileDialog
import hassony105.dartbarrelfile.misc.addDartFileToBarrelFile
import hassony105.dartbarrelfile.misc.findExistingBarrelFiles
import hassony105.dartbarrelfile.misc.isDartFile
import hassony105.dartbarrelfile.misc.isInBarrelFile
import hassony105.dartbarrelfile.misc.isPartFile

class AddToBarrelFileAction : AnAction() {

    private lateinit var dataContext: DataContext

    override fun update(event: AnActionEvent) {

        event.presentation.isEnabledAndVisible = false

        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return
        psiFile.name
        if(!isDartFile(psiFile)) {
            return
        }

        val project = event.project ?: return
        if(isPartFile(project, psiFile)) return

        event.presentation.isEnabledAndVisible = true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        this.dataContext = e.dataContext
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return


        val barrelFiles = mutableListOf<PsiFile>()


        psiFile.name
        val dir = psiFile.containingDirectory

        findExistingBarrelFiles(project, dir, barrelFiles, psiFile)

        //Were any barrel files found?
        if(barrelFiles.isEmpty()) {
            val emptyDialog = NoBarrelFileFoundDialog()
            emptyDialog.show()
            return
        }

        val inExistingBarrelFiles = isInBarrelFile(project, psiFile, barrelFiles)

        //If this file is already in other barrel files, show a dialog
        var doContinue = true
        if(inExistingBarrelFiles.isNotEmpty()) {
            val existingDialog = AlreadyInBarrelFileDialog(psiFile, inExistingBarrelFiles)
            existingDialog.show()
            doContinue = existingDialog.isOK
        }

        //exit if user chose to cancel.
        if(!doContinue) return

        val dialog = SelectBarrelFileDialog(project, barrelFiles)
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

        addDartFileToBarrelFile(project, psiFile, selectedBarrelFile)




    }
}