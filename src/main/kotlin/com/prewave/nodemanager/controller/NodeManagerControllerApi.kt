package com.prewave.nodemanager.controller

import com.prewave.nodemanager.dto.EdgeDTO
import com.prewave.nodemanager.dto.EdgeResponse
import com.prewave.nodemanager.dto.NodeTreeDTO
import com.prewave.nodemanager.exception.DuplicateResourceException
import com.prewave.nodemanager.services.NodeManagerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/node-manager")
class NodeManagerControllerApi(private val nodeManagerService: NodeManagerService) {

    @PostMapping("/edges")
    fun createEdge(
        @RequestHeader("x-request-id") requestId: String,
        @RequestHeader("x-correlation-id") correlationId: String,
        @RequestBody request: EdgeDTO
    ): ResponseEntity<EdgeResponse> {
        val existingEdge = nodeManagerService.findEdge(request.fromId, request.toId)
        if (existingEdge != null) {
            throw DuplicateResourceException("Edge from ${request.fromId} to ${request.toId} already exists.")
        }
        val newEdge = nodeManagerService.createEdge(request.fromId, request.toId)
        val response = EdgeResponse(
            fromId = newEdge?.fromId,
            toId = newEdge?.toId
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/edges/{fromId}/{toId}")
    fun removeEdge(
        @RequestHeader("x-request-id") requestId: String,
        @RequestHeader("x-correlation-id") correlationId: String,
        @PathVariable fromId: Int,
        @PathVariable toId: Int
    ): ResponseEntity<Void> {
        nodeManagerService.deleteEdge(fromId, toId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/edges/tree/{nodeId}")
    fun getEdgeTree(
        @RequestHeader("x-request-id") requestId: String,
        @RequestHeader("x-correlation-id") correlationId: String,
        @PathVariable nodeId: Int
    ): NodeTreeDTO {
        return nodeManagerService.getTreeByRootNode(nodeId);
    }

}