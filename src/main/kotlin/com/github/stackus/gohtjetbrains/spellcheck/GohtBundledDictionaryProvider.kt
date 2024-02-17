package com.github.stackus.gohtjetbrains.spellcheck

import com.intellij.spellchecker.BundledDictionaryProvider

class GohtBundledDictionaryProvider : BundledDictionaryProvider {
    override fun getBundledDictionaries(): Array<String> {

        return arrayOf(
            "/spellcheck.dic",
        )
    }
}