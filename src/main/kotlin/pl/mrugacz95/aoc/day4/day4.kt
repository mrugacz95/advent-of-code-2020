package pl.mrugacz95.aoc.day4

import pl.mrugacz95.aoc.day2.toInt

class Passport(var passport: String) {
    private val fields = listOf(
        IntField("byr", MinMaxValidator(1920, 2002)),
        IntField("iyr", MinMaxValidator(2010, 2020)),
        IntField("eyr", MinMaxValidator(2020, 2030)),
        StringField("hgt", HeightValidator()),
        StringField("hcl", RegexValidator("#[a-f0-9]{6}".toRegex())),
        StringField("ecl", RegexValidator("amb|blu|brn|gry|grn|hzl|oth".toRegex())),
        StringField("pid", RegexValidator("\\d{9}".toRegex())),
        StringField("cid")
    )

    init {
        passport = passport.replace("\n", " ")
        passport
            .split(" ")
            .map {
                val pair = it.split(":")
                val key = pair[0]
                val newValue = pair[1]
                for (field in fields) {
                    if (field.name == key) {
                        field.setValue(newValue)
                    }
                }
            }
    }

    abstract class Field<T>(open val name: String, private val validator: Validator<T>? = null) {
        var value: T? = null
        fun isValid(): Boolean {
            validator ?: return true
            value?.let {
                if (!validator.validate(it)) {
                    return false
                }
            } ?: return false
            return true
        }

        abstract fun setValue(newValue: String)
    }

    class IntField(override val name: String, validator: Validator<Int>? = null) : Field<Int>(name, validator) {
        override fun setValue(newValue: String) {
            this.value = newValue.toInt()
        }
    }

    class StringField(override val name: String, validator: Validator<String>? = null) :
        Field<String>(name, validator) {
        override fun setValue(newValue: String) {
            this.value = newValue
        }
    }

    abstract class Validator<T> {
        abstract fun validate(value: T): Boolean
    }

    class MinMaxValidator(private val min: Int, private val max: Int) : Validator<Int>() {
        override fun validate(value: Int): Boolean {
            return value in min..max
        }
    }

    class HeightValidator : Validator<String>() {
        private val cmValidator = MinMaxValidator(150, 193)
        private val inValidator = MinMaxValidator(59, 76)
        private val regex = "(?<length>\\d+)(?<unit>(cm|in))".toRegex()
        override fun validate(value: String): Boolean {
            val groups = regex.matchEntire(value)?.groups ?: return false
            val unit = groups["unit"]?.value ?: return false
            val validator = when (unit) {
                "cm" -> cmValidator
                "in" -> inValidator
                else -> return false
            }
            val length = groups["length"]?.value?.toIntOrNull() ?: return false
            return validator.validate(length)
        }
    }

    class RegexValidator(private val regex: Regex) : Validator<String>() {
        override fun validate(value: String): Boolean {
            return value.matches(regex)
        }
    }

    fun hasRequiredFields(): Boolean {
        for (field in fields) {
            if (field.name != "cid" && field.value == null) {
                return false
            }
        }
        return true
    }

    fun isValid(): Boolean {
        return fields.all{ it.isValid() }
    }
}

fun main() {
    val passports = {}::class.java.getResource("/day4.in")
        .readText()
        .split("\n\n")
        .map { Passport(it) }
    println("Part 1 answer: ${passports.map { it.hasRequiredFields().toInt() }.sum()}")
    println("Part 2 answer: ${passports.map { it.isValid().toInt() }.sum()}")
}
