package fr.xpert.drum.content

import org.junit.Assert.assertTrue
import org.junit.Test

class LessonsTest {
    @Test
    fun leconsValidesEtIllustrees() {
        // Construire les leçons vérifie tous les rythmes d'exemple.
        assertTrue(Lessons.all.size >= 7)
        for (l in Lessons.all) {
            val advices = l.blocks.filterIsInstance<Block.Advice>()
            assertTrue("${l.id} : au moins une illustration", advices.isNotEmpty())
        }
        val kit = Lessons.byId("kit")!!.blocks.filterIsInstance<Block.Advice>()
        assertTrue("chaque élément a son son", kit.drop(1).all { it.sound != null })
        val rythme = Lessons.byId("rythme")!!.blocks.filterIsInstance<Block.Advice>()
        assertTrue("chaque notion de lecture s'écoute", rythme.all { it.rhythm != null })
    }
}
