package com.prewave.nodemanager.exception

class ResourceNotFoundException(message: String) : RuntimeException(message)
class InvalidOperationException(message: String) : RuntimeException(message)
class DuplicateResourceException(message: String) : RuntimeException(message)