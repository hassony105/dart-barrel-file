package hassony105.dartbarrelfile.dialog

import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.psi.PsiFile
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class SelectBarrelFileDialog (project: Project, private val barrelFiles: List<PsiFile>) : DialogWrapper(project) {

    private val barrelFilesCollection: CollectionListModel<PsiFile> = CollectionListModel(
        barrelFiles.map { it }
    )
    private val barrelFilesNamesList: JBList<PsiFile> = JBList(barrelFilesCollection).apply{
        cellRenderer = DefaultPsiElementCellRenderer()
        setSelectionMode(0)
    }
    private val filesComponent: LabeledComponent<JPanel>

    init {


        val decorator = ToolbarDecorator.createDecorator(barrelFilesNamesList)
            .disableAddAction()
        val panel = decorator.createPanel()

        filesComponent = LabeledComponent.create(panel, "Choose " +"A "+"Barrel "+"File")

        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.add(filesComponent, BorderLayout.CENTER)
        return panel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return barrelFilesNamesList
    }



    private fun extractSelectedIndices(): Set<Int> {
        return this.barrelFilesNamesList.selectedIndices.toSet()
    }

    fun getSelectedFiles(): List<PsiFile> {
        val selectedIndexes = extractSelectedIndices()
        return this.barrelFiles.filterIndexed { index, _ -> index in selectedIndexes}
    }


}

//class BarrelSelectListener () : ListSelectionListener {
//    override fun valueChanged(p0: ListSelectionEvent?) {
//        println("FIRED!! first = ${p0?.firstIndex}")
//        println("FIRED!! last = ${p0?.lastIndex}")
//    }
//
//}