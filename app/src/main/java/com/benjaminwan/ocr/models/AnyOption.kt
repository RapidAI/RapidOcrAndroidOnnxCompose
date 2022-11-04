package com.benjaminwan.ocr.models

//因为ItemOption需要检索first或second，map检索second没有优势，故选择list。
interface AnyOption<T> {
    val first: Int
    val second: T
}

inline fun <reified T> List<AnyOption<T>>.firstList(): List<Int> = this.map { it.first }

inline fun <reified T> List<AnyOption<T>>.secondList(): List<T> = this.map { it.second }

inline fun <reified T> List<AnyOption<T>>.firstArray(): Array<Int> = this.firstList().toTypedArray()

inline fun <reified T> List<AnyOption<T>>.secondArray(): Array<T> = this.secondList().toTypedArray()

inline fun <reified T> findFirst(options: List<AnyOption<T>>, second: T, defFirst: Int = 0): Int {
    val foundItem = options.find { it.second == second }
    return foundItem?.first ?: defFirst
}

inline fun <reified T> findItemIndex(options: List<AnyOption<T>>, second: T): Int {
    return options.indexOfFirst { it.second == second }
}

inline fun <reified T> findSecond(options: List<AnyOption<T>>, first: Int): T? {
    val foundItem = options.find { it.first == first }
    return foundItem?.second
}

