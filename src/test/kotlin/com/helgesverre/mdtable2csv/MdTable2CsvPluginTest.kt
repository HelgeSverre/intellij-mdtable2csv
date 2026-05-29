package com.helgesverre.mdtable2csv

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil

/**
 * Smoke test confirming the IntelliJ test fixture wires up. Replace with real
 * Markdown-table-to-CSV conversion tests as the feature lands.
 */
class MdTable2CsvPluginTest : BasePlatformTestCase() {

    fun testFixtureLoads() {
        val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
        val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)
        assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))
    }
}
