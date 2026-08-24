package com.example.contactcallblocker

import android.telecom.Call
import android.telecom.CallScreeningService

class ContactCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val direction = when (callDetails.callDirection) {
            Call.Details.DIRECTION_INCOMING -> CallDirection.INCOMING
            Call.Details.DIRECTION_OUTGOING -> CallDirection.OUTGOING
            else -> CallDirection.UNKNOWN
        }

        if (screeningAction(direction) != ScreeningAction.BLOCK) return

        // Omitting READ_CONTACTS makes Android withhold contact calls from this service.
        // Granting it would change that platform filtering behavior.
        val response = CallScreeningService.CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(false)
            .build()
        respondToCall(callDetails, response)
    }
}
