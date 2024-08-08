package dev.bernasss12.bebooks.api.collections

class BiMap<K, V> private constructor() {

    private val firstToSecond: LinkedHashSet<K> = linkedSetOf()
    private val secondToFirst: LinkedHashSet<V> = linkedSetOf()

    companion object {
        fun <K, V> of(vararg values: Pair<K, V>): BiMap<K, V> {
            return BiMap()
        }
    }

    fun put(first: K, second: V): V? {
        return insert(first, second)?.second
    }

    fun put(second: V, first: K): K? {
        return insert(first, second)?.first
    }

    /**
     * Inserts the value-value relation to the BiMap.
     *
     * @param first a value to be inserted
     * @param second a value to be inserted
     * @return a Pair containing the inserted value-value pair, or null if either value already exists in the BiMap
     */
    private fun insert(first: K, second: V) : Pair<K, V>? {
        val left = secondToFirst.put(second, first) ?: return null
        val right = firstToSecond.put(first, second) ?: return null
        return left to right
    }

    fun putAll(from: Map<K, V>): Unit {
        from.entries.forEach { (first, second) ->
            put(first, second)
        }
    }

    fun putAll(from: Map<V, K>): Unit {
        from.entries.forEach { (first, second) ->
            put(first, second)
        }
    }

    fun remove(first: K): V? {
        firstToSecond.re
    }

    fun remove(second: V): K? {

    }

}