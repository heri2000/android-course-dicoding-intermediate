package com.rinjaninet.storyapp.addstory

import com.google.gson.annotations.SerializedName

data class AddStoryResponse(
	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

