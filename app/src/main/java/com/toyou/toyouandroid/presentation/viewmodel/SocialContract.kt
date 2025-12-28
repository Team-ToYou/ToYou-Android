package com.toyou.toyouandroid.presentation.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.model.FriendListModel

data class SocialUiState(
    // Friends
    val friends: List<FriendListModel> = emptyList(),
    val isLoading: Boolean = false,

    // Character selection
    val selectedChar: Int = -1,
    val nextBtnEnabled: Boolean = false,

    // Question
    val questionDto: QuestionDto = QuestionDto(0, "", "", false, null),
    val selectedEmotion: Int? = null,
    val selectedEmotionMent: String = "",
    val optionList: List<String> = emptyList(),

    // Search
    val isFriend: String = "",
    val searchName: String = "",
    val searchFriendId: Long = 0L
) : UiState

sealed interface SocialEvent : UiEvent {
    // Friend request events
    data object FriendRequestCompleted : SocialEvent
    data object FriendRequestCanceled : SocialEvent
    data object FriendRequestRemoved : SocialEvent

    // Approval events
    data class ApprovalSuccess(val alarmId: Int, val position: Int) : SocialEvent
    data class ApprovalFailed(val alarmId: Int, val position: Int) : SocialEvent

    // Error events
    data class ShowError(val message: String) : SocialEvent
    data object TokenExpired : SocialEvent

    // Question events
    data object QuestionSent : SocialEvent
}

sealed interface SocialAction : UiAction {
    // Data loading
    data object LoadFriends : SocialAction
    data class SearchFriend(val name: String) : SocialAction

    // Character selection
    data class SelectCharacter(val position: Int) : SocialAction

    // Target friend
    data class SetTargetFriend(
        val friendName: String,
        val emotion: Int?,
        val ment: String?
    ) : SocialAction
    data class SetTypeFriend(val type: String) : SocialAction

    // Question management
    data class UpdateQuestionOptions(val options: List<String>) : SocialAction
    data object UpdateOption : SocialAction
    data object RemoveOptions : SocialAction
    data object RemoveContent : SocialAction
    data class SetAnonymous(val isAnonymous: Boolean) : SocialAction
    data class SendQuestion(val myName: String) : SocialAction

    // Friend requests
    data class SendFriendRequest(val friendId: Long, val myName: String) : SocialAction
    data class DeleteFriend(val friendName: String?, val friendId: Long?) : SocialAction
    data class ApproveNotice(
        val name: String,
        val myName: String,
        val alarmId: Int,
        val position: Int
    ) : SocialAction
    data class PatchApprove(val friendId: Long, val myName: String) : SocialAction

    // Reset
    data object ResetFriendRequest : SocialAction
    data object ResetFriendRequestCanceled : SocialAction
    data object ResetFriendRequestRemove : SocialAction
    data object ResetFriendState : SocialAction
    data object ResetQuestionData : SocialAction
    data object ResetApproveSuccess : SocialAction
}
