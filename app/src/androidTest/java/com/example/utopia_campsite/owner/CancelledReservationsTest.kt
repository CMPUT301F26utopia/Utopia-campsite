package com.example.utopia_campsite.owner

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.utopia_campsite.user.Reservation
import com.example.utopia_campsite.user.User
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CancelledReservationsTest {

    @Test
    fun getCancelledReservations_fetchesSuccessfully() {
        val owner = Owner(
            deviceID = "testOwner",
            postedCampsitesId = mutableListOf("testCampsiteId")
        )

        val latch = CountDownLatch(1)
        var error: Exception? = null

        OwnerRepository().getCancelledReservations(
            owner = owner,
            onSuccess = {
                latch.countDown()
            },
            onFailure = {
                error = it
                latch.countDown()
            }
        )
        assertTrue(latch.await(15, TimeUnit.SECONDS))
        assertNull(error)
    }
}