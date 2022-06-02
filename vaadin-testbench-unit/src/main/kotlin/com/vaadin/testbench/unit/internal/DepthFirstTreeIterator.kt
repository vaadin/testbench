/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit.internal

import java.util.Deque
import java.util.LinkedList
import kotlin.streams.toList
import com.vaadin.flow.component.Component

/**
 * Walks the child tree, depth-first: first the node, then its descendants,
 * then its next sibling.
 * @param root start here.
 * @param children fetches children of given node.
 */
class DepthFirstTreeIterator<out T>(root: T, private val children: (T) -> List<T>) : Iterator<T> {
    private val queue: Deque<T> = LinkedList(listOf(root))
    override fun hasNext(): Boolean = !queue.isEmpty()
    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        val result: T = queue.pop()
        children(result).asReversed().forEach { queue.push(it) }
        return result
    }
}

/**
 * Walks the component child tree, depth-first: first the component, then its descendants,
 * then its next sibling.
 */
fun Component.walk(): Iterable<Component> = Iterable {
    DepthFirstTreeIterator(this) { component: Component -> component.children.toList() }
}
