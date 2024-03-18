package hassony105.dartbarrelfile.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import hassony105.dartbarrelfile.misc.addToExistingBarrelFileFlow
import hassony105.dartbarrelfile.misc.autoAddToHigherBarrelFile
import hassony105.dartbarrelfile.misc.buildBarrelFileWithDialog
import hassony105.dartbarrelfile.misc.createBarrelFile
import hassony105.dartbarrelfile.misc.getAvailableFileNames
import hassony105.dartbarrelfile.misc.getDirName

class NewBarrelFileAction : AnAction() {

    private lateinit var dataContext: DataContext

    override fun update(event: AnActionEvent) {
        //Shows on the New group only if a directory is selected.
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)
        event.presentation.isEnabledAndVisible = psiFile == null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        this.dataContext = e.dataContext

        if(project == null) return

        val availableFiles = getAvailableFileNames(project, this.dataContext)
        val dirName = getDirName(this.dataContext)

        val barrelFile = buildBarrelFileWithDialog(project, dirName, availableFiles) ?: return

        val view = LangDataKeys.IDE_VIEW.getData(this.dataContext)
        val dir = view?.orChooseDirectory
        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().executeCommand(
                project,
                {
                    createBarrelFile(project, dir!!, barrelFile)
                },
                "Generate Barrel File",
                null
            )
        }

        //0.5.0 Functionality - if other barrel files exist up the directory tree, ask
        //the user to go ahead and add this new barrel file to one of those existing
        //higher barrel files.

        //Would you like to add this new file to a higher barrel file?

        val justCreated = dir!!.findFile(barrelFile.barrelFileName)

        val addToHigherBarrelFile = autoAddToHigherBarrelFile(project, justCreated!!)
        if(!addToHigherBarrelFile) return

        addToExistingBarrelFileFlow(project, justCreated)

    }
}