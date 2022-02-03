package com.paligot.conferences.backend.talks

data class TalkDb(
  val id: String = "",
  val title: String = "",
  val level: String? = null,
  val abstract: String = "",
  val category: String = "",
  val format: String = "",
  val speakerIds: List<String> = emptyList(),
  val openFeedback: String? = null
)
