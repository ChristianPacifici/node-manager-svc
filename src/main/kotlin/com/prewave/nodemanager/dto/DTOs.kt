package com.prewave.nodemanager.dto

data class EdgeDTO(
    val fromId: Int,
    val toId: Int
)

data class EdgeResponse(
    val fromId: Int?,
    val toId: Int?
)

data class NodeTreeDTO(
    val id: Int?,
    val children: MutableList<NodeTreeDTO> = mutableListOf()
)