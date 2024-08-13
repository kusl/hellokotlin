import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.harawata.appdirs.AppDirsFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

data class Config(val analyticsConsent: Boolean)
data class GameStats(val attempts: List<Int>, val secretNumber: Int, val guesses: List<Int>)
data class GameHistory(val games: MutableList<GameStats>)

fun main(args: Array<String>) {
    val appDirs = AppDirsFactory.getInstance()
    val configDir = appDirs.getUserConfigDir("KusGuessingGame", "0.1.1", "Kushal Hada")
    val dataDir = appDirs.getUserDataDir("KusGuessingGame", "0.1.1", "Kushal Hada")

    val configPath = "$configDir/config.json"
    val config = if (File(configPath).exists()) {
        Gson().fromJson(File(configPath).readText(), Config::class.java)
    } else {
        Config(false)
    }

    if (args.isNotEmpty() && args[0] == "--update-consent") {
        println("Do you consent to analytics? (yes/no)")
        val consent = readLine() ?: "no"
        val updatedConfig = config.copy(analyticsConsent = consent.lowercase() in listOf("yes", "y"))
        File(configDir).mkdirs()
        File(configPath).writeText(Gson().toJson(updatedConfig))
        println("Consent updated successfully.")
        return
    }

    playGuessingGame(config.analyticsConsent, dataDir)
}

fun playGuessingGame(analyticsConsent: Boolean, dataDir: String) {
    println("Guess the number!")
    println("Remember, you can update your consent by running this application with the --update-consent flag.")

    val secretNumber = (1..100).random()
    val attempts = mutableListOf<Int>()
    val guesses = mutableListOf<Int>()

    while (true) {
        println("Please input your guess.")
        val guess = readLine()?.toIntOrNull() ?: continue

        attempts.add(guess)
        guesses.add(guess)

        println("You guessed: $guess")

        when {
            guess < secretNumber -> println("Too small!")
            guess > secretNumber -> println("Too big!")
            else -> {
                println("You win!")
                if (analyticsConsent) {
                    fetchHelloWorld()
                }
                println("Game Statistics:")
                println("Attempts: $attempts")
                println("Secret Number: $secretNumber")
                println("Guesses: $guesses")

                val gameStats = GameStats(attempts, secretNumber, guesses)
                saveGameStats(gameStats, dataDir)

                val gameHistory = readGameHistory(dataDir)
                println("All Games History:")
                gameHistory.games.forEachIndexed { index, game ->
                    println("Game ${index + 1}: Attempts: ${game.attempts}, Secret Number: ${game.secretNumber}, Guesses: ${game.guesses}")
                }

                println("Press Enter to exit...")
                readLine()
                break
            }
        }

        val sum = add(guess, secretNumber)
        println("The sum of your guess and the secret number is: $sum")
        val product = multiply(guess, secretNumber)
        println("The product of your guess and the secret number is: $product")

        val gcd = gcd(guess, secretNumber)
        println("The greatest common divisor of your guess and the secret number is: $gcd")
    }
}

fun saveGameStats(gameStats: GameStats, dataDir: String) {
    val statsPath = "$dataDir/game_stats.json"
    val gameHistory = if (File(statsPath).exists()) {
        Gson().fromJson(File(statsPath).readText(), object : TypeToken<GameHistory>() {}.type)
    } else {
        GameHistory(mutableListOf())
    }
    gameHistory.games.add(gameStats)
    File(dataDir).mkdirs()
    File(statsPath).writeText(Gson().toJson(gameHistory))
}

fun readGameHistory(dataDir: String): GameHistory {
    val statsPath = "$dataDir/game_stats.json"
    return if (File(statsPath).exists()) {
        Gson().fromJson(File(statsPath).readText(), object : TypeToken<GameHistory>() {}.type)
    } else {
        GameHistory(mutableListOf())
    }
}

fun fetchHelloWorld() {
    val client = OkHttpClient()
    val request = Request.Builder().url("https://nice.runasp.net/Analytics/HelloWorld").build()
    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println("Response from server: ${response.body?.string()}")
        }
    } catch (e: IOException) {
        println("Failed to fetch response: ${e.message}")
    }
}

fun add(a: Int, b: Int): Int = a + b
fun multiply(a: Int, b: Int): Int = a * b
fun gcd(n: Int, m: Int): Int {
    var a = n
    var b = m
    while (b != 0) {
        val t = b
        b = a % b
        a = t
    }
    return a
}
