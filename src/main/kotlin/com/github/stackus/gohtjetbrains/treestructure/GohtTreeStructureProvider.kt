package com.github.stackus.gohtjetbrains.treestructure

import com.github.stackus.gohtjetbrains.GohtFileType
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.NestingTreeNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode

class GohtTreeStructureProvider : TreeStructureProvider {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): MutableCollection<AbstractTreeNode<*>> {
        // Find all the templ nodes
        val templNodes = mutableMapOf<String, AbstractTreeNode<*>>()
        for (child in children) {
            if (child is PsiFileNode) {
                val file = child.virtualFile
                if (file != null && !file.isDirectory && file.fileType is GohtFileType) {
                    templNodes[getGeneratedPath(file.path)] = child
                }
            }
        }

        // Find generated files
        val nodes = ArrayList<AbstractTreeNode<*>>()
        for (child in children) {
            if (child is PsiFileNode) {
                val file = child.virtualFile
                val templFile = templNodes.get(file?.path)
                if (file != null && templFile != null) {
                    nodes.add(
                        NestingTreeNode(templFile as PsiFileNode, arrayListOf(child))
                    )
                    templNodes.remove(file.path)
                    continue
                }
                if (file?.fileType is GohtFileType) {
                    // Skip templ files for now, they may need to be grouped
                    continue
                }
            }
            nodes.add(child)
        }
        for (t in templNodes) {
            nodes.add(t.value)
        }
        return nodes
    }
}