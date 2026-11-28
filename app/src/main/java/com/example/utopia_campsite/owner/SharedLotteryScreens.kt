/**
 * Utopia 02.04.02: owner screens for hosting a shared lottery, inviting other owners to it,
 * and answering an invite
 *
 * Each screen is split in two: a small wrapper that loads from and saves to the firebase,
 * and a Content composable that only shows data and reports clicks, so UI tests can
 * show the Content with fake data and stub callbacks
 */

package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

// ---------------------------------------------------------------------------
// Host a shared lottery
// ---------------------------------------------------------------------------

/**
 * Loads the signed-in owner's campsites and saves the new shared lottery to the firebase
 *
 * @param onBack: called when the owner presses Back
 * @param onCreated: called with the new lottery's ID once it is saved
 */
@Composable
fun HostSharedLotteryScreen(onBack: () -> Unit, onCreated: (String) -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val repository = remember { SharedLotteryRepository() }
    var campsites by remember { mutableStateOf<List<Campsites>?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        if (uid == null) {
            loadError = "Please sign in as an owner."
        } else {
            CampsiteRepository().getCampsites(
                // only the owner's own physical campsites can start a lottery
                onSuccess = { all -> campsites = all.filter { it.ownerId == uid && !it.isSharedLottery() } },
                onFailure = { loadError = "Could not load campsites: ${it.message}" }
            )
        }
    }

    HostSharedLotteryContent(
        campsites = campsites,
        loadError = loadError,
        onBack = onBack,
        onCreate = { ids, start, end, count, finished ->
            repository.createSharedLottery(
                ids, start, end, count,
                onSuccess = { id -> finished(null); onCreated(id) },
                onFailure = { finished(it.message ?: "Could not create the lottery.") }
            )
        }
    )
} //end HostSharedLotteryScreen

/**
 * Lets an owner pick one or more of their campsites and the registration period for a shared lottery
 *
 * @param campsites: the owner's campsites, or null while loading
 * @param loadError: an error from loading, or null
 * @param onBack: called when the owner presses Back
 * @param onCreate: called with the chosen campsite IDs, registration start and end, winner count,
 *                  and a callback to call with null on success or an error message
 */
@Composable
fun HostSharedLotteryContent(
    campsites: List<Campsites>?,
    loadError: String?,
    onBack: () -> Unit,
    onCreate: (List<String>, String, String, Int, (String?) -> Unit) -> Unit
) {
    var selected by remember { mutableStateOf(setOf<String>()) }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var winners by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack, enabled = !saving) { Text("Back") }
        Text("Host a lottery with other owners", style = MaterialTheme.typography.headlineSmall)
        Text("Pick at least one of your campsites. You can invite other owners after it is created. " +
                "Their campsites must have the same booking period and maximum occupants.")
        loadError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (campsites == null) {
            if (loadError == null) CircularProgressIndicator()
        } else {
            if (campsites.isEmpty()) Text("You have no campsites yet.")
            LazyColumn(Modifier.weight(1f, fill = false)) {
                items(campsites, key = { it.id }) { campsite ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = campsite.id in selected,
                            enabled = !saving,
                            onCheckedChange = { checked ->
                                selected = if (checked) selected + campsite.id else selected - campsite.id
                            },
                            modifier = Modifier.testTag("select_${campsite.id}")
                        )
                        Text("${campsite.name}\nBooking: ${campsite.bookingDate}\nCapacity: ${campsite.maximumOccupants}")
                    }
                }
            }
        }
        OutlinedTextField(start, { start = it }, label = { Text("Registration start DD/MM/YYYY") }, enabled = !saving)
        OutlinedTextField(end, { end = it }, label = { Text("Registration end DD/MM/YYYY") }, enabled = !saving)
        OutlinedTextField(winners, { winners = it }, label = { Text("Number of winners") }, enabled = !saving)
        message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(enabled = !saving, onClick = {
            val count = winners.trim().toIntOrNull()
            if (selected.isEmpty() || count == null || count <= 0 ||
                !MultipleLotteryDates.isValidPeriod(start.trim(), end.trim())
            ) {
                message = "Select at least one campsite and enter valid dates and a positive winner count."
            } else {
                saving = true
                message = null
                onCreate(selected.toList(), start.trim(), end.trim(), count) { error ->
                    saving = false
                    message = error
                }
            }
        }) { Text(if (saving) "Creating..." else "Create lottery") }
    }
} //end HostSharedLotteryContent

// ---------------------------------------------------------------------------
// Invite owners
// ---------------------------------------------------------------------------

/**
 * Loads the lottery and every owner, and saves invites to the firebase
 *
 * @param lotteryId: the shared lottery's document ID
 * @param onBack: called when the host presses Back
 */
@Composable
fun InviteOwnersScreen(lotteryId: String, onBack: () -> Unit) {
    val repository = remember { SharedLotteryRepository() }
    var lottery by remember { mutableStateOf<Campsites?>(null) }
    var owners by remember { mutableStateOf<List<Owner>?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lotteryId) {
        repository.getLottery(lotteryId, onSuccess = { lottery = it }, onFailure = { loadError = it.message })
        repository.getAllOwners(onSuccess = { owners = it }, onFailure = { loadError = "Could not load owners: ${it.message}" })
    }

    InviteOwnersContent(
        lottery = lottery,
        owners = owners,
        loadError = loadError,
        onBack = onBack,
        onInvite = { ownerId, finished ->
            repository.inviteOwner(
                lotteryId, ownerId,
                onSuccess = { finished(null) },
                onFailure = { finished(it.message ?: "Could not send the invite.") }
            )
        }
    )
} //end InviteOwnersScreen

/**
 * Lets the host invite other owners to add a campsite to their shared lottery
 * Shows up to 10 owners, and the host can search the rest by name or email
 *
 * @param lottery: the shared lottery, or null while loading
 * @param owners: every owner, or null while loading
 * @param loadError: an error from loading, or null
 * @param onBack: called when the host presses Back
 * @param onInvite: called with the owner's ID and a callback to call with null on success or an error message
 */
@Composable
fun InviteOwnersContent(
    lottery: Campsites?,
    owners: List<Owner>?,
    loadError: String?,
    onBack: () -> Unit,
    onInvite: (String, (String?) -> Unit) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var invitedNow by remember { mutableStateOf(setOf<String>()) } // invited while this screen is open
    var sending by remember { mutableStateOf<String?>(null) }    // owner currently being invited
    var message by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack) { Text("Back") }
        Text("Invite owners", style = MaterialTheme.typography.headlineSmall)
        lottery?.let { Text("${it.name}\nBooking: ${it.bookingDate.replace("-", " - ")}\nCapacity: ${it.maximumOccupants}") }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search owners by name or email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("ownerSearch")
        )
        (loadError ?: message)?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        if (lottery == null || owners == null) {
            if (loadError == null) CircularProgressIndicator()
        } else {
            // hide the host and owners already in the lottery; invited owners stay listed as "Invited"
            val shown = SharedLotteryRules.filterOwners(owners, query, lottery.memberOwnerIds + lottery.ownerId)
            if (shown.isEmpty()) Text("No owners found.")
            LazyColumn {
                items(shown, key = { it.deviceID }) { owner ->
                    val invited = owner.deviceID in lottery.invitedOwnerIds || owner.deviceID in invitedNow
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${owner.firstName} ${owner.lastName}\n${owner.email}", Modifier.weight(1f))
                        Button(
                            enabled = !invited && sending == null,
                            modifier = Modifier.testTag("invite_${owner.deviceID}"),
                            onClick = {
                                sending = owner.deviceID
                                message = null
                                onInvite(owner.deviceID) { error ->
                                    sending = null
                                    if (error == null) invitedNow = invitedNow + owner.deviceID else message = error
                                }
                            }
                        ) {
                            Text(when {
                                invited -> "Invited"
                                sending == owner.deviceID -> "Inviting..."
                                else -> "Invite"
                            })
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
} //end InviteOwnersContent

// ---------------------------------------------------------------------------
// Answer an invite
// ---------------------------------------------------------------------------

/**
 * Loads the lottery and the invited owner's campsites, and saves the owner's answer to the firebase
 *
 * @param lotteryId: the shared lottery's document ID
 * @param onBack: called when the owner presses Back
 * @param onDone: called once the owner accepted or declined
 */
@Composable
fun AcceptInviteScreen(lotteryId: String, onBack: () -> Unit, onDone: () -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val repository = remember { SharedLotteryRepository() }
    var lottery by remember { mutableStateOf<Campsites?>(null) }
    var myCampsites by remember { mutableStateOf<List<Campsites>?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lotteryId, uid) {
        if (uid == null) {
            loadError = "Please sign in as an owner."
        } else {
            CampsiteRepository().getCampsites(
                onSuccess = { all ->
                    lottery = all.find { it.id == lotteryId && it.isSharedLottery() }
                    myCampsites = all.filter { it.ownerId == uid && !it.isSharedLottery() }
                    if (lottery == null) loadError = "Lottery not found."
                },
                onFailure = { loadError = "Could not load the lottery: ${it.message}" }
            )
        }
    }

    AcceptInviteContent(
        lottery = lottery,
        myCampsites = myCampsites,
        ownerId = uid.orEmpty(),
        today = remember { todayText() },
        loadError = loadError,
        onBack = onBack,
        onAccept = { campsiteId, finished ->
            repository.acceptInvite(
                lotteryId, campsiteId,
                onSuccess = { finished(null); onDone() },
                onFailure = { finished(it.message ?: "Could not join the lottery.") }
            )
        },
        onDecline = { finished ->
            repository.declineInvite(
                lotteryId,
                onSuccess = { finished(null); onDone() },
                onFailure = { finished(it.message ?: "Could not decline the invite.") }
            )
        }
    )
} //end AcceptInviteScreen

/**
 * Lets an invited owner add one of their campsites to a shared lottery, or turn the invite down
 * Campsites that cannot join show the reason (e.g. a different booking period)
 * The invited owner cannot change the lottery's registration or booking period
 *
 * @param lottery: the shared lottery, or null while loading
 * @param myCampsites: the invited owner's campsites, or null while loading
 * @param ownerId: the invited owner
 * @param today: today's date as DD/MM/YYYY
 * @param loadError: an error from loading, or null
 * @param onBack: called when the owner presses Back
 * @param onAccept: called with the chosen campsite's ID and a callback to call with null on success or an error message
 * @param onDecline: called with a callback to call with null on success or an error message
 */
@Composable
fun AcceptInviteContent(
    lottery: Campsites?,
    myCampsites: List<Campsites>?,
    ownerId: String,
    today: String,
    loadError: String?,
    onBack: () -> Unit,
    onAccept: (String, (String?) -> Unit) -> Unit,
    onDecline: ((String?) -> Unit) -> Unit
) {
    var saving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val finished: (String?) -> Unit = { error -> saving = false; message = error }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack, enabled = !saving) { Text("Back") }
        Text("Lottery invitation", style = MaterialTheme.typography.headlineSmall)
        (loadError ?: message)?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        if (lottery == null || myCampsites == null) {
            if (loadError == null) CircularProgressIndicator()
        } else {
            Text(lottery.name, style = MaterialTheme.typography.titleLarge)
            Text("Registration period: ${lottery.registrationDate.replace("-", " - ")}")
            Text("Booking period: ${lottery.bookingDate.replace("-", " - ")}")
            Text("Maximum occupants: ${lottery.maximumOccupants}")
            Text("Campsites already in this lottery: ${lottery.campsiteIds.size}")
            Text("Choose one of your campsites to add:", style = MaterialTheme.typography.titleMedium)

            if (myCampsites.isEmpty()) Text("You have no campsites yet.")
            LazyColumn(Modifier.weight(1f, fill = false)) {
                items(myCampsites, key = { it.id }) { campsite ->
                    val problem = SharedLotteryRules.canJoin(lottery, campsite, ownerId, today)
                    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text("${campsite.name}\nBooking: ${campsite.bookingDate}\nCapacity: ${campsite.maximumOccupants}")
                        if (problem != null) {
                            Text(problem, color = MaterialTheme.colorScheme.error)
                        } else {
                            Button(
                                enabled = !saving,
                                modifier = Modifier.testTag("add_${campsite.id}"),
                                onClick = {
                                    saving = true
                                    message = null
                                    onAccept(campsite.id, finished)
                                }
                            ) { Text("Add this campsite") }
                        }
                    }
                    HorizontalDivider()
                }
            }

            OutlinedButton(enabled = !saving, onClick = {
                saving = true
                message = null
                onDecline(finished)
            }) { Text("Decline invite") }
        }
    }
} //end AcceptInviteContent
