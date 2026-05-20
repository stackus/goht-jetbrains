package com.github.stackus.gohtjetbrains.psi

import com.github.stackus.gohtjetbrains.lexer.GohtTokenTypes
import com.intellij.lang.ASTFactory
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.tree.IElementType

class GohtAstFactory : ASTFactory() {
    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement? {
        return when (type) {
            GohtTokenTypes.GO_FRAGMENT    -> GohtGoFragment(type, text)
            GohtTokenTypes.GO_CODE        -> GohtGoCode(type, text)
            GohtTokenTypes.GO_EXPR        -> GohtGoExpr(type, text)
            GohtTokenTypes.INTERP_CONTENT -> GohtInterpContent(type, text)
            GohtTokenTypes.ATTR_VALUE     -> GohtAttrValue(type, text)
            GohtTokenTypes.EGO_HTML       -> GohtEgoHtml(type, text)
            GohtTokenTypes.FILTER_CONTENT -> GohtFilterContent(type, text)
            GohtTokenTypes.FUNC_PARAMS    -> GohtFuncParams(type, text)
            else                          -> super.createLeaf(type, text)
        }
    }
}
