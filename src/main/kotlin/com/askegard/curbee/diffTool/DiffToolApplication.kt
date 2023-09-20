package com.askegard.curbee.diffTool

fun main(args: Array<String>) {
    val diffTool = DiffTool()
    val basic1 = BasicType("test", 1)
    val basic2 = BasicType("test2", 2)
    println("Basic diff " + diffTool.diff(basic1, basic2))

    val nested1 = NestedField(basic1)
    val nested2 = NestedField(basic2)
    println("Simple nested diff" + diffTool.diff(nested1, nested2))

    val complex1 = ComplexType(
        "basicField",
        basic1,
        listOf("a", "b"),
        listOf(basic1)
    )
    val complex2 = ComplexType(
        "fieldBasic",
        basic1,
        listOf("b", "c"),
        listOf(basic2)
    )

    println("complex type diff" + diffTool.diff(complex1, complex2))
}

data class BasicType(
    val id: String,
    val field2: Int,
)

data class NestedField(
    val basicField: BasicType
)

data class ComplexType(
    val basicField: String,
    val nestedField: BasicType,
    val primitiveList: List<String>,
    val complexList: List<BasicType>,
)