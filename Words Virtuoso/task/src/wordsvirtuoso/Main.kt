package wordsvirtuoso

import java.io.File
import kotlin.math.pow
import kotlin.system.exitProcess

var invalidWords = 0
var invalidCandidates = 0
var notIncluded = 0
var turns = 0
var wrongLetters = ""
val clues = mutableListOf<String>()

fun main(args: Array<String>) {
    val secretWord: String
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        exitProcess(0)
    }
    val words = File("./${ args[0] }")
    if (!words.exists()) {
        println("Error: The words file ${ args[0] } doesn't exist.")
        exitProcess(0)
    }
    val wordsArray = words.readLines().map { it.lowercase() }.toTypedArray()
    val candidates = File("./${ args[1] }")
    if (!candidates.exists()) {
        println("Error: The candidate words file ${ args[1] } doesn't exist.")
        exitProcess(0)
    }
    val candidatesArray = candidates.readLines().map { it.lowercase() }.toTypedArray()
    isValid(wordsArray, candidatesArray)
    for (candidate in candidatesArray) {
        if (!wordsArray.contains(candidate)) notIncluded++
    }
    when {
        invalidWords != 0 -> {
            println("Error: $invalidWords invalid words were found in the ${args[0]} file.")
            exitProcess(0)
        }
        invalidCandidates != 0 -> {
            println("Error: $invalidCandidates invalid words were found in the ${ args[1] } file.")
            exitProcess(0)
        }
        notIncluded != 0 -> {
            println("Error: $notIncluded candidate words are not included in the ${ args[0] } file.")
            exitProcess(0)
        }
        else -> { println("Words Virtuoso\n") ; secretWord = candidatesArray.random()
            game(secretWord, candidatesArray, wordsArray)
        }
    }
}

fun isValid(wordsArray: Array<String>, candidatesArray: Array<String>) {
    val regex = Regex("[a-zA-Z]+")
    for (word in wordsArray) {
        if (word.length != 5 || !regex.matches(word) || word.toList().distinctBy { it.uppercaseChar() }.size != 5)
            invalidWords++
    }
    for (word in candidatesArray) {
        if (word.length != 5 || !regex.matches(word) || word.toList().distinctBy { it.uppercaseChar() }.size != 5)
            invalidCandidates++
    }
}

fun game(secretWord: String, candidatesArray: Array<String>, wordsArray: Array<String>) {
    println("Input a 5-letter word:")
    val start = System.nanoTime()
    val regex = Regex("[a-zA-Z]+")
    var clue = ""
    val input = readln().lowercase()
    when {
        input == "exit" -> { println("The game is over."); exitProcess(0) }
        input == secretWord && turns == 0 -> {
            var coloredWord = ""
            for (i in 0..secretWord.lastIndex) coloredWord += "\u001B[48:5:10m${secretWord[i]}\u001B[0m"
            println("\n${coloredWord.uppercase()}")
            println("\nCorrect!\nAmazing luck! The solution was found at once.")
            exitProcess(0) }
        input == secretWord && turns != 0 -> {
            val end = System.nanoTime()
            val duration = ((end - start) / 10.0.pow(9)).toInt()
            turns++
            for (i in 0..clues.lastIndex) println(clues[i])
            var coloredWord = ""
            for (i in 0..secretWord.lastIndex) coloredWord += "\u001B[48:5:10m${secretWord[i]}\u001B[0m"
            println("${coloredWord.uppercase()}")
            println("\nCorrect!\nThe solution was found after $turns tries in $duration seconds.")
            exitProcess(0) }
        else -> {
            if (input.length != 5) {
                println("The input isn't a 5-letter word.")
                game(secretWord, candidatesArray, wordsArray)
            } else if (!regex.matches(input)) {
                println("One or more letters of the input aren't valid.")
                game(secretWord, candidatesArray, wordsArray)
            } else if (input.toList().distinctBy { it }.size != 5) {
                println("The input has duplicate letters.")
                game(secretWord, candidatesArray, wordsArray)
            } else if (!wordsArray.contains(input)) {
                println("The input word isn't included in my words list.")
                game(secretWord, candidatesArray, wordsArray)
            } else {
                for (i in 0..4) {
                    when {
                        secretWord[i] == input[i] -> { clue += "\u001B[48:5:10m${input[i]}\u001B[0m"; continue }
                        secretWord.contains(input[i]) -> { clue += "\u001B[48:5:11m${input[i]}\u001B[0m"; continue }
                        else -> {
                            clue += "\u001B[48:5:7m${input[i]}\u001B[0m"
                            if (!wrongLetters.contains(input[i])) wrongLetters += input[i]
                        }
                    }
                }
                clues.add(clue.uppercase())
                turns++
                for (i in 0..clues.lastIndex) println(clues[i])
                println("\u001B[48:5:14m${ wrongLetters.toCharArray().apply { sort() }.joinToString("").uppercase() }\u001B[0m\n")
                game(secretWord, candidatesArray, wordsArray)
            }
        }
    }
}