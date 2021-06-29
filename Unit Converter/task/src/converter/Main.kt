package converter

enum class LengthObj(val small: String, val single: String, val several: String, val ofMeter: Double) {
    METER("m", "meter", "meters", 1.0),
    KILOMETER("km", "kilometer", "kilometers", 1000.0),
    CENTIMETER("cm", "centimeter", "centimeters", 0.01),
    MILLIMETER("mm", "millimeter", "millimeters", 0.001),
    MILE("mi", "mile", "miles", 1609.35),
    YARD("yd", "yard", "yards", 0.9144),
    FOOT("ft", "foot", "feet", 0.3048),
    INCH("in", "inch", "inches", 0.0254),
}

enum class WeightObj(val small: String, val single: String, val several: String, val ofGram: Double) {
    GRAM("g", "gram","grams", 1.0),
    KILOGRAM("kg", "kilogram", "kilograms", 1000.0),
    MILLIGRAM("mg", "milligram", "milligrams", 0.001),
    POUND("lb", "pound", "pounds", 453.592),
    OUNCE("oz", "ounce", "ounces", 28.3495),
}

enum class DegreeObj(val small: String, val anySmall: String, val single: String, val anySingle: String, val several: String,
                     val ofCelsius: Double, val ofCelsiusPlus: Double) {
    CELSIUS("c", "dc", "degree Celsius", "celsius","degrees Celsius", 1.0, 0.0),
    FAHRENHEIT("f", "df", "degree Fahrenheit", "fahrenheit","degrees Fahrenheit", 1.8, 32.0),
    KELVINS("k", "", "kelvin", "","kelvins", 1.0, 273.15),
}

fun String.toLengthObj(): LengthObj = LengthObj.values().filter {
    toLowerCase() == it.small || toLowerCase() == it.single || toLowerCase() == it.several
}[0]

fun String.toWeightObj(): WeightObj = WeightObj.values().filter {
    toLowerCase() == it.small || toLowerCase() == it.single || toLowerCase() == it.several
}[0]

fun String.toDegreeObj(): DegreeObj = DegreeObj.values().filter {
    toLowerCase() == it.small || toLowerCase() == it.anySmall ||
            toLowerCase() == it.single.toLowerCase() || toLowerCase() == it.anySingle.toLowerCase() ||
            toLowerCase() == it.several.toLowerCase()
}[0]

fun Double.convert(objFrom: LengthObj, objTo: LengthObj) = this * objFrom.ofMeter / objTo.ofMeter

fun Double.convert(objFrom: WeightObj, objTo: WeightObj) = this * objFrom.ofGram / objTo.ofGram

fun Double.convert(objFrom: DegreeObj, objTo: DegreeObj): Double {
    if (objFrom.small == "c" || objFrom.small == "k")
        return (this - objFrom.ofCelsiusPlus) * objFrom.ofCelsius * objTo.ofCelsius + objTo.ofCelsiusPlus
    else // Fahrenheit
        return (this - objFrom.ofCelsiusPlus) / objFrom.ofCelsius * objTo.ofCelsius + objTo.ofCelsiusPlus
}

fun main() {
    var objFromOK = true
    var objToOK = true
    var typeFrom = 0
    var typeTo = 0
    var errMsgFrom = ""
    var errMsgTo = ""

    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input_ = readLine()!!.split(" ")
        var value = 0.0
        if (input_[0] == "exit") return

        try {
            value = input_[0].toDouble()
        } catch (e: Exception) {
            println("Parse error\n")
            continue
        }
        var input = Array<String>(2){ it -> ""}
        var step = 0
        if (input_[2] == "to" || input_[2] == "in") {
            input[0] = input_[1]
        } else if (input_[3] == "to" || input_[3] == "in") {
            input[0] = "${input_[1]} ${input_[2]}"
            step = 1
        } else {
            println("Conversion from ??? to ??? is impossible\n")
            continue
        }

        input[1] = if (input_.lastIndex-step < 4) input_[3+step] else "${input_[3+step]} ${input_[4+step]}"

        objFromOK = true
        objToOK = true
        typeFrom = 0
        typeTo = 0

        try {
            val objFrom = input[0].toLengthObj()
            typeFrom = 0
            errMsgFrom = objFrom.several
        } catch (e: Exception) {
            try {
                val objFrom = input[0].toWeightObj()
                typeFrom = 1
                errMsgFrom = objFrom.several
            } catch (e: Exception) {
                try {
                    val objFrom = input[0].toDegreeObj()
                    typeFrom = 2
                    errMsgFrom = objFrom.several
                } catch (e: Exception) {
                    objFromOK = false
                    errMsgFrom = "???"
                }
            }
        }
        try {
            val objTo = input[1].toLengthObj()
            typeTo = 0
            errMsgTo = objTo.several
        } catch (e: Exception) {
            try {
                val objTo = input[1].toWeightObj()
                typeTo = 1
                errMsgTo = objTo.several
            } catch (e: Exception) {
                try {
                    val objTo = input[1].toDegreeObj()
                    typeTo = 2
                    errMsgTo = objTo.several
                } catch (e: Exception) {
                    objToOK = false
                    errMsgTo = "???"
                }
            }
        }

        if ((!objFromOK || !objToOK) || (typeFrom != typeTo)) {
            println("Conversion from $errMsgFrom to $errMsgTo is impossible")
            continue
        } else {
            if (typeFrom == 0) {
                val objFrom = input[0].toLengthObj()
                val objTo = input[1].toLengthObj()
                if (value < 0) {
                    println("Length shouldn't be negative.\n")
                    continue
                }
                val convertValue: Double = value.convert(objFrom, objTo)
                println(
                    "$value ${if (value == 1.0) objFrom.single else objFrom.several} " +
                            "is $convertValue ${if (convertValue == 1.0) objTo.single else objTo.several}"
                )
            } else if (typeFrom == 1) {
                val objFrom = input[0].toWeightObj()
                val objTo = input[1].toWeightObj()
                if (value < 0) {
                    println("Weight shouldn't be negative.\n")
                    continue
                }
                val convertValue: Double = value.convert(objFrom, objTo)
                println(
                    "$value ${if (value == 1.0) objFrom.single else objFrom.several} " +
                            "is $convertValue ${if (convertValue == 1.0) objTo.single else objTo.several}"
                )
            } else if (typeFrom == 2) {
                val objFrom = input[0].toDegreeObj()
                val objTo = input[1].toDegreeObj()
                val convertValue: Double = value.convert(objFrom, objTo)
                println(
                    "$value ${if (value == 1.0) objFrom.single else objFrom.several} " +
                            "is $convertValue ${if (convertValue == 1.0) objTo.single else objTo.several}"
                )
            }
        }
        println("")
    }
}