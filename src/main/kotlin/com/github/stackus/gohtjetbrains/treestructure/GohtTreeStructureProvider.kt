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
        val gohtNodes = mutableMapOf<String, AbstractTreeNode<*>>()
        for (child in children) {
            if (child is PsiFileNode) {
                val file = child.virtualFile
                if (file != null && !file.isDirectory && file.fileType is GohtFileType) {
                    gohtNodes[getGeneratedPath(file.path)] = child
                }
            }
        }

        val nodes = ArrayList<AbstractTreeNode<*>>()
        for (child in children) {
            if (child is PsiFileNode) {
                val file = child.virtualFile
                val gohtFile = gohtNodes[file?.path]
                if (file != null && gohtFile != null) {
                    nodes.add(
                        NestingTreeNode(gohtFile as PsiFileNode, arrayListOf(child))
                    )
                    gohtNodes.remove(file.path)
                    continue
                }
                if (file?.fileType is GohtFileType) {
                    continue
                }
            }
            nodes.add(child)
        }
        for (entry in gohtNodes) {
            nodes.add(entry.value)
        }
        return nodes
    }
}