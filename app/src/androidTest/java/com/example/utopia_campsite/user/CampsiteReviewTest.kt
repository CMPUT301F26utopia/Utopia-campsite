package com.example.utopia_campsite.user
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.util.CampsiteDetailsContent
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test



class CampsiteReviewTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val testCampsite = Campsites(
        id = "camp_001",
        name = "Banff Mountain Campground",
        photoUrl = null,
        address = "Banff, Alberta, Canada",
        description = "Mountain campsite"
    )

    private fun setContent(
        campsite: Campsites = testCampsite,
        onBack: (() -> Unit)? = null,
        onPostReview: ((String, () -> Unit, (Exception) -> Unit) -> Unit)? = { _, _, _ -> }
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                CampsiteDetailsContent(
                    campsite = campsite,
                    onBack = onBack,
                    onPostReview = onPostReview
                )
            }
        }
    } //end setContent
    private fun scrollTo(text: String) {
        composeTestRule.onNode(hasScrollToNodeAction()).performScrollToNode(hasText(text))
    } //end scrollTo

    @Test
    fun testNoReviewsShowsEmptyMessage() {
        setContent()
        scrollTo("No reviews yet.")
        composeTestRule.onNodeWithText("No reviews yet.").assertIsDisplayed()
    }

    @Test
    fun testReviewShowsNameAndTextSeparately() {
        setContent(campsite = testCampsite.copy(reviews = listOf("John Doe: Great spot!")))
        scrollTo("Great spot!")
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Great spot!").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe: Great spot!").assertDoesNotExist()

    }

    @Test
    fun testReviewWithColonsOnlySplitsAtFirstColon() {
        setContent(campsite = testCampsite.copy(reviews = listOf("John Doe: Note: bring bug spray. Rating: 10/10")))
        scrollTo("Note: bring bug spray. Rating: 10/10")
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Note: bring bug spray. Rating: 10/10").assertIsDisplayed()
    }

    @Test
    fun testPostButtonDisabledUntilReviewTyped() {
        setContent()
        scrollTo("Post Review")
        composeTestRule.onNodeWithText("Post Review").assertIsNotEnabled()

        // only spaces doesn't count as a review
        composeTestRule.onNodeWithText("Write a review").performTextInput("   ")
        composeTestRule.onNodeWithText("Post Review").assertIsNotEnabled()

        composeTestRule.onNodeWithText("Write a review").performTextInput("Nice")
        composeTestRule.onNodeWithText("Post Review").assertIsEnabled()
    }

    @Test
    fun testPostingSendsTrimmedTextAndClearsBoxOnSuccess() {
        var postedText: String? = null
        var onSuccessCallback: (() -> Unit)? = null
        setContent(onPostReview = { text, onSuccess, _ ->
            postedText = text
            onSuccessCallback = onSuccess
        })

        scrollTo("Post Review")
        composeTestRule.onNodeWithText("Write a review").performTextInput("  Great spot!  ")
        composeTestRule.onNodeWithText("Post Review").performClick()

        // only the text is sent, the name is added by the repository
        assertEquals("Great spot!", postedText)

        // pretend firebase saved it
        composeTestRule.runOnIdle { onSuccessCallback!!.invoke() }
        composeTestRule.onNodeWithText("Great spot!", substring = true).assertDoesNotExist()
    }

    @Test
    fun testPostButtonDisabledWhilePosting() {
        setContent(onPostReview = { _, _, _ -> })
        scrollTo("Post Review")
        composeTestRule.onNodeWithText("Write a review").performTextInput("Great spot!")
        composeTestRule.onNodeWithText("Post Review").performClick()
        composeTestRule.onNodeWithText("Post Review").assertIsNotEnabled()
    }
}