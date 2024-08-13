package myapp

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

// Importing functions and classes from the main file
import myapp.add
import myapp.multiply
import myapp.gcd
import myapp.GameStats
import myapp.saveGameStats
import myapp.readGameHistory
import myapp.fetchHelloWorld

class MainKtTest {

    @Test
    fun testAdd() {
        assertEquals(5, add(2, 3))
        assertEquals(0, add(-1, 1))
        assertEquals(-5, add(-2, -3))
    }

    @Test
    fun testMultiply() {
        assertEquals(6, multiply(2, 3))
        assertEquals(0, multiply(0, 5))
        assertEquals(-6, multiply(-2, 3))
    }

    @Test
    fun testGcd() {
        assertEquals(3, gcd(9, 6))
        assertEquals(1, gcd(7, 5))
        assertEquals(5, gcd(10, 5))
    }

    @Test
    fun testSaveAndReadGameHistory() {
        val dataDir = "testData"
        val gameStats = GameStats(listOf(1, 2, 3), 42, listOf(1, 2, 3))
        saveGameStats(gameStats, dataDir)

        val gameHistory = readGameHistory(dataDir)
        assertEquals(1, gameHistory.games.size)
        assertEquals(gameStats, gameHistory.games[0])

        // Clean up
        File("$dataDir/game_stats.json").delete()
        File(dataDir).delete()
    }

    @Test
    fun testFetchHelloWorld() {
        // This test assumes the endpoint is available and returns a valid response
        assertDoesNotThrow {
            fetchHelloWorld()
        }
    }
}
