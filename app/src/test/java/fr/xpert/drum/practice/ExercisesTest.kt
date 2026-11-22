package fr.xpert.drum.practice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExercisesTest {
    @Test
    fun tousLesExercicesSontValides() {
        // Construire le catalogue exécute toutes les vérifications de pattern().
        val all = Exercises.all
        assertTrue(all.isNotEmpty())
        assertEquals("identifiants uniques", all.size, all.map { it.id }.toSet().size)
        for (ex in all) {
            assertTrue("${ex.id} : motif vide", ex.pattern.hits.isNotEmpty())
            assertTrue("${ex.id} : tempo", ex.startBpm in 30..ex.targetBpm)
            assertEquals("${ex.id} : montée de tempo alignée sur le motif", 0, ex.rampEveryBars % ex.pattern.bars)
            val maxStep = ex.pattern.bars * ex.pattern.stepsPerBar
            assertTrue(ex.pattern.hits.all { it.step in 0 until maxStep })
        }
    }

    @Test
    fun rockDeBase() {
        val p = Exercises.byId("rock-1")!!.pattern
        assertEquals(1, p.bars)
        assertEquals(8, p.stepsPerBar)
        assertEquals(listOf(2, 6), p.hits.filter { it.lane == Lane.SNARE }.map { it.step })
        assertEquals(listOf(0, 4), p.hits.filter { it.lane == Lane.KICK }.map { it.step })
        assertTrue(p.hits.filter { it.lane == Lane.HIHAT }.all { it.sticking == 'D' })
        assertTrue(p.hits.filter { it.lane == Lane.SNARE }.all { it.sticking == 'G' })
    }

    @Test
    fun breakSurDeuxMesures() {
        val p = Exercises.byId("fill-1")!!.pattern
        assertEquals(2, p.bars)
        val fill = p.hits.filter { it.lane == Lane.SNARE && it.step >= 28 }
        assertEquals("DGDG", fill.joinToString("") { it.sticking.toString() })
    }
}
