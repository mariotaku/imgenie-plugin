package org.mariotaku.imgenie

import groovy.util.GroovyCollections

fun <T> Iterable<Iterable<T>>.combinations(): List<List<T>> {
    @Suppress("UNCHECKED_CAST")
    return GroovyCollections.combinations(this) as List<List<T>>
}