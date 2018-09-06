package com.jazart.symphony

import java.util.*

sealed class Result {
    object Loading : Result()
    data class Success<T>(val data: T) : Result()
    data class Failure(val e: Throwable, val message: Error) : Result()
}

enum class Error(var message: String = "") {
    NOT_FOUND("Entry not found"),
    UNKNOWN_ERROR()
    // more enum error cases here
}

/**
 * Definition for a binary tree node.
 * class TreeNode(var `val`: Int = 0) {
 *     var left: TreeNode? = null
 *     var right: TreeNode? = null
 * }
 */

  class TreeNode(var `val`: Int = 0) {
      var left: TreeNode? = null
      var right: TreeNode? = null
  }

class Solution {
    fun kthSmallest(root: TreeNode?, k: Int): Int {
        var count = k
        var queue = ArrayDeque<TreeNode?>()

        queue.addFirst(root)
        var curr = root
        while(!queue.isEmpty() || curr != null) {
            var curr = queue.removeFirst()
            while(curr != null) {
                queue.addFirst(curr)
                curr = curr.left
            }
            var left = queue.removeFirst()
            if(count++ == k) return left?.`val`!!
            curr = curr?.right
            var list = mutableListOf(1)
        }
        return 0
    }
}