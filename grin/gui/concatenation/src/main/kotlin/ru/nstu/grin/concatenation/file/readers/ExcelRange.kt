package ru.nstu.grin.concatenation.file.readers

data class ExcelRange(
    val startCell: Int,
    val endCell: Int,
    val startRow: Int,
    val endRow: Int
) {
    constructor(range: String) : this(
        parseStartCell(range),
        parseEndCell(range),
        parseStartRow(range),
        parseEndRow(range)
    )

    private companion object {
        fun parseStartCell(range: String): Int {
            val letters = range.substringBefore(":").filter {
                it.isLetter()
            }
            return if (letters.length > 1) {
                val start = letters.dropLast(1)
                val startSum = start.map {
                    (parseLetter(it) + 1) * 26
                }.sum()
                startSum + parseLetter(letters.last())
            } else {
                letters.map { parseLetter(it) }.sum()
            }
        }

        fun parseEndCell(range: String): Int {
            val letters = range.substringAfter(":").filter {
                it.isLetter()
            }

            return if (letters.length > 1) {
                val start = letters.dropLast(1)
                val startSum = start.map {
                    (parseLetter(it) + 1) * 26
                }.sum()
                startSum + parseLetter(letters.last())
            } else {
                letters.map { parseLetter(it) }.sum()
            }
        }

        fun parseStartRow(range: String): Int {
            return range.substringBefore(":").filter {
                it.isDigit()
            }.toInt() - 1
        }

        fun parseEndRow(range: String): Int {
            return range.substringAfter(":").filter {
                it.isDigit()
            }.toInt() - 1
        }

        fun parseLetter(letter: Char): Int {
            return when (letter) {
                'A' -> 0
                'B' -> 1
                'C' -> 2
                'D' -> 3
                'E' -> 4
                'F' -> 5
                'G' -> 6
                'H' -> 7
                'I' -> 8
                'J' -> 9
                'K' -> 10
                'L' -> 11
                'M' -> 12
                'N' -> 13
                'O' -> 14
                'P' -> 15
                'Q' -> 16
                'R' -> 17
                'S' -> 18
                'T' -> 19
                'U' -> 20
                'V' -> 21
                'W' -> 22
                'X' -> 23
                'Y' -> 24
                'Z' -> 25
                else -> {
                    throw IllegalArgumentException("Can't find such letter $letter in excel")
                }
            }
        }
    }
}