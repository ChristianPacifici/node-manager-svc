package com.prewave.nodemanager.services

import com.prewave.nodemanager.dto.NodeTreeDTO
import com.prewave.nodemanager.exception.DuplicateResourceException
import com.prewave.nodemanager.exception.ResourceNotFoundException
import com.prewave.nodemanager.model.tables.records.EdgeRecord
import com.prewave.nodemanager.model.tables.references.EDGE
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.LinkedList
import java.util.Queue


@Service
class NodeManagerService(private val dsl: DSLContext) {

    /**
     * Creates a new edge between two nodes.
     * @param fromId The ID of the source node.
     * @param toId The ID of the target node.
     * @return The created EdgeRecord, or null if creation failed.
     */
    @Transactional
    fun createEdge(fromId: Int, toId: Int): EdgeRecord? {
        val newEdge = dsl.newRecord(EDGE)
        newEdge.fromId = fromId
        newEdge.toId = toId
        val rowsInserted = newEdge.store()
        return if (rowsInserted > 0) newEdge
        else throw DuplicateResourceException("Error inserting Edge")
    }

    /**
     * Finds an edge by its source and target node IDs.
     * @param fromId The ID of the source node.
     * @param toId The ID of the target node.
     * @return The found EdgeRecord, or null if not found.
     */
    fun findEdge(fromId: Int, toId: Int): EdgeRecord? {
        return dsl.selectFrom(EDGE)
            .where(EDGE.FROM_ID.eq(fromId))
            .and(EDGE.TO_ID.eq(toId))
            .fetchOne()
    }

    /**
     * Constructs a tree-like structure of nodes and their children starting from a given root node.
     * The method performs a Breadth-First Search (BFS) to traverse the graph and build the tree.
     *
     * @param rootId The ID of the root node from which to start the tree traversal.
     * @return A {@link com.prewave.nodemanager.controller.dto.NodeTreeDto} representing the root of the tree,
     * including all its descendants.
     * @throws com.prewave.nodemanager.exception.ResourceNotFoundException if the specified root node
     * does not exist in the database.
     */
    fun getTreeByRootNode(rootId: Int): NodeTreeDTO {
        val rootExists = dsl.selectCount()
            .from(EDGE)
            .where(EDGE.FROM_ID.eq(rootId))
            .fetchOne(0, Int::class.java)!! >= 0
        if (!rootExists) {
            throw ResourceNotFoundException("Node $rootId not found.")
        }
        val root = NodeTreeDTO(id = rootId, children = mutableListOf())
        val queue: Queue<NodeTreeDTO> = LinkedList()
        queue.add(root)

        while (queue.isNotEmpty()) {
            val currentNode = queue.poll()
            val childEdges = dsl.select(EDGE.TO_ID)
                .from(EDGE)
                .where(EDGE.FROM_ID.eq(currentNode.id))
                .fetch()
            // populating children
            for (edge in childEdges) {
                val childId: Int? = edge.value1()
                val childNode = NodeTreeDTO(id = childId, children = mutableListOf())
                currentNode.children.add(childNode)
                queue.add(childNode)
            }
        }
        return root
    }

    fun deleteEdge(fromId: Int, toId: Int): Int {
        return dsl.deleteFrom(EDGE)
            .where(EDGE.FROM_ID.eq(fromId))
            .and(EDGE.TO_ID.eq(toId))
            .execute()
    }

}