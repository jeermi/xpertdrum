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

    @Test
    fun lectureAppliqueeAccents() {
        val p = Exercises.byId("app-soupirs")!!.pattern
        val hands = p.hits.filter { it.lane == Lane.SNARE }
        assertEquals("croches continues sur deux mesures", 16, hands.size)
        assertEquals(listOf(0, 2, 6, 10, 12, 14), hands.filter { it.accent }.map { it.step })
        assertTrue(p.gainOf(hands[0]) > p.gainOf(hands[1]))
        assertEquals(8, p.hits.count { it.lane == Lane.KICK })
    }

    @Test
    fun trioletsEtSixHuit() {
        val t = Exercises.byId("triolets")!!.pattern
        assertEquals(12, t.stepsPerBar)
        assertEquals("DGDGDGDGDGDG", t.hits.filter { it.lane == Lane.SNARE }.joinToString("") { it.sticking.toString() })
        val six = Exercises.byId("six-huit")!!.pattern
        assertEquals(2, six.beatsPerBar)
        assertEquals(3, six.stepsPerBeat)
    }

    @Test
    fun silencesGardentLeDoigteAlterne() {
        val p = Exercises.byId("dc-galop")!!.pattern
        assertEquals("DDGDDGDDGDDG", p.hits.filter { it.lane == Lane.SNARE }.joinToString("") { it.sticking.toString() })
    }

    @Test
    fun sansAccentVolumeNormal() {
        val p = Exercises.byId("rock-1")!!.pattern
        assertTrue(p.hits.all { p.gainOf(it) == 0.9f })
    }
}
