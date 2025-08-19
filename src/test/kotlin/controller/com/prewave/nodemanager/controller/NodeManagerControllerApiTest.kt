package com.prewave.nodemanager.controller

import com.prewave.nodemanager.dto.EdgeDTO
import com.prewave.nodemanager.dto.EdgeResponse
import com.prewave.nodemanager.dto.NodeTreeDTO
import com.prewave.nodemanager.exception.DuplicateResourceException
import com.prewave.nodemanager.model.tables.records.EdgeRecord
import com.prewave.nodemanager.services.NodeManagerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class NodeManagerControllerApiTest {

    private lateinit var nodeManagerService: NodeManagerService
    private lateinit var nodeManagerControllerApi: NodeManagerControllerApi

    @BeforeEach
    fun setUp() {
        // Initialize the mocks before each test.
        nodeManagerService = mock()
        nodeManagerControllerApi = NodeManagerControllerApi(nodeManagerService)
    }

    @Test
    fun `createEdge 201 on success`() {
        // Arrange
        val requestId = "test-request-id"
        val correlationId = "test-correlation-id"
        val fromId = 1
        val toId = 2
        val request = EdgeDTO(fromId = fromId, toId = toId)
        val edgeRecordMock = mock<EdgeRecord>()

        whenever(nodeManagerService.findEdge(fromId, toId)).thenReturn(null)
        whenever(nodeManagerService.createEdge(fromId, toId)).thenReturn(edgeRecordMock)
        whenever(edgeRecordMock.fromId).thenReturn(fromId)
        whenever(edgeRecordMock.toId).thenReturn(toId)

        // Act
        val response: ResponseEntity<EdgeResponse> = nodeManagerControllerApi.createEdge(requestId, correlationId, request)

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)
        Assertions.assertNotNull(response.body)
        Assertions.assertEquals(fromId, response.body?.fromId)
        Assertions.assertEquals(toId, response.body?.toId)

        // Verify that the service methods were called as expected.
        verify(nodeManagerService, times(1)).findEdge(fromId, toId)
        verify(nodeManagerService, times(1)).createEdge(fromId, toId)
    }

    @Test
    fun `createEdge should throw DuplicateResourceException`() {
        // Arrange
        val requestId = "test-request-id"
        val correlationId = "test-correlation-id"
        val fromId = 1
        val toId = 2
        val request = EdgeDTO(fromId = fromId, toId = toId)
        val existingEdgeRecord = mock<EdgeRecord>()

        // Mock the service call to return an existing edge.
        whenever(nodeManagerService.findEdge(fromId, toId)).thenReturn(existingEdgeRecord)

        // Act & Assert
        assertThrows<DuplicateResourceException> {
            nodeManagerControllerApi.createEdge(requestId, correlationId, request)
        }

        // Verify that createEdge was never called.
        verify(nodeManagerService, times(1)).findEdge(fromId, toId)
        verify(nodeManagerService, never()).createEdge(any(), any())
    }

    @Test
    fun `removeEdge should return HTTP 204 No Content`() {
        // Arrange
        val requestId = "test-request-id"
        val correlationId = "test-correlation-id"
        val fromId = 1
        val toId = 2

        whenever(nodeManagerService.deleteEdge(fromId, toId)).thenReturn(1)
        val response: ResponseEntity<Void> = nodeManagerControllerApi.removeEdge(requestId, correlationId, fromId, toId)

        // Assert
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        // Verify that the service method was called.
        verify(nodeManagerService, times(1)).deleteEdge(fromId, toId)
    }

    @Test
    fun `getEdgeTree should return a NodeTreeDto on success`() {
        // Arrange
        val requestId = "test-request-id"
        val correlationId = "test-correlation-id"
        val nodeId = 1
        val expectedNodeTree = NodeTreeDTO(id = nodeId, children = mutableListOf())

        // Mock the service call to return a NodeTreeDto.
        whenever(nodeManagerService.getTreeByRootNode(nodeId)).thenReturn(expectedNodeTree)

        // Act
        val result: NodeTreeDTO = nodeManagerControllerApi.getEdgeTree(requestId, correlationId, nodeId)

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(nodeId, result.id)
        Assertions.assertEquals(expectedNodeTree, result)

        // Verify that the service method was called.
        verify(nodeManagerService, times(1)).getTreeByRootNode(nodeId)
    }
}