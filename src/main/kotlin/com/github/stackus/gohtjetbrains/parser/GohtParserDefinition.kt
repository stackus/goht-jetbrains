package com.github.stackus.gohtjetbrains.parser

import com.github.stackus.gohtjetbrains.GohtLanguage
import com.github.stackus.gohtjetbrains.lexer.GohtLexer
import com.github.stackus.gohtjetbrains.lexer.GohtTokenTypes
import com.github.stackus.gohtjetbrains.psi.*
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class GohtParserDefinition : ParserDefinition {

    companion object {
        val GOHT_FILE = IFileElementType(GohtLanguage)
    }

    override fun createLexer(project: Project) = GohtLexer()

    override fun createParser(project: Project) = PsiParser { root, builder ->
        val file = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        file.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType() = GOHT_FILE

    override fun getWhitespaceTokens(): TokenSet = TokenSet.EMPTY

    override fun getCommentTokens(): TokenSet = TokenSet.create(GohtTokenTypes.COMMENT)

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement = when (node.elementType) {
        GohtTokenTypes.GO_FRAGMENT    -> GohtGoFragment(node.elementType, node.chars)
        GohtTokenTypes.GO_CODE        -> GohtGoCode(node.elementType, node.chars)
        GohtTokenTypes.GO_EXPR        -> GohtGoExpr(node.elementType, node.chars)
        GohtTokenTypes.INTERP_CONTENT -> GohtInterpContent(node.elementType, node.chars)
        GohtTokenTypes.ATTR_VALUE     -> GohtAttrValue(node.elementType, node.chars)
        GohtTokenTypes.EGO_HTML       -> GohtEgoHtml(node.elementType, node.chars)
        GohtTokenTypes.FILTER_CONTENT -> GohtFilterContent(node.elementType, node.chars)
        GohtTokenTypes.FUNC_PARAMS    -> GohtFuncParams(node.elementType, node.chars)
        else                          -> LeafPsiElement(node.elementType, node.chars)
    }

    override fun createFile(viewProvider: FileViewProvider) = GohtFile(viewProvider)
}
