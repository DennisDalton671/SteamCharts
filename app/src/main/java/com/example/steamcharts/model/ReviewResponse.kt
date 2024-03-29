package com.example.steamcharts.model


    data class ReviewResponse(
    val success: Int,
    val query_summary: ReviewSummary
    )

    data class ReviewSummary(
        val review_score: Int
)

